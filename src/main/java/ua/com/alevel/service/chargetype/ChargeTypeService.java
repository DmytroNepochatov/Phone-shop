package ua.com.alevel.service.chargetype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.alevel.model.accessory.ChargeType;
import ua.com.alevel.repository.chargetype.ChargeTypeRepository;
import java.util.Optional;

@Service
public class ChargeTypeService {
    private final ChargeTypeRepository chargeTypeRepository;

    @Autowired
    public ChargeTypeService(ChargeTypeRepository chargeTypeRepository) {
        this.chargeTypeRepository = chargeTypeRepository;
    }

    public Optional<ChargeType> findFirstByName(String name) {
        return chargeTypeRepository.findFirstByName(name);
    }
}
