package backend.backend.Repository;

import backend.backend.Entity.HistoryCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface HistoryCountRepository extends JpaRepository<HistoryCountEntity, Long> {


    List<HistoryCountEntity> findAllByClientid(Long clientid);
}
