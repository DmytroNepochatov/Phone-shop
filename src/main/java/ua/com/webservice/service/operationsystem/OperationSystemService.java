package ua.com.webservice.service.operationsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.webservice.model.accessory.OperationSystem;
import ua.com.webservice.repository.operationsystem.OperationSystemRepository;
import ua.com.webservice.repository.phone.PhoneDescriptionRepository;
import java.util.List;
import java.util.Optional;

@Service
public class OperationSystemService {
    private final OperationSystemRepository operationSystemRepository;
    private final PhoneDescriptionRepository phoneDescriptionRepository;

    @Autowired
    public OperationSystemService(OperationSystemRepository operationSystemRepository, PhoneDescriptionRepository phoneDescriptionRepository) {
        this.operationSystemRepository = operationSystemRepository;
        this.phoneDescriptionRepository = phoneDescriptionRepository;
    }

    public Optional<OperationSystem> findFirstByName(String name) {
        return operationSystemRepository.findFirstByName(name);
    }

    public List<String> findAllOperationSystemsNames() {
        return operationSystemRepository.findAllOperationSystemsNames();
    }

    public List<OperationSystem> findAllOperationSystemsForAdmin() {
        return operationSystemRepository.findAllOperationSystemsForAdmin();
    }

    public boolean delete(String id) {
        if (phoneDescriptionRepository.findFirstByOperationSystem(operationSystemRepository.findById(id).get()).isPresent()) {
            return false;
        }
        else {
            operationSystemRepository.deleteById(id);
            return true;
        }
    }

    public boolean save(OperationSystem operationSystem) {
        if (operationSystemRepository.findFirstByName(operationSystem.getName()).isPresent()) {
            return false;
        }
        else {
            operationSystemRepository.save(operationSystem);
            return true;
        }
    }
}
