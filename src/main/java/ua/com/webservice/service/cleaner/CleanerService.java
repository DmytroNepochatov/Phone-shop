package ua.com.webservice.service.cleaner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.com.webservice.model.check.ClientCheck;
import ua.com.webservice.model.phone.PhoneInstance;
import ua.com.webservice.repository.clientcheck.ClientCheckRepository;
import ua.com.webservice.repository.phone.PhoneInstanceRepository;
import java.util.List;

@Service
public class CleanerService {
    private final ClientCheckRepository clientCheckRepository;
    private final PhoneInstanceRepository phoneInstanceRepository;

    @Autowired
    public CleanerService(ClientCheckRepository clientCheckRepository, PhoneInstanceRepository phoneInstanceRepository) {
        this.clientCheckRepository = clientCheckRepository;
        this.phoneInstanceRepository = phoneInstanceRepository;
    }

    @Scheduled(cron = "0 0 0 1/14 * ?")
    public void cleanShoppingCarts() {
        List<PhoneInstance> phoneInstances = phoneInstanceRepository.findAllPhoneInstancesWhichInCartForCleaner();

        phoneInstances.forEach(phone -> phoneInstanceRepository.delShoppingCartForPhone(phone.getId()));
    }

    @Scheduled(cron = "0 0 0 1/14 * ?")
    public void cleanDefaultClientChecks() {
        List<ClientCheck> clientChecks = clientCheckRepository.findAllDefaultClientChecksForCleaner();

        clientChecks.forEach(check -> {
            check.getPhoneInstances().forEach(phone -> {
                phone.setClientCheck(null);
                phoneInstanceRepository.save(phone);
            });

            clientCheckRepository.deleteByClientCheckId(check.getId());
        });
    }
}
