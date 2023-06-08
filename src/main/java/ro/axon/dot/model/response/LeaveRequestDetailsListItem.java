package ro.axon.dot.model.response;

import java.time.Instant;
import java.time.LocalDate;
import lombok.Data;
import ro.axon.dot.enums.LeaveRequestStatus;
import ro.axon.dot.enums.LeaveRequestType;

@Data
public class LeaveRequestDetailsListItem {

  private Long id;
  private String crtUsr;
  private Instant crtTms;
  private String mdfUsr;
  private Instant mdfTms;
  private LocalDate startDate;
  private LocalDate endDate;
  private LeaveRequestStatus status;
  private LeaveRequestType type;
  private String description;
  private String rejectReason;
  private Integer noOfDays;
  private Long v;
  private EmployeeDetailsListItem employeeDetails;
}