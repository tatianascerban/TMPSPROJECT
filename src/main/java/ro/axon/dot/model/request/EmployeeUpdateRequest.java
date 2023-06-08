package ro.axon.dot.model.request;

import lombok.Data;

@Data
public class EmployeeUpdateRequest {

  private String firstName;
  private String lastName;
  private String username;
  private String email;
  private String role;
  private Long teamId;
  private Long v;

}
