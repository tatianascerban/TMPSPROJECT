package ro.axon.dot.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.axon.dot.domain.persistence.LegallyDaysOffPersistenceManager;
import ro.axon.dot.mapper.LegallyDaysOffMapper;
import ro.axon.dot.model.response.LegallyDaysOffList;

@Service
@RequiredArgsConstructor
public class LegallyDaysOffService {

  private static final String EXPECTED_YEAR_FORMAT = "yyyy";
  private static final String EXPECTED_PERIOD_FORMAT = "yyyy-MM";
  private static final String CURRENT_DATE_FORMAT = "yyyy-MM-dd";

  private final LegallyDaysOffPersistenceManager legallyDaysOffPersistenceManager;

  public LegallyDaysOffList getLegallyDaysOff(String[] years, String[] periods) {
    if(Objects.isNull(years) && Objects.isNull(periods)) {
      return getLegallyDaysOffIfRequestParamsAreNull();
    } else if(Objects.nonNull(periods) && Objects.isNull(years)) {
      return getLegallyDaysOffIfYearsAreNull(periods);
    } else {
      return getLegallyDaysOffIfYearsNonNull(years);
    }
  }

  private LegallyDaysOffList getLegallyDaysOffIfRequestParamsAreNull() {
    var legallyDaysList = new LegallyDaysOffList();
    legallyDaysList.setLegallyDaysOffListItemList(legallyDaysOffPersistenceManager.retrievesAllLegallyDaysOffFromDb()
        .stream()
        .map(LegallyDaysOffMapper.INSTANCE::mapLegallyDaysOffEtyToLegallyDaysOffDto)
        .collect(Collectors.toList()));
    return legallyDaysList;
  }

  private LegallyDaysOffList getLegallyDaysOffIfYearsNonNull(String[] years) {
    var legallyDaysList = new LegallyDaysOffList();
    legallyDaysList.setLegallyDaysOffListItemList(legallyDaysOffPersistenceManager.retrievesAllLegallyDaysOffFromDb()
        .stream()
        .map(LegallyDaysOffMapper.INSTANCE::mapLegallyDaysOffEtyToLegallyDaysOffDto)
        .filter(day -> Arrays.stream(years).anyMatch(x -> x.equals(yearConvertor(day.getDate()))))
        .collect(Collectors.toList()));
    return legallyDaysList;
  }

  private LegallyDaysOffList getLegallyDaysOffIfYearsAreNull(String[] periods) {
    var legallyDaysList = new LegallyDaysOffList();
    legallyDaysList.setLegallyDaysOffListItemList(legallyDaysOffPersistenceManager.retrievesAllLegallyDaysOffFromDb()
        .stream()
        .map(LegallyDaysOffMapper.INSTANCE::mapLegallyDaysOffEtyToLegallyDaysOffDto)
        .filter(day -> Arrays.stream(periods).anyMatch(x -> x.equals(periodConvertor(day.getDate()))))
        .collect(Collectors.toList()));
    return legallyDaysList;
  }

  private static String periodConvertor(LocalDate inputPeriod) {
      SimpleDateFormat dateFormat = new SimpleDateFormat(EXPECTED_PERIOD_FORMAT);
      try {
        var date = new SimpleDateFormat(CURRENT_DATE_FORMAT).parse(String.valueOf(inputPeriod));
        return dateFormat.format(date);
      } catch (ParseException e) {
        throw new IllegalStateException("Failed to convert period.");
      }

  }

  private static String yearConvertor(LocalDate inputYear) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(EXPECTED_YEAR_FORMAT);
    try {
      var date = new SimpleDateFormat(CURRENT_DATE_FORMAT).parse(String.valueOf(inputYear));
      return dateFormat.format(date);
    } catch (ParseException e) {
      throw new IllegalStateException("Failed to convert year.");
    }

  }

}
