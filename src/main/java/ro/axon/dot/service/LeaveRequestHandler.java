package ro.axon.dot.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.axon.dot.domain.entity.LegallyDaysOffEty;
import ro.axon.dot.domain.persistence.LegallyDaysOffPersistenceManager;

@Component
@RequiredArgsConstructor
public class LeaveRequestHandler {

  private final LegallyDaysOffPersistenceManager legallyDaysOffPersistenceManager;

  private static boolean isWeekend(LocalDate date) {
    var day = DayOfWeek.of(date.get(ChronoField.DAY_OF_WEEK));
    return day == DayOfWeek.SUNDAY || day == DayOfWeek.SATURDAY;
  }

  int getDaysOffFromAPeriod(LocalDate start, LocalDate end) {
    var daysOff = new AtomicInteger();
    start.datesUntil(end.plusDays(1)).forEach(date -> {
      if (findByDate(date).isEmpty() && !isWeekend(date)) {
        daysOff.incrementAndGet();
      }
    });
    return daysOff.get();
  }

  private Optional<LegallyDaysOffEty> findByDate(LocalDate date) {
    return legallyDaysOffPersistenceManager.retrievesAllLegallyDaysOffFromDb().stream()
        .filter(d -> d.getDate().equals(date)).findFirst();
  }

}
