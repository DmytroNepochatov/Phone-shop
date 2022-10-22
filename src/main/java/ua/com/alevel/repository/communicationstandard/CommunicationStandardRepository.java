package ua.com.alevel.repository.communicationstandard;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.accessory.CommunicationStandard;
import java.util.Optional;

@Repository
public interface CommunicationStandardRepository extends CrudRepository<CommunicationStandard, String> {

    Optional<CommunicationStandard> findFirstByName(String name);
}
