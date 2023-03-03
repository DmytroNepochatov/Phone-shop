package ua.com.alevel.service.typescreen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.alevel.model.accessory.TypeScreen;
import ua.com.alevel.repository.phone.PhoneDescriptionRepository;
import ua.com.alevel.repository.typescreen.TypeScreenRepository;
import java.util.List;
import java.util.Optional;

@Service
public class TypeScreenService {
    private final TypeScreenRepository typeScreenRepository;
    private final PhoneDescriptionRepository phoneDescriptionRepository;

    @Autowired
    public TypeScreenService(TypeScreenRepository typeScreenRepository, PhoneDescriptionRepository phoneDescriptionRepository) {
        this.typeScreenRepository = typeScreenRepository;
        this.phoneDescriptionRepository = phoneDescriptionRepository;
    }

    public Optional<TypeScreen> findFirstByName(String name) {
        return typeScreenRepository.findFirstByName(name);
    }

    public List<String> findAllTypeScreensNames() {
        return typeScreenRepository.findAllTypeScreensNames();
    }

    public List<TypeScreen> findAllTypeScreensForAdmin() {
        return typeScreenRepository.findAllTypeScreensForAdmin();
    }

    public boolean delete(String id) {
        if (phoneDescriptionRepository.findFirstByTypeScreen(typeScreenRepository.findById(id).get()).isPresent()) {
            return false;
        }
        else {
            typeScreenRepository.deleteById(id);
            return true;
        }
    }

    public boolean save(TypeScreen typeScreen) {
        if (typeScreenRepository.findFirstByName(typeScreen.getName()).isPresent()) {
            return false;
        }
        else {
            typeScreenRepository.save(typeScreen);
            return true;
        }
    }
}