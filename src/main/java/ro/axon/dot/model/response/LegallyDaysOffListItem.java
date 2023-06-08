package ro.axon.dot.model.response;

import java.time.LocalDate;
import java.util.Date;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LegallyDaysOffListItem {

  private LocalDate date;

  private String description;

}
