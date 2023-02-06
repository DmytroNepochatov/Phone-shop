package ua.com.alevel.service.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.alevel.model.accessory.Processor;
import ua.com.alevel.repository.phone.PhoneRepository;
import ua.com.alevel.repository.processor.ProcessorRepository;
import java.util.List;
import java.util.Optional;

@Service
public class ProcessorService {
    private final ProcessorRepository processorRepository;
    private final PhoneRepository phoneRepository;

    @Autowired
    public ProcessorService(ProcessorRepository processorRepository, PhoneRepository phoneRepository) {
        this.processorRepository = processorRepository;
        this.phoneRepository = phoneRepository;
    }

    public Optional<Processor> findFirstByName(String name) {
        return processorRepository.findFirstByName(name);
    }

    public List<String> findAllProcessorsNames() {
        return processorRepository.findAllProcessorsNames();
    }

    public List<Processor> findAllProcessorsForAdmin() {
        return processorRepository.findAllProcessorsForAdmin();
    }

    public boolean delete(String id) {
        if (phoneRepository.findFirstByProcessor(processorRepository.findById(id).get()).isPresent()) {
            return false;
        }
        else {
            processorRepository.deleteById(id);
            return true;
        }
    }

    public boolean save(Processor processor) {
        if (processorRepository.findFirstByName(processor.getName()).isPresent()) {
            return false;
        }
        else {
            processorRepository.save(processor);
            return true;
        }
    }
}