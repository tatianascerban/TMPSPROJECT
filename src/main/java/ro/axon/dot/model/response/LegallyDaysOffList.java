package ro.axon.dot.model.response;

import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LegallyDaysOffList {
  private List<LegallyDaysOffListItem> legallyDaysOffListItemList;

}
