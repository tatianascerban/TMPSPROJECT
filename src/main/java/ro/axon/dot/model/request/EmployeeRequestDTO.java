package ro.axon.dot.model.request;

import java.time.LocalDate;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmployeeRequestDTO {

  @NotEmpty
  private String username;

  @NotEmpty
  private String firstname;

  @NotEmpty
  private String lastname;

  @NotEmpty
  private String email;

  @NotEmpty
  private String role;

  @NotNull
  private Long teamId;

  @NotNull
  private LocalDate contractStartDate;

  @NotNull
  @Min(value = 0, message = "Numbers of days cannot be negative")
  private Integer noDaysOff;

  public static class EmployeeRequestDTOBuilder{
    private EmployeeRequestDTO dto = new EmployeeRequestDTO();

    public EmployeeRequestDTOBuilder username(String username) {
      dto.setUsername(username);
      return this;
    }

    public EmployeeRequestDTOBuilder firstName(String firstname) {
      dto.setFirstname(firstname);
      return this;
    }

    public EmployeeRequestDTOBuilder lastName(String lastName) {
      dto.setLastname(lastName);
      return this;
    }

    public EmployeeRequestDTOBuilder email(String email) {
      dto.setEmail(email);
      return this;
    }
    public EmployeeRequestDTOBuilder teamId(Long teamId) {
      dto.setTeamId(teamId);
      return this;
    }
    public EmployeeRequestDTOBuilder contractStartDate(LocalDate contractStartDate) {
      dto.setContractStartDate(contractStartDate);
      return this;
    }

    public EmployeeRequestDTOBuilder noDaysOff(Integer noDaysOff) {
      dto.setNoDaysOff(noDaysOff);
      return this;
    }

    public EmployeeRequestDTO build() {
      return dto;
    }
  }
}
