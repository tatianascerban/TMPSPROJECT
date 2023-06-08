package ro.axon.dot.domain.entity;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.axon.dot.domain.entity.enums.DaysOffType;

@Entity
@SequenceGenerator(name = "EMP_YEARLY_DAYS_OFF_HIST_ID_SQ", sequenceName = "EMP_YEARLY_DAYS_OFF_HIST_ID_SQ", allocationSize = 1)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "EMP_YEARLY_DAYS_OFF_HIST")
public class DaysOffHistoryEty {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMP_YEARLY_DAYS_OFF_HIST_ID_SQ")
  @Column(name = "ID")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "EMP_YEARLY_DAYS_OFF_ID")
  private DaysOffEty daysOffEty;

  @Column(name = "NO_DAYS")
  private Integer numberOfDays;

  @Column(name = "DESCRIPTION")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "TYPE")
  private DaysOffType type;

  @Column(name = "CRT_USR")
  private String userNameCreate;

  @Column(name = "CRT_TMS")
  private Instant createdTime;
}
