package ua.com.webservice.service.chargetype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.webservice.model.accessory.ChargeType;
import ua.com.webservice.repository.chargetype.ChargeTypeRepository;
import ua.com.webservice.repository.phone.PhoneDescriptionRepository;
import java.util.List;
import java.util.Optional;

@Service
public class ChargeTypeService {
    private final ChargeTypeRepository chargeTypeRepository;
    private final PhoneDescriptionRepository phoneDescriptionRepository;

    @Autowired
    public ChargeTypeService(ChargeTypeRepository chargeTypeRepository, PhoneDescriptionRepository phoneDescriptionRepository) {
        this.chargeTypeRepository = chargeTypeRepository;
        this.phoneDescriptionRepository = phoneDescriptionRepository;
    }

    public Optional<ChargeType> findFirstByName(String name) {
        return chargeTypeRepository.findFirstByName(name);
    }

    public List<String> findAllChargeTypesNames() {
        return chargeTypeRepository.findAllChargeTypesNames();
    }

    public List<ChargeType> findAllChargeTypesForAdmin() {
        return chargeTypeRepository.findAllChargeTypesForAdmin();
    }

    public boolean delete(String id) {
        if (phoneDescriptionRepository.findFirstByChargeType(chargeTypeRepository.findById(id).get()).isPresent()) {
            return false;
        }
        else {
            chargeTypeRepository.deleteById(id);
            return true;
        }
    }

    public boolean save(ChargeType chargeType) {
        if (chargeTypeRepository.findFirstByName(chargeType.getName()).isPresent()) {
            return false;
        }
        else {
            chargeTypeRepository.save(chargeType);
            return true;
        }
    }
}
