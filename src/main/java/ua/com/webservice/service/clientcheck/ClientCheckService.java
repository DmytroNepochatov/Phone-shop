package ua.com.webservice.service.clientcheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.webservice.model.check.ClientCheck;
import ua.com.webservice.model.dto.SalesSettingsForSpecificModels;
import ua.com.webservice.model.dto.SalesSettingsForSpecificModelsMonths;
import ua.com.webservice.model.dto.SalesSettingsForSpecificModelsParams;
import ua.com.webservice.repository.clientcheck.ClientCheckRepository;
import ua.com.webservice.repository.phone.PhoneInstanceRepository;
import ua.com.webservice.util.Util;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ClientCheckService {
    private final ClientCheckRepository clientCheckRepository;
    private final PhoneInstanceRepository phoneInstanceRepository;
    private static final String DATE_PATTERN = "dd.M.yyyy HH:mm:ss";
    private static final String START_DAY = " 00:00:00";
    private static final String END_DAY = " 23:59:59";
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientCheckService.class);

    @Autowired
    public ClientCheckService(ClientCheckRepository clientCheckRepository, PhoneInstanceRepository phoneInstanceRepository) {
        this.clientCheckRepository = clientCheckRepository;
        this.phoneInstanceRepository = phoneInstanceRepository;
    }

    public SalesSettingsForSpecificModelsParams getStoreEarningsByMonthAndYear(SalesSettingsForSpecificModels salesSettingsForSpecificModels) throws Exception {
        SalesSettingsForSpecificModelsParams salesSettingsForSpecificModelsParam = new SalesSettingsForSpecificModelsParams(null, new ArrayList<>());
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);

        int monthCount = 0;
        boolean checkTempYear = false;

        if (LocalDate.now().getYear() == Integer.parseInt(salesSettingsForSpecificModels.getYear())) {
            monthCount = LocalDate.now().getMonthValue();
            checkTempYear = true;
        }
        else {
            monthCount = 12;
        }

        for (int i = 0; i < monthCount; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(salesSettingsForSpecificModels.getYear()), i, 1);
            calendar.add(Calendar.MONTH, 0);

            int lastDayInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            int tempMonth = i + 1;

            if (checkTempYear && LocalDate.now().getMonthValue() == i + 1) {
                lastDayInMonth = LocalDate.now().getDayOfMonth();
            }

            Date startDate = formatter.parse("1." + tempMonth + "." + salesSettingsForSpecificModels.getYear() + START_DAY);
            Date endDate = formatter.parse(lastDayInMonth + "." + tempMonth + "." + salesSettingsForSpecificModels.getYear() + END_DAY);

            AtomicInteger priceForMonth = new AtomicInteger(0);

            clientCheckRepository.findAllClientChecksBetweenDates(startDate, endDate).forEach(clientCheck ->
                    priceForMonth.addAndGet(phoneInstanceRepository.findPriceForClientCheckId(clientCheck.getId()) * 100)
            );

            SalesSettingsForSpecificModelsMonths salesSettingsForSpecificModelsMonths = new SalesSettingsForSpecificModelsMonths();
            salesSettingsForSpecificModelsMonths.setMonth(Util.getMonth(tempMonth));
            salesSettingsForSpecificModelsMonths.setSold(priceForMonth.get());

            salesSettingsForSpecificModelsParam.getFields().add(salesSettingsForSpecificModelsMonths);
        }

        return salesSettingsForSpecificModelsParam;
    }

    public void save(ClientCheck clientCheck) {
        clientCheckRepository.save(clientCheck);
    }

    public Optional<ClientCheck> findClientCheckForUserIdForNewOrder(String userId, Date date) {
        return clientCheckRepository.findClientCheckForUserIdForNewOrder(userId, date);
    }

    public Optional<String> findDefaultCheckIdForUserEmail(String email) {
        return clientCheckRepository.findDefaultCheckIdForUserEmail(email);
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
    }

    public List<ClientCheck> findAllChecksForUserId(String userId) {
        return clientCheckRepository.findAllChecksForUserId(userId);
    }

    public String getUserIdForCheckId(String checkId) {
        return clientCheckRepository.getUserIdForCheckId(checkId);
    }
}
