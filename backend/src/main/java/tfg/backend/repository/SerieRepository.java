package tfg.backend.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import tfg.backend.domain.Serie;

import java.util.List;

public interface SerieRepository extends MongoRepository<Serie, Long>{
}
