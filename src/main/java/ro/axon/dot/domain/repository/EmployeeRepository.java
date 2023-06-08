package ro.axon.dot.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ro.axon.dot.domain.entity.EmployeeEty;
import ro.axon.dot.domain.entity.TeamEty;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEty, String>,
    QuerydslPredicateExecutor<EmployeeEty> {

  boolean existsEmployeeEtyByUsernameIgnoreCase(String username);

  boolean existsEmployeeEtyByEmailIgnoreCase(String email);

  Optional<EmployeeEty> findEmployeeEtyByUsername(String username);

  Optional<EmployeeEty> findEmployeeEtyById(String id);

  List<EmployeeEty> findAllByTeam(TeamEty teamEty);
}
