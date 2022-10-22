package ua.com.alevel.repository.chargetype;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.accessory.ChargeType;
import java.util.Optional;

@Repository
public interface ChargeTypeRepository extends CrudRepository<ChargeType, String> {

    Optional<ChargeType> findFirstByName(String name);
}
