package ro.axon.dot.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.axon.dot.domain.entity.DaysOffEty;
import ro.axon.dot.domain.entity.EmployeeEty;
import ro.axon.dot.domain.entity.TeamEty;
import ro.axon.dot.domain.entity.enums.DaysOffType;
import ro.axon.dot.domain.repository.DaysOffRepository;
import ro.axon.dot.domain.repository.EmployeeRepository;
import ro.axon.dot.model.request.EmployeesDayOffUpdateRequest;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
  @Mock
  EmployeeRepository employeeRepository;
  @Mock
  DaysOffRepository daysOffRepository;
  @Mock
  Clock clock;
  @Mock
  SecurityAccessTokenHandler securityAccessTokenHandler;
  @InjectMocks
  EmployeeService employeeService;

  @Test
  void updateEmployeesDaysOffTest() {
    when(securityAccessTokenHandler.getEmployeeIdFromAuthenticationToken()).thenReturn("test");
    when(clock.instant()).thenReturn(Instant.now());

    var employee1 = new EmployeeEty();
    employee1.setId("emp1");
    employee1.setUsername("username1");
    employee1.setFirstName("firstname1");
    employee1.setLastName("lastname1");
    employee1.setEmail("email1@gmail.com");
    employee1.setRole("USER");
    var team1 = new TeamEty();
    team1.setId(1L);
    team1.setName("Marketing");
    employee1.setTeam(team1);
    employee1.setContractStartDate(LocalDate.now());
    var daysOff1 = DaysOffEty.builder().daysOffHistoryEties(new ArrayList<>()).build();
    employee1.setDaysOffEties(List.of(daysOff1));

    var employee2 = new EmployeeEty();
    employee2.setId("emp2");

    var updateRequest = new EmployeesDayOffUpdateRequest();
    updateRequest.setEmployeeIds(List.of("emp1"));
    updateRequest.setNoDays(5);
    updateRequest.setType(DaysOffType.INCREASE);

    when(employeeRepository.findAllById(anyIterable()))
        .thenReturn(List.of(employee1));

    when(daysOffRepository.save(any(DaysOffEty.class)))
        .thenReturn(daysOff1);

    employeeService.updateEmployeesDaysOff(updateRequest);

    verify(employeeRepository).findAllById(anyIterable());
  }
}