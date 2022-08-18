package repository;

import model.GameResponseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameResponseRepository extends JpaRepository<GameResponseModel, Long> {
}

