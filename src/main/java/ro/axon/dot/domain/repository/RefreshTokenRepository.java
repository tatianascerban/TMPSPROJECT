package ro.axon.dot.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ro.axon.dot.domain.entity.RefreshTokenEty;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEty, String>,
    QuerydslPredicateExecutor<RefreshTokenEty> {

  Optional<RefreshTokenEty> findRefreshTokenEtyById(String id);

}
