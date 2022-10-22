package ua.com.alevel.repository.processor;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.accessory.Processor;
import java.util.Optional;

@Repository
public interface ProcessorRepository extends CrudRepository<Processor, String> {

    Optional<Processor> findFirstByName(String name);
}
