package ua.com.alevel.service.communicationstandard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.alevel.model.accessory.CommunicationStandard;
import ua.com.alevel.repository.communicationstandard.CommunicationStandardRepository;
import java.util.Optional;

@Service
public class CommunicationStandardService {
    private final CommunicationStandardRepository communicationStandardRepository;

    @Autowired
    public CommunicationStandardService(CommunicationStandardRepository communicationStandardRepository) {
        this.communicationStandardRepository = communicationStandardRepository;
    }

    public Optional<CommunicationStandard> findFirstByName(String name) {
        return communicationStandardRepository.findFirstByName(name);
    }
}
