package ua.com.alevel.service.clientcheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public Optional<ClientCheck> findById(String id) {
        return clientCheckRepository.findById(id);
    }

    public void updateCheckClosed(boolean isClosed, String id) {
        clientCheckRepository.updateCheckClosed(isClosed, new Date(), id);
        LOGGER.info("Check with id {} closed", id);
    }

    public void cancelCheck(String id) {
        clientCheckRepository.deleteByClientCheckId(id);
        LOGGER.info("Check with id {} canceled", id);
    }

    public List<ClientCheck> findAllChecksForUserId(String userId) {
        return clientCheckRepository.findAllChecksForUserId(userId);
    }

    public String getUserIdForCheckId(String checkId) {
        return clientCheckRepository.getUserIdForCheckId(checkId);
    }
}
