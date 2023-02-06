package ua.com.alevel.service.operationsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.alevel.model.accessory.OperationSystem;
import ua.com.alevel.repository.operationsystem.OperationSystemRepository;
import ua.com.alevel.repository.phone.PhoneRepository;
import java.util.List;
import java.util.Optional;

@Service
public class OperationSystemService {
    private final OperationSystemRepository operationSystemRepository;
    private final PhoneRepository phoneRepository;

    @Autowired
    public OperationSystemService(OperationSystemRepository operationSystemRepository, PhoneRepository phoneRepository) {
        this.operationSystemRepository = operationSystemRepository;
        this.phoneRepository = phoneRepository;
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
        if (phoneRepository.findFirstByOperationSystem(operationSystemRepository.findById(id).get()).isPresent()) {
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
