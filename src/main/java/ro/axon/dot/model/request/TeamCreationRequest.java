package ro.axon.dot.model.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TeamCreationRequest {
  @NotBlank
  @NotNull
  private String name;

}
