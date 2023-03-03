package ua.com.alevel.service.phonedescription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.alevel.model.phone.PhoneDescription;
import ua.com.alevel.repository.phone.PhoneDescriptionRepository;
import ua.com.alevel.repository.phone.PhoneRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PhoneDescriptionService {
    private final PhoneDescriptionRepository phoneDescriptionRepository;
    private final PhoneRepository phoneRepository;

    @Autowired
    public PhoneDescriptionService(PhoneDescriptionRepository phoneDescriptionRepository, PhoneRepository phoneRepository) {
        this.phoneDescriptionRepository = phoneDescriptionRepository;
        this.phoneRepository = phoneRepository;
    }

    public List<PhoneDescription> findAllPhoneDescriptions() {
        List<PhoneDescription> phoneDescriptions = new ArrayList<>();
        phoneDescriptionRepository.findAll().forEach(phoneDescription -> phoneDescriptions.add(phoneDescription));
        Collections.sort(phoneDescriptions);

        return phoneDescriptions;
    }

    public PhoneDescription findById(String id) {
        return phoneDescriptionRepository.findById(id).get();
    }

    public boolean delete(String id) {
        if (phoneRepository.findFirstByPhoneDescription(phoneDescriptionRepository.findById(id).get()).isPresent()) {
            return false;
        }
        else {
            phoneDescriptionRepository.deleteById(id);
            return true;
        }
    }

    public boolean save(PhoneDescription phoneDescription) {
        if (phoneDescriptionRepository.findFirstByBrandAndNameAndSeries(phoneDescription.getBrand(), phoneDescription.getName(), phoneDescription.getSeries())
                .isPresent()) {
            return false;
        }
        else {
            phoneDescriptionRepository.save(phoneDescription);
            return true;
        }
    }

    public void update(PhoneDescription phoneDescription) {
        phoneDescriptionRepository.save(phoneDescription);
    }
}
