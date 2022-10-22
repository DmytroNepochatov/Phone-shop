package ua.com.alevel.service.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.alevel.model.accessory.Processor;
import ua.com.alevel.repository.processor.ProcessorRepository;
import java.util.Optional;

@Service
public class ProcessorService {
    private final ProcessorRepository processorRepository;

    @Autowired
    public ProcessorService(ProcessorRepository processorRepository) {
        this.processorRepository = processorRepository;
    }

    public Optional<Processor> findFirstByName(String name) {
        return processorRepository.findFirstByName(name);
    }
}
