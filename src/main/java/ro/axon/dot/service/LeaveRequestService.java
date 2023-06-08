package ro.axon.dot.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ro.axon.dot.domain.entity.EmployeeEty;
import ro.axon.dot.domain.entity.LeaveRequestEty;
import ro.axon.dot.domain.repository.EmployeeRepository;
import ro.axon.dot.domain.repository.LeaveRequestRepository;
import ro.axon.dot.domain.repository.TeamRepository;
import ro.axon.dot.enums.LeaveRequestStatus;
import ro.axon.dot.enums.LeaveRequestType;
import ro.axon.dot.exceptions.BusinessErrorCode;
import ro.axon.dot.exceptions.BusinessException;
import ro.axon.dot.exceptions.BusinessException.BusinessExceptionElement;
import ro.axon.dot.mapper.LeaveRequestMapper;
import ro.axon.dot.model.response.LeaveRequestDetailsList;
import ro.axon.dot.model.response.LeaveRequestDetailsListItem;
import ro.axon.dot.model.response.VacationResponse;
import ro.axon.dot.model.response.VacationResponse.VacationResponseItem;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {

  private final LeaveRequestRepository leaveRequestRepository;
  private final EmployeeRepository employeeRepository;
  private final SecurityAccessTokenHandler securityAccessTokenHandler;
  private final LeaveRequestHandler leaveRequestHandler;
  private final TeamRepository teamRepository;

  private static final BiPredicate<LeaveRequestEty, LeaveRequestFilter> filter = (leaveRequest, filters) ->
      (filters.getStatus() == null || filters.getStatus().equals(leaveRequest.getStatus().name()))
          &&
          (filters.getSearch() == null || (
              leaveRequest.getEmployee().getFirstName().toUpperCase()
                  .contains(filters.getSearch().toUpperCase())
                  || leaveRequest.getEmployee().getLastName().toUpperCase()
                  .contains(filters.getSearch().toUpperCase()))) &&
          (filters.getType() == null || leaveRequest.getType().name().equals(filters.getType())) &&
          (filters.getStartDate() == null || !leaveRequest.getEndDate()
              .isBefore(filters.getStartDate())) &&
          (filters.getEndDate() == null || !leaveRequest.getStartDate()
              .isAfter(filters.getEndDate()));

  @Transactional
  public LeaveRequestDetailsList getLeaveRequestsDetails(String status, String search, String type,
      LocalDate startDate, LocalDate endDate) {
    var leaveRequestsList = new LeaveRequestDetailsList();
    List<LeaveRequestEty> leaveRequests;
    LeaveRequestFilter leaveRequestFilter = new LeaveRequestFilter(status, search, type, startDate,
        endDate);
    Comparator<LeaveRequestDetailsListItem> compareByStatus =
        Comparator.comparing(
            leaveRequestDetailsListItem -> leaveRequestDetailsListItem.getStatus().getPriority());
    Comparator<LeaveRequestDetailsListItem> compareByCrtTms = Comparator.comparing(
        LeaveRequestDetailsListItem::getCrtTms);

    var loggedUserId = securityAccessTokenHandler.getEmployeeIdFromAuthenticationToken();
    var loggedUser = employeeRepository.findEmployeeEtyById(loggedUserId).orElseThrow(
        () -> new BusinessException(Collections.singletonList(
            BusinessExceptionElement.builder().errorCode(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
                .build())));
    var role = loggedUser.getRole();
    if (role.equals("TEAM_LEAD")) {
      leaveRequests = getLeaveRequestsDetailsTeamLead();
    } else {
      leaveRequests = leaveRequestRepository.findAll();
    }
    var leaveRequestStream = leaveRequests.stream();
    leaveRequestsList.setItems(
        leaveRequestStream
            .filter(leaveRequestEty -> filter.test(leaveRequestEty, leaveRequestFilter))
            .map(LeaveRequestMapper.INSTANCE::mapEtyToLeaveRequestDetailsListItem)
            .sorted(compareByStatus.thenComparing(compareByCrtTms))
            .collect(Collectors.toList()));

    return leaveRequestsList;
  }

  @Transactional
  public VacationResponse getVacationRequestsByPeriod(String team, LocalDate startDate,
      LocalDate endDate) {

    List<EmployeeEty> employees;

    if (team == null || team.isEmpty()) {
      employees = employeeRepository.findAll();
    } else {
      var teamEty = teamRepository.findByNameIgnoreCase(team)
          .orElseThrow(() -> new BusinessException(Collections.singletonList(
              BusinessExceptionElement.builder()
                  .errorCode(BusinessErrorCode.TEAM_NOT_FOUND)
                  .build())));
      employees = employeeRepository.findAllByTeam(teamEty);
    }

    if (startDate.isEqual(endDate) || startDate.isAfter(endDate)) {
      throw new BusinessException(Collections.singletonList(BusinessExceptionElement.builder()
          .errorCode(BusinessErrorCode.INVALID_DATE_PROVIDED)
          .build()));
    }

    var lists = employees.stream()
        .map(employeeEty -> createVacationResponseItem(employeeEty, startDate,
            endDate))
        .collect(Collectors.toList());

    return new VacationResponse(lists);
  }

  private VacationResponseItem createVacationResponseItem(EmployeeEty employeeEty,
      LocalDate startDate, LocalDate endDate) {
    var leaveRequests = employeeEty.getLeaveRequestEties().stream()
        .filter(leaveRequestEty -> leaveRequestEty.getStatus() == LeaveRequestStatus.APPROVED
            && !leaveRequestEty.getEndDate().isBefore(startDate)
            && !leaveRequestEty.getStartDate().isAfter(endDate))
        .collect(Collectors.toList());

    var vacationDays = leaveRequests.stream()
        .filter(leaveRequestEty -> leaveRequestEty.getType() == LeaveRequestType.VACATION)
        .mapToInt(request -> sumLeaveRequestDaysBetweenDates(request, startDate, endDate))
        .sum();

    var medicalDays = leaveRequests.stream()
        .filter(leaveRequestEty -> leaveRequestEty.getType() == LeaveRequestType.MEDICAL)
        .mapToInt(request -> sumLeaveRequestDaysBetweenDates(request, startDate, endDate))
        .sum();

    return VacationResponseItem.builder()
        .firstName(employeeEty.getFirstName())
        .lastName(employeeEty.getLastName())
        .noOfVacationDays(vacationDays)
        .noOfMedicalDays(medicalDays)
        .leaveRequests(leaveRequests.stream()
            .map(LeaveRequestMapper.INSTANCE::mapEtyToResponseDto)
            .collect(Collectors.toList()))
        .build();
  }

  private Integer sumLeaveRequestDaysBetweenDates(LeaveRequestEty leaveRequest, LocalDate startDate,
      LocalDate endDate) {
    if (!leaveRequest.getStartDate().isBefore(startDate) && !leaveRequest.getEndDate()
        .isAfter(endDate)) {
      return leaveRequest.getNoOfDays();
    }
    if (leaveRequest.getEndDate().isAfter(endDate)) {
      return leaveRequestHandler.getDaysOffFromAPeriod(leaveRequest.getStartDate(), endDate);
    }
    if (leaveRequest.getStartDate().isBefore(startDate)) {
      return leaveRequestHandler.getDaysOffFromAPeriod(startDate, leaveRequest.getEndDate());
    }
    return 0;
  }

  private List<LeaveRequestEty> getLeaveRequestsDetailsTeamLead() {
    List<LeaveRequestEty> leaveRequests = new ArrayList<>();
    for (EmployeeEty employee : employeeRepository.getOne(
        securityAccessTokenHandler.getEmployeeIdFromAuthenticationToken()).getTeam().getEmployees()) {
      leaveRequests.addAll(employee.getLeaveRequestEties());
    }
    return leaveRequests;
  }

  @AllArgsConstructor
  @Getter
  @Setter
  private static class LeaveRequestFilter {

    private String status;
    private String search;
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
  }

}