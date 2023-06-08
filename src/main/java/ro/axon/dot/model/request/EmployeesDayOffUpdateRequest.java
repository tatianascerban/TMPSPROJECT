package ro.axon.dot.model.request;

import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import ro.axon.dot.domain.entity.enums.DaysOffType;

@Data
public class EmployeesDayOffUpdateRequest {

  @NotEmpty
  List<String> employeeIds;
  @NotNull
  @Min(value = 1, message = "Numbers of days must be greater than zero")
  Integer noDays;
  @NotNull
  DaysOffType type;

  String description;
}
