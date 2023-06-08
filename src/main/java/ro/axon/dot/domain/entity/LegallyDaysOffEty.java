package ro.axon.dot.domain.entity;


import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "legally_days_off")
public class LegallyDaysOffEty {

  @Id
  @Column(name = "date")
  private LocalDate date;

  @Column(name = "description")
  private String description;

}
