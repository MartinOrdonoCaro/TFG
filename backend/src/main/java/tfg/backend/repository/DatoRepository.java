package tfg.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tfg.backend.domain.Dato;
import tfg.backend.domain.Serie;

public interface DatoRepository extends MongoRepository<Dato, Long>{
}
