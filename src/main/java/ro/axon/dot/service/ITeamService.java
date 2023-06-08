package ro.axon.dot.service;

import java.util.Optional;
import ro.axon.dot.domain.entity.enums.TeamStatus;
import ro.axon.dot.model.request.TeamCreationRequest;
import ro.axon.dot.model.response.TeamDetailsList;

public interface ITeamService {

  TeamDetailsList getTeamsDetails(Optional<TeamStatus> teamStatus);

  void createNewTeam(TeamCreationRequest team);
}
