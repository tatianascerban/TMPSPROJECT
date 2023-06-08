package ro.axon.dot.model.response;

import java.util.List;
import lombok.Data;

@Data
public class EmployeeDetailList {

  private List<EmployeeDetailListItem> items;
}
