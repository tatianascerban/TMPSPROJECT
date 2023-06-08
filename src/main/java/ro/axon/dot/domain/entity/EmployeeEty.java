package ro.axon.dot.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import ro.axon.dot.domain.entity.enums.EmployeeStatus;

@Entity
@Setter
@Getter
@Table(name = "EMPLOYEE")
public class EmployeeEty extends SrgKeyEntityTml<String> {

  @Id
  @GeneratedValue(generator = "employee-uuid")
  @GenericGenerator(name = "employee-uuid", strategy = "uuid2")
  @Column(name = "EMPLOYEE_ID")
  private String id;

  @Column(name = "USERNAME")
  private String username;

  @Column(name = "PASSWORD")
  private String password;

  @Column(name = "FIRST_NAME")
  private String firstName;

  @Column(name = "LAST_NAME")
  private String lastName;

  @Column(name = "EMAIL")
  private String email;

  @Column(name = "CRT_USR")
  private String crtUsr;

  @Column(name = "CRT_TMS")
  private Instant crtTms;

  @Column(name = "MDF_USR")
  private String mdfUsr;

  @Column(name = "MDF_TMS")
  private Instant mdfTms;

  @Column(name = "ROLE")
  private String role;

  @Enumerated(EnumType.STRING)
  @Column(name = "STATUS")
  private EmployeeStatus status;

  @JsonFormat(pattern = "yyyy-MM-dd")
  @Column(name = "CONTRACT_START_DATE")
  private LocalDate contractStartDate;

  @Column(name = "CONTRACT_END_DATE")
  private LocalDate contractEndDate;

  @ManyToOne
  @JoinColumn(name = "TEAM_ID", referencedColumnName = "ID")
  private TeamEty team;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "EMPLOYEE_ID")
  private List<DaysOffEty> daysOffEties = new ArrayList<>();

  @OneToMany(
      mappedBy = "employee",
      cascade = CascadeType.ALL,
      orphanRemoval = true
  )
  private List<LeaveRequestEty> leaveRequestEties = new ArrayList<>();

  public void addLeaveRequest(LeaveRequestEty leaveRequestEty) {
    leaveRequestEties.add(leaveRequestEty);
    leaveRequestEty.setEmployee(this);
  }

  public void removeLeaveRequest(LeaveRequestEty leaveRequestEty) {
    leaveRequestEties.remove(leaveRequestEty);
    leaveRequestEty.setEmployee(null);
  }

  @Override
  public String toString() {
    return "EmployeeEty{" +
        "id=" + id +
        ", userName='" + username + '\'' +
        ", password='" + password + '\'' +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", email='" + email + '\'' +
        ", userNameCreate='" + crtUsr + '\'' +
        ", createdTime=" + crtTms +
        ", userNameModify='" + mdfUsr + '\'' +
        ", lastModifyTime=" + mdfTms +
        ", role=" + role +
        ", status='" + status + '\'' +
        ", contractStartDate=" + contractStartDate +
        ", contractEndDate=" + contractEndDate +
        ", team=" + team +
        '}';
  }

  @Override
  protected Class<? extends SrgKeyEntityTml<String>> entityRefClass() {
    return EmployeeEty.class;
  }
}