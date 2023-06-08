package ro.axon.dot.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ro.axon.dot.domain.entity.DaysOffEty;
import ro.axon.dot.domain.entity.EmployeeEty;


public interface DaysOffRepository extends JpaRepository<DaysOffEty, Long>,
    QuerydslPredicateExecutor<DaysOffEty> {

  Optional<DaysOffEty> findByEmployee(EmployeeEty employee);

}
