package ua.com.alevel.service.communicationstandard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.alevel.model.accessory.CommunicationStandard;
import ua.com.alevel.repository.communicationstandard.CommunicationStandardRepository;
import ua.com.alevel.repository.phone.PhoneRepository;
import java.util.List;
import java.util.Optional;

@Service
public class CommunicationStandardService {
    private final CommunicationStandardRepository communicationStandardRepository;
    private final PhoneRepository phoneRepository;

    @Autowired
    public CommunicationStandardService(CommunicationStandardRepository communicationStandardRepository, PhoneRepository phoneRepository) {
        this.communicationStandardRepository = communicationStandardRepository;
        this.phoneRepository = phoneRepository;
    }

    public Optional<CommunicationStandard> findFirstByName(String name) {
        return communicationStandardRepository.findFirstByName(name);
    }

    public List<String> findAllCommunicationStandardsNames() {
        return communicationStandardRepository.findAllCommunicationStandardsNames();
    }

    public List<CommunicationStandard> findAllCommunicationStandardsForAdmin() {
        return communicationStandardRepository.findAllCommunicationStandardsForAdmin();
    }

    public boolean delete(String id) {
        if (phoneRepository.findFirstByCommunicationStandard(communicationStandardRepository.findById(id).get()).isPresent()) {
            return false;
        }
        else {
            communicationStandardRepository.deleteById(id);
            return true;
        }
    }

    public boolean save(CommunicationStandard communicationStandard) {
        if (communicationStandardRepository.findFirstByName(communicationStandard.getName()).isPresent()) {
            return false;
        }
        else {
            communicationStandardRepository.save(communicationStandard);
            return true;
        }
    }
}
