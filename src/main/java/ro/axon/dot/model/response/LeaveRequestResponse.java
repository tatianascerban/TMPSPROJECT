package ro.axon.dot.model.response;

import java.time.LocalDate;
import lombok.Data;
import ro.axon.dot.enums.LeaveRequestStatus;
import ro.axon.dot.enums.LeaveRequestType;

@Data
public class LeaveRequestResponse {

  private LocalDate startDate;

  private LocalDate endDate;

  private LeaveRequestStatus status;

  private LeaveRequestType type;

  private String description;

  private Integer noOfDays;
}
