package ro.axon.dot.model.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
public class VacationResponse {

  private List<VacationResponseItem> items;

  @Data
  @Builder
  public static class VacationResponseItem {

    private String firstName;

    private String lastName;

    private int noOfVacationDays;

    private int noOfMedicalDays;

    private List<LeaveRequestResponse> leaveRequests;

  }
}

