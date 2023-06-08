package ro.axon.dot.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ro.axon.dot.domain.entity.LeaveRequestEty;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequestEty, Long>,
    QuerydslPredicateExecutor<LeaveRequestEty> {

}
