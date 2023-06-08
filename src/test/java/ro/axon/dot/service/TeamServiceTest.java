package ro.axon.dot.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.axon.dot.domain.entity.TeamEty;
import ro.axon.dot.domain.entity.enums.TeamStatus;
import ro.axon.dot.domain.repository.TeamRepository;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

  @Mock
  private TeamRepository teamRepository;

  @InjectMocks
  private TeamService teamService;


  @Test
  void getActiveTeams_returnRsAccordingly() {
    var team1 = new TeamEty();
    team1.setId(15L);
    team1.setName("Marketing");
    team1.setCrtUsr("aditional.load");
    team1.setCrtTms(Instant.parse("2022-06-17T21:00:00Z"));
    team1.setMdfUsr("aditional.load");
    team1.setMdfTms(Instant.parse("2022-06-17T21:00:00Z"));
    team1.setStatus(TeamStatus.ACTIVE);

    var team2 = new TeamEty();
    team2.setId(17L);
    team2.setName("Testing");
    team2.setCrtUsr("aditional.load");
    team2.setCrtTms(Instant.parse("2022-06-17T21:00:00Z"));
    team2.setMdfUsr("aditional.load");
    team2.setMdfTms(Instant.parse("2022-06-17T21:00:00Z"));
    team2.setStatus(TeamStatus.ACTIVE);

    when(teamRepository.findByStatus(any(TeamStatus.class)))
        .thenReturn(List.of(team1, team2));

    var activeTeams = teamService.getTeamsDetails(Optional.of(TeamStatus.ACTIVE));
    assertThat(activeTeams).isNotNull();
    assertThat(activeTeams.getItems()).hasSize(2);

    var teamDetailsListItem = activeTeams.getItems().get(0);
    assertThat(teamDetailsListItem.getId()).isEqualTo(15L);
    assertThat(teamDetailsListItem.getName()).isEqualTo("Marketing");
    assertThat(teamDetailsListItem.getCrtUsr()).isEqualTo("aditional.load");
    assertThat(teamDetailsListItem.getCrtTms()).isEqualTo(Instant.parse("2022-06-17T21:00:00Z"));
    assertThat(teamDetailsListItem.getMdfUsr()).isEqualTo("aditional.load");
    assertThat(teamDetailsListItem.getMdfTms()).isEqualTo(Instant.parse("2022-06-17T21:00:00Z"));
    assertThat(teamDetailsListItem.getStatus()).isEqualTo(TeamStatus.ACTIVE);

    teamDetailsListItem = activeTeams.getItems().get(1);
    assertThat(teamDetailsListItem.getId()).isEqualTo(17L);
    assertThat(teamDetailsListItem.getName()).isEqualTo("Testing");
    assertThat(teamDetailsListItem.getCrtUsr()).isEqualTo("aditional.load");
    assertThat(teamDetailsListItem.getCrtTms()).isEqualTo(Instant.parse("2022-06-17T21:00:00Z"));
    assertThat(teamDetailsListItem.getMdfUsr()).isEqualTo("aditional.load");
    assertThat(teamDetailsListItem.getMdfTms()).isEqualTo(Instant.parse("2022-06-17T21:00:00Z"));
    assertThat(teamDetailsListItem.getStatus()).isEqualTo(TeamStatus.ACTIVE);

    verify(teamRepository).findByStatus(TeamStatus.ACTIVE);


  }

  @Test
  void getAllTeams_returnRsAccordingly() {
    var team1 = new TeamEty();
    team1.setId(15L);
    team1.setName("Marketing");
    team1.setCrtUsr("aditional.load");
    team1.setCrtTms(Instant.parse("2022-06-17T21:00:00Z"));
    team1.setMdfUsr("aditional.load");
    team1.setMdfTms(Instant.parse("2022-06-17T21:00:00Z"));
    team1.setStatus(TeamStatus.ACTIVE);

    when(teamRepository.findAll())
        .thenReturn(List.of(team1));

    var activeTeams = teamService.getTeamsDetails(Optional.empty());
    assertThat(activeTeams).isNotNull();
    assertThat(activeTeams.getItems()).hasSize(1);

    var teamDetailsListItem = activeTeams.getItems().get(0);
    assertThat(teamDetailsListItem.getId()).isEqualTo(15L);
    assertThat(teamDetailsListItem.getName()).isEqualTo("Marketing");
    assertThat(teamDetailsListItem.getCrtUsr()).isEqualTo("aditional.load");
    assertThat(teamDetailsListItem.getCrtTms()).isEqualTo(Instant.parse("2022-06-17T21:00:00Z"));
    assertThat(teamDetailsListItem.getMdfUsr()).isEqualTo("aditional.load");
    assertThat(teamDetailsListItem.getMdfTms()).isEqualTo(Instant.parse("2022-06-17T21:00:00Z"));
    assertThat(teamDetailsListItem.getStatus()).isEqualTo(TeamStatus.ACTIVE);

    verify(teamRepository).findAll();


  }
}