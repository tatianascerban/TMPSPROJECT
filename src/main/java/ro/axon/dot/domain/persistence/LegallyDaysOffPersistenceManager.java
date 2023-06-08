package ro.axon.dot.domain.persistence;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import ro.axon.dot.domain.entity.LegallyDaysOffEty;
import ro.axon.dot.domain.repository.LegallyDaysOffRepository;

@Component
@RequiredArgsConstructor
public class LegallyDaysOffPersistenceManager {

  private final LegallyDaysOffRepository legallyDaysOffRepository;

  @Cacheable(value = "legallyDaysOff")
  public List<LegallyDaysOffEty> retrievesAllLegallyDaysOffFromDb() {
    return legallyDaysOffRepository.findAll();
  }

}
