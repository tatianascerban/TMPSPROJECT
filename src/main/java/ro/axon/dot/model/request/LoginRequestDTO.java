package ro.axon.dot.model.request;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginRequestDTO {

  @NotEmpty
  private String username;

  @NotEmpty
  private String password;

}
