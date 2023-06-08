package ro.axon.dot.model.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class UserDetailsResponse {

  private List<String> roles;

  private String username;

  private String employeeId;

  private TeamDetails teamDetails;

  @Getter
  @Setter
  @Builder
  public static class TeamDetails {

    private Long teamId;

    private String name;
  }
}


