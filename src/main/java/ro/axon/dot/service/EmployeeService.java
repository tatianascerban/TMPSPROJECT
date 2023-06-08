package ro.axon.dot.service;

import static ro.axon.dot.domain.entity.enums.DaysOffType.INCREASE;
import static ro.axon.dot.enums.LeaveRequestStatus.PENDING;
import static ro.axon.dot.enums.LeaveRequestStatus.REJECTED;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.axon.dot.domain.entity.DaysOffEty;
import ro.axon.dot.domain.entity.DaysOffHistoryEty;
import ro.axon.dot.domain.entity.EmployeeEty;
import ro.axon.dot.domain.entity.LeaveRequestEty;
import ro.axon.dot.domain.entity.TeamEty;
import ro.axon.dot.domain.entity.enums.DaysOffType;
import ro.axon.dot.domain.entity.enums.EmployeeStatus;
import ro.axon.dot.domain.repository.DaysOffRepository;
import ro.axon.dot.domain.repository.EmployeeRepository;
import ro.axon.dot.domain.repository.LeaveRequestRepository;
import ro.axon.dot.domain.repository.TeamRepository;
import ro.axon.dot.enums.LeaveRequestReviewType;
import ro.axon.dot.enums.LeaveRequestStatus;
import ro.axon.dot.enums.LeaveRequestType;
import ro.axon.dot.exceptions.BusinessErrorCode;
import ro.axon.dot.exceptions.BusinessException;
import ro.axon.dot.exceptions.BusinessException.BusinessExceptionElement;
import ro.axon.dot.mapper.EmployeeMapper;
import ro.axon.dot.mapper.LeaveRequestMapper;
import ro.axon.dot.model.request.EmployeeRequestDTO;
import ro.axon.dot.model.request.EmployeeUpdateRequest;
import ro.axon.dot.model.request.EmployeesDayOffUpdateRequest;
import ro.axon.dot.model.request.LeaveRequestCreationRequest;
import ro.axon.dot.model.request.LeaveRequestReview;
import ro.axon.dot.model.request.LeaveRequestUpdate;
import ro.axon.dot.model.response.EmployeeDetailList;
import ro.axon.dot.model.response.EmployeeRemainingDaysOfCurrentYear;
import ro.axon.dot.model.response.LeaveRequestDetailsList;

@Service
@RequiredArgsConstructor
public class EmployeeService {

  private static final String INIT_DAYS_OFF_DESCRIPTION = "Initial number of days off for the current year";

  private final EmployeeRepository employeeRepository;

  private final DaysOffRepository daysOffRepository;

  private final TeamRepository teamRepository;

  private final Clock clock;

  private final SecurityAccessTokenHandler securityAccessTokenHandler;

  private final EmployeeHandler employeeHandler;

  private final LeaveRequestRepository leaveRequestRepository;

  private final LeaveRequestHandler leaveRequestHandler;

  @Transactional(readOnly = true)
  public EmployeeDetailList getEmployeeDetails(String name) {
    var employeeDetailList = new EmployeeDetailList();
    var employeeDetailStream = employeeRepository.findAll().stream();
    Comparator<EmployeeEty> compareByStatus = Comparator.comparing(
        employeeEty -> employeeEty.getStatus().getPriority());
    Comparator<EmployeeEty> compareByLastName = Comparator.comparing(EmployeeEty::getLastName);
    if (name != null) {
      employeeDetailStream = employeeDetailStream.filter(employeeEty ->
          employeeEty.getFirstName().toUpperCase().contains(name.trim().toUpperCase()) ||
              employeeEty.getLastName().toUpperCase().contains(name.trim().toUpperCase()));
    }

    var currentYear = getCurrentYear(clock.instant());
    employeeDetailList.setItems(
        employeeDetailStream.sorted(compareByStatus.thenComparing(compareByLastName))
            .map(employeeEty -> {
              var employeeDetailListItem = EmployeeMapper.INSTANCE.mapEtyToEmployeeDetailListItem(
                  employeeEty);
              employeeDetailListItem.setTotalVacationDays(
                  employeeHandler.getEmployeeYearTotalDaysOff(
                      employeeEty.getDaysOffEties(), currentYear));
              return employeeDetailListItem;
            }).collect(Collectors.toList()));
    return employeeDetailList;
  }

  @Transactional
  public void updateEmployee(String employeeId, EmployeeUpdateRequest employeeUpdateRequest) {
    var employeeEty = getEmployeeById(employeeId);

    if (employeeUpdateRequest.getV() < employeeEty.getV()) {
      throw new BusinessException(List.of(BusinessExceptionElement.builder()
          .errorCode(BusinessErrorCode.EMPLOYEE_UPDATE_VERSION_CONFLICT)
          .build()));
    }

    employeeEty.setMdfUsr(
        securityAccessTokenHandler.getEmployeeIdFromAuthenticationToken());
    employeeEty.setMdfTms(clock.instant());
    employeeEty.setFirstName(employeeUpdateRequest.getFirstName());
    employeeEty.setLastName(employeeUpdateRequest.getLastName());
    employeeEty.setUsername(employeeUpdateRequest.getUsername());
    employeeEty.setEmail(employeeUpdateRequest.getEmail());
    employeeEty.setRole(employeeUpdateRequest.getRole());

    if (employeeUpdateRequest.getTeamId() != null) {
      employeeEty.setTeam(handleEmployeeTeam(employeeUpdateRequest.getTeamId()));
    }
    employeeRepository.save(employeeEty);

  }

  @Transactional
  public void updateEmployeesDaysOff(EmployeesDayOffUpdateRequest dayOffUpdateRequest) {
    employeeRepository.findAllById(dayOffUpdateRequest.getEmployeeIds()).stream()
        .map(this::getOrCreateDaysOffEntity)
        .map(doe -> this.updateDaysOffEntity(doe, dayOffUpdateRequest))
        .forEach(daysOffRepository::save);
  }

  @Transactional
  public void createEmployee(EmployeeRequestDTO employeeRequest) {
    var employeeEty = createEmployeeFromDTO(employeeRequest);
    var loggedEmployeeId = securityAccessTokenHandler.getEmployeeIdFromAuthenticationToken();
    var password = employeeHandler.generatePassword(employeeRequest.getUsername());

    var now = clock.instant();
    employeeEty.setPassword(password);
    employeeEty.setCrtUsr(loggedEmployeeId);
    employeeEty.setCrtTms(now);
    employeeEty.setMdfUsr(loggedEmployeeId);
    employeeEty.setMdfTms(now);
    employeeEty.setStatus(EmployeeStatus.ACTIVE);

    addDaysOffEtyToEmployeeDaysOffList(employeeEty, employeeRequest.getNoDaysOff());
    employeeRepository.save(employeeEty);

  }

  @Transactional
  public void inactivateEmployee(String employeeId) {
    var employeeEty = getEmployeeById(employeeId);
    employeeEty.setStatus(EmployeeStatus.INACTIVE);
    employeeEty.setMdfTms(clock.instant());
    employeeEty.setMdfUsr(
        securityAccessTokenHandler.getEmployeeIdFromAuthenticationToken());
    employeeRepository.save(employeeEty);
  }

  public void reviewLeaveRequest(String employeeId, Long requestId,
      LeaveRequestReview leaveRequestReview) {

    getEmployeeById(employeeId);

    var leaveRequestEty = leaveRequestRepository.findById(requestId).orElseThrow(
        () -> new BusinessException(Collections.singletonList(
            BusinessExceptionElement.builder().errorCode(BusinessErrorCode.LEAVE_REQUEST_NOT_FOUND)
                .build())));

    validateLeaveRequest(leaveRequestReview, leaveRequestEty);

    if (leaveRequestReview.getType() == LeaveRequestReviewType.REJECTION) {
      if (leaveRequestReview.getRejectionReason().isEmpty()) {
        throw new BusinessException(Collections.singletonList(
            BusinessExceptionElement.builder()
                .errorCode(BusinessErrorCode.LEAVE_REQUEST_REJECTED_WITHOUT_REJECTION_REASON)
                .build()));
      }
      leaveRequestEty.setRejectReason(leaveRequestReview.getRejectionReason());
      leaveRequestEty.setStatus(LeaveRequestStatus.REJECTED);

    }
    if (leaveRequestReview.getType() == LeaveRequestReviewType.APPROVAL) {
      leaveRequestEty.setStatus(LeaveRequestStatus.APPROVED);
    }

    leaveRequestEty.setMdfTms(clock.instant());
    leaveRequestEty.setMdfUsr(securityAccessTokenHandler.getEmployeeIdFromAuthenticationToken());
    leaveRequestRepository.save(leaveRequestEty);

  }

  private static void validateLeaveRequest(LeaveRequestReview leaveRequestReview,
      LeaveRequestEty leaveRequestEty) {
    if (leaveRequestReview.getV() < leaveRequestEty.getV()) {
      throw new BusinessException(Collections.singletonList(
          BusinessExceptionElement.builder()
              .errorCode(BusinessErrorCode.LEAVE_REQUEST_VERSION_CONFLICT)
              .build()));
    }
    if (leaveRequestEty.getStatus() != LeaveRequestStatus.PENDING) {
      throw new BusinessException(Collections.singletonList(
          BusinessExceptionElement.builder()
              .errorCode(BusinessErrorCode.LEAVE_REQUEST_PATCH_NOT_ALLOWED)
              .build()));
    }

  }

  private int getCurrentYear(Instant instant) {
    return LocalDate.ofInstant(instant, ZoneId.systemDefault()).getYear();
  }

  private DaysOffHistoryEty createDaysOffHistory(EmployeesDayOffUpdateRequest dayOffUpdateRequest,
      DaysOffEty daysOffEntity) {

    return DaysOffHistoryEty.builder().numberOfDays(dayOffUpdateRequest.getNoDays())
        .type(dayOffUpdateRequest.getType()).description(dayOffUpdateRequest.getDescription())
        .daysOffEty(daysOffEntity)
        .userNameCreate(securityAccessTokenHandler.getEmployeeIdFromAuthenticationToken())
        .createdTime(clock.instant()).build();
  }

  private DaysOffEty updateDaysOffEntity(DaysOffEty daysOffEntity,
      EmployeesDayOffUpdateRequest request) {
    Integer daysLeft = computeDaysLeft(daysOffEntity, request);
    validateDaysLeft(daysLeft);
    daysOffEntity.setYear(LocalDate.ofInstant(clock.instant(), ZoneId.systemDefault()).getYear());
    daysOffEntity.setTotalDays(daysLeft);
    daysOffEntity.getDaysOffHistoryEties().add(createDaysOffHistory(request, daysOffEntity));
    return daysOffEntity;
  }

  private static void validateDaysLeft(Integer daysLeft) {
    if (daysLeft < 0) {
      throw new BusinessException(Collections.singletonList(BusinessExceptionElement.builder()
          .errorCode(BusinessErrorCode.NEGATIVE_DAYS_OFF_EXCEPTION).build()));
    }
  }

  private Integer computeDaysLeft(DaysOffEty daysOffEntity, EmployeesDayOffUpdateRequest request) {
    switch (request.getType()) {
      case INCREASE:
        return daysOffEntity.getTotalDays() + request.getNoDays();
      case DECREASE:
        return daysOffEntity.getTotalDays() - request.getNoDays();
      default:
        return daysOffEntity.getTotalDays();
    }
  }

  private DaysOffEty getOrCreateDaysOffEntity(EmployeeEty employee) {
    return daysOffRepository.findByEmployee(employee)
        .orElseGet(() -> createDaysOffEntity(employee));
  }

  private DaysOffEty createDaysOffEntity(EmployeeEty employee) {
    return DaysOffEty.builder().employee(employee).daysOffHistoryEties(new ArrayList<>())
        .totalDays(0).build();
  }

  private void addDaysOffEtyToEmployeeDaysOffList(
      EmployeeEty employeeEty,
      Integer noOfDays) {
    var daysOffEty = new DaysOffEty();
    daysOffEty.setYear(LocalDate.ofInstant(clock.instant(),
        ZoneId.systemDefault()).getYear());
    daysOffEty.setTotalDays(noOfDays);
    daysOffEty.setEmployee(employeeEty);
    daysOffEty.setDaysOffHistoryEties(List.of(
        createEmpYearlyDaysOffHistEty(INIT_DAYS_OFF_DESCRIPTION,
            noOfDays, INCREASE, daysOffEty)));
    employeeEty.getDaysOffEties().add(daysOffEty);
  }

  private DaysOffHistoryEty createEmpYearlyDaysOffHistEty(String desc, int noDays,
      DaysOffType type, DaysOffEty empYearlyDaysOffEty) {
    var daysOffHistoryEty = new DaysOffHistoryEty();
    daysOffHistoryEty.setDaysOffEty(empYearlyDaysOffEty);
    daysOffHistoryEty.setDescription(desc);
    daysOffHistoryEty.setCreatedTime(clock.instant());
    daysOffHistoryEty.setUserNameCreate(
        securityAccessTokenHandler.getEmployeeIdFromAuthenticationToken());
    daysOffHistoryEty.setType(type);
    daysOffHistoryEty.setNumberOfDays(noDays);
    return daysOffHistoryEty;
  }

  private EmployeeEty createEmployeeFromDTO(EmployeeRequestDTO employeeRequestDTO) {
    if (employeeRepository.existsEmployeeEtyByEmailIgnoreCase(employeeRequestDTO.getEmail())
        || employeeRepository.existsEmployeeEtyByUsernameIgnoreCase(
        employeeRequestDTO.getUsername())) {
      throw new BusinessException(Collections.singletonList(BusinessExceptionElement.builder()
          .errorCode(BusinessErrorCode.EMPLOYEE_ALREADY_EXISTING_EXCEPTION).build()));
    }

    var employeeEty = EmployeeMapper.INSTANCE.mapEmployeeDtoToEmployeeEntity(
        employeeRequestDTO);
    var team = handleEmployeeTeam(employeeRequestDTO.getTeamId());
    employeeEty.setTeam(team);

    return employeeEty;
  }

  private TeamEty handleEmployeeTeam(Long teamId) {
    return teamRepository.findById(teamId)
        .orElseThrow(() -> new BusinessException(Collections.singletonList(
            BusinessExceptionElement.builder()
                .errorCode(BusinessErrorCode.TEAM_NOT_FOUND)
                .build())));
  }

  private EmployeeEty getEmployeeById(String employeeId) {
    return employeeRepository
        .findById(employeeId)
        .orElseThrow(() -> new BusinessException(Collections.singletonList(
            BusinessExceptionElement.builder()
                .errorCode(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
                .build())));
  }

  @Transactional(readOnly = true)
  public LeaveRequestDetailsList getLeaveRequests(String employeeId, LocalDate startDate,
      LocalDate endDate) {
    var leaveRequestDetailsList = new LeaveRequestDetailsList();
    var employeeEty = employeeRepository.findById(employeeId).orElseThrow(() ->
        new BusinessException(List.of(
            BusinessExceptionElement.builder().errorCode(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
                .build())));
    var leaveRequestStream = employeeEty.getLeaveRequestEties().stream();
    if (startDate != null) {
      leaveRequestStream = leaveRequestStream.filter(
          leaveRequestEty -> leaveRequestEty.getEndDate().isAfter(startDate)
              && leaveRequestEty.getStartDate().isBefore(endDate));
    }
    leaveRequestDetailsList.setItems(leaveRequestStream
        .map(LeaveRequestMapper.INSTANCE::mapEtyToLeaveRequestDetailsListItem)
        .collect(Collectors.toList()));
    return leaveRequestDetailsList;
  }

  @Transactional
  public void createLeaveRequest(String employeeId, LeaveRequestCreationRequest leaveRequest) {
    var employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new BusinessException(List.of(BusinessExceptionElement.builder()
            .errorCode(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
            .build())));

    var newLeaveRequest = LeaveRequestMapper.INSTANCE.mapLeaveRequestCreationRequestToEty(
        leaveRequest);
    newLeaveRequest.setEmployee(employee);
    checkCreateLeaveRequest(newLeaveRequest);

    var endDate = newLeaveRequest.getEndDate();
    int noVacationDays = leaveRequestHandler.getDaysOffFromAPeriod(newLeaveRequest.getStartDate(),
        endDate);
    if (getRemainingDaysOff(employeeId).getRemainingDays() < noVacationDays) {
      throw new BusinessException(List.of(BusinessExceptionElement.builder().errorCode(
          BusinessErrorCode.LEAVE_REQUEST_CREATE_INVALID_NO_DAYS_OFF).build()));
    }

    var instant = clock.instant();
    newLeaveRequest.setCrtTms(instant);
    newLeaveRequest.setCrtUsr(employee.getId());
    newLeaveRequest.setMdfTms(instant);
    newLeaveRequest.setMdfUsr(employee.getId());
    newLeaveRequest.setStatus(LeaveRequestStatus.PENDING);
    newLeaveRequest.setNoOfDays(noVacationDays);
    newLeaveRequest.setEmployee(employee);
    employee.addLeaveRequest(newLeaveRequest);
    employeeRepository.save(employee);
  }

  private void checkCreateLeaveRequest(LeaveRequestEty leaveRequestEty) {
    var startDate = leaveRequestEty.getStartDate();
    var endDate = leaveRequestEty.getEndDate();

    if (startDate.getYear() != endDate.getYear()) {
      throw new BusinessException(List.of(BusinessExceptionElement.builder().errorCode(
          BusinessErrorCode.LEAVE_REQUEST_CREATE_DIFFERENT_YEARS)
          .build()));
    }
    var nextFinishDate = endDate;
    nextFinishDate.plusDays(1);

    if (nextFinishDate.isBefore(startDate)) {
      throw new BusinessException(List.of(BusinessExceptionElement.builder().errorCode(
          BusinessErrorCode.LEAVE_REQUEST_CREATE_INVALID_PERIOD).build()));
    }

    var currentDate = LocalDate.ofInstant(clock.instant(), ZoneId.systemDefault());
    if (startDate.getYear() < currentDate.getYear()
        || (startDate.getYear() == currentDate.getYear()
        && startDate.getMonthValue() < currentDate.getMonthValue())) {
      throw new BusinessException(List.of(BusinessExceptionElement.builder().errorCode(
          BusinessErrorCode.LEAVE_REQUEST_CREATE_INVALID_PERIOD).build()));
    }

  }

  @Transactional
  public void updateEmployeeLeaveRequest(String employeeId, Long requestId,
      LeaveRequestUpdate leaveRequestUpdate) {
    var employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new BusinessException(List.of(BusinessExceptionElement.builder()
            .errorCode(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
            .build())));

    var leaveRequest = employee.getLeaveRequestEties().stream()
        .filter(leaveRequestEty -> leaveRequestEty.getId().equals(requestId)).findFirst()
        .orElseThrow(() -> new BusinessException(Collections.singletonList(
            BusinessExceptionElement.builder().errorCode(BusinessErrorCode.LEAVE_REQUEST_NOT_FOUND)
                .build()))
        );

    if (!leaveRequest.getV().equals(leaveRequestUpdate.getV())) {
      throw new BusinessException(List.of(BusinessExceptionElement.builder()
          .errorCode(BusinessErrorCode.LEAVE_REQUEST_VERSION_CONFLICT)
          .build()));
    }
    if (leaveRequest.getStatus() == REJECTED) {
      throw new BusinessException(List.of(BusinessExceptionElement.builder()
          .errorCode(BusinessErrorCode.LEAVE_REQUEST_UPDATE_ALREADY_REJECTED)
          .build()));
    }

    if (!canLeaveRequestBeModified(leaveRequest, leaveRequestUpdate.getStartDate(),
        leaveRequestUpdate.getEndDate())) {
      throw new BusinessException(List.of(BusinessExceptionElement.builder()
          .errorCode(BusinessErrorCode.LEAVE_REQUEST_UPDATE_IN_PAST)
          .build()));
    }
    modifyLeaveRequest(leaveRequest, leaveRequestUpdate);
    employeeRepository.save(employee);
  }

  private void modifyLeaveRequest(LeaveRequestEty leaveRequestEty,
      LeaveRequestUpdate leaveRequestUpdate) {
    Optional.ofNullable(leaveRequestUpdate.getType()).ifPresent(leaveRequestEty::setType);
    Optional.ofNullable(leaveRequestUpdate.getDescription())
        .ifPresent(leaveRequestEty::setDescription);
    Optional.ofNullable(leaveRequestUpdate.getStartDate()).ifPresent(leaveRequestEty::setStartDate);
    Optional.ofNullable(leaveRequestUpdate.getEndDate()).ifPresent(leaveRequestEty::setEndDate);
    leaveRequestEty.setNoOfDays(leaveRequestHandler.getDaysOffFromAPeriod(leaveRequestEty.getStartDate(),
        leaveRequestEty.getEndDate()));
    leaveRequestEty.setMdfUsr(securityAccessTokenHandler.getEmployeeIdFromAuthenticationToken());
    leaveRequestEty.setMdfTms(clock.instant());
    leaveRequestEty.setStatus(PENDING);
  }

  private boolean canLeaveRequestBeModified(LeaveRequestEty leaveRequestEty, LocalDate newStartDate,
      LocalDate newEndDate) {
    var currentDate = LocalDate.ofInstant(clock.instant(), ZoneId.systemDefault());
    if (leaveRequestEty.getStartDate().isAfter(currentDate)) {
      return true;
    } else {
      return leaveRequestEty.getStartDate().getMonthValue() < currentDate.getMonthValue()
          && Objects.equals(leaveRequestEty.getStartDate(), newStartDate)
          && newEndDate.getMonthValue() == currentDate.getMonthValue();
    }
  }

  @Transactional
  public void deleteLeaveRequest(String employeeId, Long requestId) {

    var employeeEty = employeeRepository.findById(employeeId)
        .orElseThrow(
            () -> new BusinessException(Collections.singletonList(
                BusinessExceptionElement.builder().errorCode(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
                    .build()))
        );
    var leaveRequest = employeeEty.getLeaveRequestEties().stream()
        .filter(leaveRequestEty -> leaveRequestEty.getId().equals(requestId)).findFirst()
        .orElseThrow(() -> new BusinessException(Collections.singletonList(
            BusinessExceptionElement.builder().errorCode(BusinessErrorCode.LEAVE_REQUEST_NOT_FOUND)
                .build()))
        );

    if (leaveRequest.getStatus() == LeaveRequestStatus.REJECTED) {
      throw new BusinessException(Collections.singletonList(BusinessExceptionElement.builder()
          .errorCode(BusinessErrorCode.LEAVE_REQUEST_DELETE_NOT_PERMITTED_REJECTED_STATUS)
          .build()));
    }

    LocalDate now = LocalDate.ofInstant(clock.instant(), ZoneId.systemDefault());
    int currentMonth = now.getMonthValue();
    int currentYear = now.getYear();
    int leaveRequestStartDateMonth = leaveRequest.getStartDate().getMonthValue();
    int leaveRequestStartDateYear = leaveRequest.getStartDate().getYear();

    if ((leaveRequest.getStatus() == LeaveRequestStatus.APPROVED) && (
        leaveRequestStartDateYear < currentYear || (leaveRequestStartDateYear == currentYear
            && leaveRequestStartDateMonth < currentMonth))) {
      throw new BusinessException(Collections.singletonList(BusinessExceptionElement.builder()
          .errorCode(BusinessErrorCode.LEAVE_REQUEST_DELETE_NOT_PERMITTED_APPROVED_DAYS_IN_PAST)
          .build()));
    }

    employeeEty.removeLeaveRequest(leaveRequest);
    employeeRepository.save(employeeEty);
  }

  @Transactional
  public EmployeeRemainingDaysOfCurrentYear getRemainingDaysOff(String employeeId) {
    var employee = employeeRepository.findEmployeeEtyById(employeeId).orElseThrow(
        () -> new BusinessException(Collections.singletonList(
            BusinessExceptionElement.builder()
                .errorCode(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
                .build())));

    var currentDate = LocalDate.ofInstant(clock.instant(), ZoneOffset.UTC);
    var currentYear = currentDate.getYear();

    var totalDaysOff = employee.getDaysOffEties().stream()
        .filter(daysOffEty -> daysOffEty.getYear() == currentYear)
        .findFirst()
        .orElseThrow(() -> new BusinessException(Collections.singletonList(
            BusinessExceptionElement.builder()
                .errorCode(BusinessErrorCode.DAYS_OFF_MISSING)
                .build())))
        .getTotalDays();

    int vacationRequestsDays = getAllVacationDays(employee);

    int remainingVacationDays = totalDaysOff - vacationRequestsDays;

    if (remainingVacationDays < 0) {
      throw new BusinessException(Collections.singletonList(
          BusinessExceptionElement.builder()
              .errorCode(BusinessErrorCode.NEGATIVE_DAYS_OFF_EXCEPTION)
              .build()));
    }

    return new EmployeeRemainingDaysOfCurrentYear(remainingVacationDays);
  }

  private Integer getAllVacationDays(EmployeeEty employee) {
    var leaveRequestList = employee.getLeaveRequestEties();

    return leaveRequestList.stream()
        .filter(leaveRequestEty -> leaveRequestEty.getType() == LeaveRequestType.VACATION)
        .filter(leaveRequestEty -> leaveRequestEty.getStatus() != LeaveRequestStatus.REJECTED)
        .mapToInt(LeaveRequestEty::getNoOfDays)
        .sum();
  }
}
