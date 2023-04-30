package ua.com.webservice.repository.communicationstandard;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.webservice.model.accessory.CommunicationStandard;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommunicationStandardRepository extends CrudRepository<CommunicationStandard, String> {

    Optional<CommunicationStandard> findFirstByName(String name);

    @Query("select communicationStandard.name from CommunicationStandard communicationStandard order by communicationStandard.name asc")
    List<String> findAllCommunicationStandardsNames();

    @Query("select communicationStandard from CommunicationStandard communicationStandard order by communicationStandard.name asc")
    List<CommunicationStandard> findAllCommunicationStandardsForAdmin();
}
