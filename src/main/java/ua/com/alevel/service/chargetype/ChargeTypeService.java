package ua.com.alevel.service.chargetype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.alevel.model.accessory.ChargeType;
import ua.com.alevel.repository.chargetype.ChargeTypeRepository;
import ua.com.alevel.repository.phone.PhoneRepository;
import java.util.List;
import java.util.Optional;

@Service
public class ChargeTypeService {
    private final ChargeTypeRepository chargeTypeRepository;
    private final PhoneRepository phoneRepository;

    @Autowired
    public ChargeTypeService(ChargeTypeRepository chargeTypeRepository, PhoneRepository phoneRepository) {
        this.chargeTypeRepository = chargeTypeRepository;
        this.phoneRepository = phoneRepository;
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
        if (phoneRepository.findFirstByChargeType(chargeTypeRepository.findById(id).get()).isPresent()) {
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
