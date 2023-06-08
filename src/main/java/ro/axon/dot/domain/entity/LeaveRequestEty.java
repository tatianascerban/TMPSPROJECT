package ro.axon.dot.domain.entity;

import java.time.Instant;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ro.axon.dot.enums.LeaveRequestStatus;
import ro.axon.dot.enums.LeaveRequestType;


@Entity
@Table(name = "LEAVE_REQUEST")
@Getter
@Setter
@SequenceGenerator(name = "LEAVE_REQUEST_ID_SQ", sequenceName = "LEAVE_REQUEST_ID_SQ", allocationSize = 1)

public class LeaveRequestEty extends SrgKeyEntityTml<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LEAVE_REQUEST_ID_SQ")
  private Long id;

  @Column(name = "CRT_USR")
  private String crtUsr;

  @Column(name = "CRT_TMS")
  private Instant crtTms;

  @Column(name = "MDF_USR")
  private String mdfUsr;

  @Column(name = "MDF_TMS")
  private Instant mdfTms;

  @Column(name = "START_DATE")
  private LocalDate startDate;

  @Column(name = "END_DATE")
  private LocalDate endDate;

  @Column(name = "STATUS")
  @Enumerated(EnumType.STRING)
  private LeaveRequestStatus status;

  @Column(name = "TYPE")
  @Enumerated(EnumType.STRING)
  private LeaveRequestType type;

  @Column(name = "DESCRIPTION")
  private String description;

  @Column(name = "REJECT_REASON")
  private String rejectReason;

  @Column(name = "NO_DAYS")
  private Integer noOfDays;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "EMPLOYEE_ID")
  private EmployeeEty employee;

  @Override
  protected Class<? extends SrgKeyEntityTml<Long>> entityRefClass() {
    return LeaveRequestEty.class;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof LeaveRequestEty)) {
      return false;
    }
    return id != null && id.equals(((LeaveRequestEty) o).getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

}
