package ua.com.webservice.service.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.webservice.model.accessory.Processor;
import ua.com.webservice.repository.phone.PhoneDescriptionRepository;
import ua.com.webservice.repository.processor.ProcessorRepository;
import java.util.List;
import java.util.Optional;

@Service
public class ProcessorService {
    private final ProcessorRepository processorRepository;
    private final PhoneDescriptionRepository phoneDescriptionRepository;

    @Autowired
    public ProcessorService(ProcessorRepository processorRepository, PhoneDescriptionRepository phoneDescriptionRepository) {
        this.processorRepository = processorRepository;
        this.phoneDescriptionRepository = phoneDescriptionRepository;
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
        if (phoneDescriptionRepository.findFirstByProcessor(processorRepository.findById(id).get()).isPresent()) {
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