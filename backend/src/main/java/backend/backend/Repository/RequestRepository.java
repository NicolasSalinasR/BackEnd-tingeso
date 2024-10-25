package backend.backend.Repository;

import backend.backend.Entity.HistoryCountEntity;
import backend.backend.Entity.RequestEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository


public interface RequestRepository extends JpaRepository<RequestEntity, Long> {

    @Transactional
    List<RequestEntity> findAllByClientId(Long ClientId);


}
