package ua.com.webservice.service.communicationstandard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.webservice.model.accessory.CommunicationStandard;
import ua.com.webservice.repository.communicationstandard.CommunicationStandardRepository;
import ua.com.webservice.repository.phone.PhoneDescriptionRepository;
import java.util.List;
import java.util.Optional;

@Service
public class CommunicationStandardService {
    private final CommunicationStandardRepository communicationStandardRepository;
    private final PhoneDescriptionRepository phoneDescriptionRepository;

    @Autowired
    public CommunicationStandardService(CommunicationStandardRepository communicationStandardRepository, PhoneDescriptionRepository phoneDescriptionRepository) {
        this.communicationStandardRepository = communicationStandardRepository;
        this.phoneDescriptionRepository = phoneDescriptionRepository;
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
        if (phoneDescriptionRepository.findFirstByCommunicationStandard(communicationStandardRepository.findById(id).get()).isPresent()) {
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
