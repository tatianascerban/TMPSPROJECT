package ro.axon.dot.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ro.axon.dot.domain.entity.TeamEty;
import ro.axon.dot.domain.entity.enums.TeamStatus;

public interface TeamRepository extends
    JpaRepository<TeamEty, Long>,
    QuerydslPredicateExecutor<TeamEty> {

  List<TeamEty> findByStatus(TeamStatus status);

  Optional<TeamEty> findByNameIgnoreCase(String name);
}
