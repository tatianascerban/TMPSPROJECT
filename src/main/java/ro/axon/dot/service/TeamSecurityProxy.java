package ro.axon.dot.service;

import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;
import ro.axon.dot.domain.entity.enums.TeamStatus;
import ro.axon.dot.model.request.TeamCreationRequest;
import ro.axon.dot.model.response.TeamDetailsList;

@Component
public class TeamSecurityProxy implements ITeamService{

  private final SecurityAccessTokenHandler securityAccessTokenHandler;

  private final TeamService teamService;
  public TeamSecurityProxy(SecurityAccessTokenHandler securityAccessTokenHandler,
      TeamService teamService) {
    this.securityAccessTokenHandler = securityAccessTokenHandler;
    this.teamService = teamService;
  }

  @Override
  public TeamDetailsList getTeamsDetails(Optional<TeamStatus> teamStatus) {
    if(Objects.isNull(securityAccessTokenHandler.getEmployeeIdFromAuthenticationToken())) {
      throw new IllegalStateException("User not logged.");
    } else
      return teamService.getTeamsDetails(teamStatus);
  }

  @Override
  public void createNewTeam(TeamCreationRequest team) {
    teamService.createNewTeam(team);
  }
}
