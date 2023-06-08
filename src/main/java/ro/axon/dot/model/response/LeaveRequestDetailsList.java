package ro.axon.dot.model.response;

import java.util.List;
import lombok.Data;

@Data
public class LeaveRequestDetailsList {

  private List<LeaveRequestDetailsListItem> items;

}