package ua.com.alevel.repository.phone;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.accessory.*;
import ua.com.alevel.model.phone.PhoneDescription;
import java.util.Optional;

@Repository
public interface PhoneDescriptionRepository extends CrudRepository<PhoneDescription, String>, PagingAndSortingRepository<PhoneDescription, String> {

    Optional<PhoneDescription> findFirstByBrand(Brand brand);

    Optional<PhoneDescription> findFirstByChargeType(ChargeType chargeType);

    Optional<PhoneDescription> findFirstByCommunicationStandard(CommunicationStandard communicationStandard);

    Optional<PhoneDescription> findFirstByOperationSystem(OperationSystem operationSystem);

    Optional<PhoneDescription> findFirstByTypeScreen(TypeScreen typeScreen);

    Optional<PhoneDescription> findFirstByProcessor(Processor processor);

    Optional<PhoneDescription> findFirstByBrandAndNameAndSeries(Brand brand, String name, String series);
}
