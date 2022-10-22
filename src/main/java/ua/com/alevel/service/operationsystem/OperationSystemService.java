package ua.com.alevel.service.operationsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.alevel.model.accessory.OperationSystem;
import ua.com.alevel.repository.operationsystem.OperationSystemRepository;
import java.util.Optional;

@Service
public class OperationSystemService {
    private final OperationSystemRepository operationSystemRepository;

    @Autowired
    public OperationSystemService(OperationSystemRepository operationSystemRepository) {
        this.operationSystemRepository = operationSystemRepository;
    }

    public Optional<OperationSystem> findFirstByName(String name) {
        return operationSystemRepository.findFirstByName(name);
    }
}
