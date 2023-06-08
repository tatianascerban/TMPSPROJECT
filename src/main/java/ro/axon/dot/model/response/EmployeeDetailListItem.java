package ro.axon.dot.model.response;

import java.time.Instant;
import java.time.LocalDate;
import lombok.Data;
import ro.axon.dot.domain.entity.enums.EmployeeStatus;

@Data
public class EmployeeDetailListItem {

  private String id;
  private String firstName;
  private String lastName;
  private String email;
  private String crtUsr;
  private Instant crtTms;
  private String mdfUsr;
  private Instant mdfTms;
  private String role;
  private EmployeeStatus status;
  private LocalDate contractStartDate;
  private LocalDate contractEndDate;
  private Long v;
  private int totalVacationDays;
  private TeamDetailsListItem teamDetails;
  private String username;
}
