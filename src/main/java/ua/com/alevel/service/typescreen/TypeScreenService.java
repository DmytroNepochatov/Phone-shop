package ua.com.alevel.service.typescreen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.alevel.model.accessory.TypeScreen;
import ua.com.alevel.repository.typescreen.TypeScreenRepository;
import java.util.Optional;

@Service
public class TypeScreenService {
    private final TypeScreenRepository typeScreenRepository;

    @Autowired
    public TypeScreenService(TypeScreenRepository typeScreenRepository) {
        this.typeScreenRepository = typeScreenRepository;
    }

    public Optional<TypeScreen> findFirstByName(String name) {
        return typeScreenRepository.findFirstByName(name);
    }
}
