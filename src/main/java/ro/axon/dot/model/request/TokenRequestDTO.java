package ro.axon.dot.model.request;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class TokenRequestDTO {

  @NotEmpty
  private String refreshToken;
}
