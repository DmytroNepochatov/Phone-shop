package ua.com.alevel.repository.chargetype;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.accessory.ChargeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChargeTypeRepository extends CrudRepository<ChargeType, String> {

    Optional<ChargeType> findFirstByName(String name);

    @Query("select chargeType.name from ChargeType chargeType order by chargeType.name asc")
    List<String> findAllChargeTypesNames();

    @Query("select chargeType from ChargeType chargeType order by chargeType.name asc")
    List<ChargeType> findAllChargeTypesForAdmin();
}
