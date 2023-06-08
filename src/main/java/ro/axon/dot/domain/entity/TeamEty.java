package ro.axon.dot.domain.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ro.axon.dot.domain.entity.enums.TeamStatus;

@Entity
@SequenceGenerator(name = "TEAM_ID_SQ", sequenceName = "TEAM_ID_SQ", allocationSize = 1)
@Getter
@Setter
@Table(name = "TEAM")
public class TeamEty extends SrgKeyEntityTml<Long> {


  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEAM_ID_SQ")
  private Long id;

  @Column(name = "NAME")
  private String name;
  @Column(name = "CRT_USR")
  private String crtUsr;
  @Column(name = "CRT_TMS")
  private Instant crtTms;
  @Column(name = "MDF_USR")
  private String mdfUsr;
  @Column(name = "MDF_TMS")
  private Instant mdfTms;
  @Enumerated(EnumType.STRING)
  @Column(name = "STATUS")
  private TeamStatus status;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "TEAM_ID")
  private List<EmployeeEty> employees = new ArrayList<>();

  public void addEmployee(EmployeeEty employeeEty) {
    employees.add(employeeEty);
  }

  @Override
  protected Class<? extends SrgKeyEntityTml<Long>> entityRefClass() {
    return TeamEty.class;
  }

}
