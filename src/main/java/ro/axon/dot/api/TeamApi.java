package ro.axon.dot.api;

import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ro.axon.dot.domain.entity.enums.TeamStatus;
import ro.axon.dot.model.request.TeamCreationRequest;
import ro.axon.dot.model.response.TeamDetailsList;
import ro.axon.dot.service.TeamSecurityProxy;
import ro.axon.dot.service.TeamService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teams")
public class TeamApi {

  private final TeamSecurityProxy teamService;

  @GetMapping()
  public ResponseEntity<TeamDetailsList> getTeamDetailsList(
      @RequestParam(required = false) TeamStatus status) {
    return ResponseEntity.ok(teamService.getTeamsDetails(Optional.ofNullable(status)));
  }

  @PostMapping()
  public ResponseEntity<Void> createTeam(@Valid @RequestBody TeamCreationRequest team) {
    teamService.createNewTeam(team);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
