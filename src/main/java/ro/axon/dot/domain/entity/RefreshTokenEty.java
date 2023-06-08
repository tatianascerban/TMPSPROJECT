package ro.axon.dot.domain.entity;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.axon.dot.domain.entity.enums.RefreshTokenStatus;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "REFRESH_TOKEN")
public class RefreshTokenEty {

  @Id
  @Column(name = "ID")
  private String id;

  @Enumerated(EnumType.STRING)
  @Column(name = "STATUS")
  private RefreshTokenStatus status;

  @ManyToOne
  @JoinColumn(name = "AUDIENCE", referencedColumnName = "EMPLOYEE_ID")
  private EmployeeEty audience;

  @Column(name = "CRT_TMS")
  private Instant createTime;

  @Column(name = "MDF_TMS")
  private Instant modifyTime;

  @Column(name = "EXP_TMS")
  private Instant expireTime;
}


