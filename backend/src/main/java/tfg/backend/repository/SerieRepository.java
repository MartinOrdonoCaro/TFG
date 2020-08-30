package tfg.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import tfg.backend.domain.Serie;

import java.util.List;

public interface SerieRepository extends MongoRepository<Serie, Long>{
    @Query
    Page<Serie> findAllBySiglasFuente(
            String siglasFuente, Pageable pageable);

    List<Serie> findByIdIn(List<Long> ids);
}
