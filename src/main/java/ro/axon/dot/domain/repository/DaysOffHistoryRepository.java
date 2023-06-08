package ro.axon.dot.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ro.axon.dot.domain.entity.DaysOffHistoryEty;

public interface DaysOffHistoryRepository extends JpaRepository<DaysOffHistoryEty, Long>,
    QuerydslPredicateExecutor<DaysOffHistoryEty> {

}
