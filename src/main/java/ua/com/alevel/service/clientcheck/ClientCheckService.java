package ua.com.alevel.service.clientcheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.repository.clientcheck.ClientCheckRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ClientCheckService {
    private final ClientCheckRepository clientCheckRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCheckService.class);

    @Autowired
    public ClientCheckService(ClientCheckRepository clientCheckRepository) {
        this.clientCheckRepository = clientCheckRepository;
    }

    @Transactional
    public void save(ClientCheck clientCheck) {
        clientCheckRepository.save(clientCheck);
        LOGGER.info("Client check for user {} saved", clientCheck.getRegisteredUser().getId());
    }

    public Optional<ClientCheck> findClientCheckForUserIdForNewOrder(String userId, Date date) {
        return clientCheckRepository.findClientCheckForUserIdForNewOrder(userId, date);
    }

    public List<ClientCheck> findAllNoClosedChecksForUserId(String userId) {
        return clientCheckRepository.findAllNoClosedChecksForUserId(userId);
    }

    public List<ClientCheck> findAllClosedChecksForUserId(String userId) {
        return clientCheckRepository.findAllClosedChecksForUserId(userId);
    }

    public ClientCheck findById(String id) {
        return clientCheckRepository.findById(id).get();
    }

    @Transactional
    public void updateCheckClosed(boolean isClosed, String id) {
        clientCheckRepository.updateCheckClosed(isClosed, id);
        LOGGER.info("Check with id {} closed", id);
    }
}
