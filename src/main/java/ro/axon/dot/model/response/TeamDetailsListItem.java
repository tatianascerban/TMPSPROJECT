package ro.axon.dot.model.response;

import lombok.Data;

import java.time.Instant;
import ro.axon.dot.domain.entity.enums.TeamStatus;

@Data
public class TeamDetailsListItem {

  private Long id;
  private String name;
  private String crtUsr;
  private Instant crtTms;
  private String mdfUsr;
  private Instant mdfTms;
  private TeamStatus status;

}
