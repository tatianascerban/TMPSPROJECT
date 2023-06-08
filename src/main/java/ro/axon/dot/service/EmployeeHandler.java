package ro.axon.dot.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ro.axon.dot.configuration.EmployeeProperties;
import ro.axon.dot.domain.entity.DaysOffEty;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(EmployeeProperties.class)
public class EmployeeHandler {
  
  private final EmployeeProperties employeeProperties;

  private final PasswordEncoder passwordEncoder;

  int getEmployeeYearTotalDaysOff(List<DaysOffEty> empYearlyDaysOffEtyList, int year) {
    var totalDaysOff = new AtomicInteger();
    empYearlyDaysOffEtyList.stream()
        .filter(empYearlyDaysOffEty -> empYearlyDaysOffEty.getYear() == year)
        .findFirst()
        .ifPresentOrElse(empYearlyDaysOffEty -> totalDaysOff.set(
                empYearlyDaysOffEty.getTotalDays()),
            () -> totalDaysOff.set(0));
    return totalDaysOff.get();
  }

  public String generatePassword(String username) {
    return passwordEncoder.encode(username + employeeProperties.getPasswordSequence());
  }

}
