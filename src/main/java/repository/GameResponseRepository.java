package repository;

import model.GameResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameResponseRepository extends JpaRepository<GameResponse, Long> {
}

