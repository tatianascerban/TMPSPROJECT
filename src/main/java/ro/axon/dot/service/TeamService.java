package ro.axon.dot.service;

import java.time.Clock;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.axon.dot.domain.entity.TeamEty;
import ro.axon.dot.domain.entity.enums.TeamStatus;
import ro.axon.dot.domain.repository.TeamRepository;
import ro.axon.dot.mapper.TeamMapper;
import ro.axon.dot.model.request.TeamCreationRequest;
import ro.axon.dot.model.response.TeamDetailsList;

@Service
@RequiredArgsConstructor
public class TeamService implements ITeamService {

  private final TeamRepository teamRepository;

  private final Clock clock;

  private final SecurityAccessTokenHandler securityAccessTokenHandler;

  public TeamDetailsList getTeamsDetails(Optional<TeamStatus> teamStatus) {
    var teamDetailsList = new TeamDetailsList();

    teamStatus.ifPresentOrElse(status ->
            teamDetailsList.setItems(
                teamRepository.findByStatus(status).stream()
                    .map(TeamMapper.INSTANCE::mapTeamEtyToTeamDto)
                    .collect(Collectors.toList()))
        ,
        () ->
            teamDetailsList.setItems(
                teamRepository.findAll().stream().map(TeamMapper.INSTANCE::mapTeamEtyToTeamDto)
                    .collect(Collectors.toList())));
    return teamDetailsList;
  }

  public void createNewTeam(TeamCreationRequest team) {
    var loggedEmployeeId = securityAccessTokenHandler.getEmployeeIdFromAuthenticationToken();
    var newTeam = new TeamEty();
    var now = clock.instant();
    newTeam.setName(team.getName());
    newTeam.setCrtUsr(loggedEmployeeId);
    newTeam.setMdfUsr(loggedEmployeeId);
    newTeam.setCrtTms(now);
    newTeam.setMdfTms(now);
    newTeam.setStatus(TeamStatus.ACTIVE);
    teamRepository.save(newTeam);
  }
}
