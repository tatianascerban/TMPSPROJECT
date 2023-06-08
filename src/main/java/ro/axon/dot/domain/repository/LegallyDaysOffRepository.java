package ro.axon.dot.domain.repository;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ro.axon.dot.domain.entity.LegallyDaysOffEty;

@Repository
public interface LegallyDaysOffRepository extends JpaRepository<LegallyDaysOffEty, LocalDate>,
    QuerydslPredicateExecutor<LegallyDaysOffEty> {

}
