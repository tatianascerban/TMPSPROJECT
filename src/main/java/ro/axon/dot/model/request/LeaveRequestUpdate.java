package ro.axon.dot.model.request;

import java.time.LocalDate;
import javax.validation.constraints.Size;
import lombok.Data;
import ro.axon.dot.enums.LeaveRequestType;

@Data
public class LeaveRequestUpdate {

  private LeaveRequestType type;
  @Size(max = 255)
  private String description;
  private LocalDate startDate;
  private LocalDate endDate;
  private Long v;
}