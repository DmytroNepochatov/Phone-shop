package ua.com.webservice.repository.processor;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.webservice.model.accessory.Processor;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessorRepository extends CrudRepository<Processor, String> {

    Optional<Processor> findFirstByName(String name);

    @Query("select processor.name from Processor processor order by processor.name asc")
    List<String> findAllProcessorsNames();

    @Query("select processor from Processor processor order by processor.name asc")
    List<Processor> findAllProcessorsForAdmin();
}
