package tfg.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tfg.backend.domain.Dato;

public interface DatoRepository extends MongoRepository<Dato, Long>{
}
