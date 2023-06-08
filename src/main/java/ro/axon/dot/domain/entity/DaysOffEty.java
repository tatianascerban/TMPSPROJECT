package ro.axon.dot.domain.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@SequenceGenerator(name = "EMP_YEARLY_DAYS_OFF_ID_SQ", sequenceName = "EMP_YEARLY_DAYS_OFF_ID_SQ", allocationSize = 1)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "EMP_YEARLY_DAYS_OFF")
public class DaysOffEty {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMP_YEARLY_DAYS_OFF_ID_SQ")
  @Column(name = "ID")
  private Long id;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "EMPLOYEE_ID")
  private EmployeeEty employee;

  @Column(name = "TOTAL_NO_DAYS")
  private Integer totalDays;

  @Column(name = "YEAR")
  private Integer year;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "EMP_YEARLY_DAYS_OFF_ID", referencedColumnName = "ID")
  private List<DaysOffHistoryEty> daysOffHistoryEties = new ArrayList<>();

}
