package ua.com.alevel.service.phone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ua.com.alevel.mapper.FilterMapper;
import ua.com.alevel.mapper.PhoneMapper;
import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.model.dto.*;
import ua.com.alevel.model.dto.filterparams.*;
import ua.com.alevel.model.phone.Phone;
import ua.com.alevel.model.phone.PhoneInstance;
import ua.com.alevel.model.rating.Rating;
import ua.com.alevel.model.shoppingcart.ShoppingCart;
import ua.com.alevel.repository.phone.PhoneInstanceRepository;
import ua.com.alevel.repository.phone.PhoneInstanceRepositoryCriteria;
import ua.com.alevel.repository.phone.PhoneRepository;
import ua.com.alevel.repository.rating.RatingRepository;
import ua.com.alevel.util.Util;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
public class PhoneInstanceService {
    private final PhoneInstanceRepository phoneInstanceRepository;
    private final PhoneRepository phoneRepository;
    private final PhoneInstanceRepositoryCriteria phoneInstanceRepositoryCriteria;
    private final RatingRepository ratingRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneInstanceService.class);
    private static final int NEED_PHONES = 40;
    private static final String DATE_PATTERN = "dd.M.yyyy HH:mm:ss";
    private static final String START_MONTH = "01.01.";
    private static final String START_DAY = " 00:00:00";
    private static final String END_DAY = " 23:59:59";

    @Autowired
    public PhoneInstanceService(@Qualifier("phoneInstanceRepositoryCriteriaImpl") PhoneInstanceRepositoryCriteria phoneInstanceRepositoryCriteria,
                                PhoneInstanceRepository phoneInstanceRepository, PhoneRepository phoneRepository, RatingRepository ratingRepository) {
        this.phoneInstanceRepositoryCriteria = phoneInstanceRepositoryCriteria;
        this.phoneInstanceRepository = phoneInstanceRepository;
        this.phoneRepository = phoneRepository;
        this.ratingRepository = ratingRepository;
    }

    public boolean save(PhoneInstance phoneInstance) {
        if (phoneInstanceRepository.findFirstByImei(phoneInstance.getImei()).isPresent()) {
            LOGGER.warn("Phone {} {} has an imei that is in the database", phoneInstance.getPhone().getPhoneDescription(), phoneInstance);
            return false;
        }
        else {
            phoneInstanceRepository.save(phoneInstance);
            LOGGER.info("Phone {} {} was successfully saved", phoneInstance.getPhone().getPhoneDescription(), phoneInstance);
            return true;
        }
    }

    public Phone savePhone(Phone phone) {
        List<Phone> phones = phoneRepository.findFirstPhoneForSave(phone.getView(), phone.getPhoneDescription(), phone.getAmountOfBuiltInMemory(),
                phone.getAmountOfRam());
        if (!phones.isEmpty()) {
            return phones.get(0);
        }

        List<Phone> phoneList = phoneRepository.findFirstPhoneForRating(phone.getPhoneDescription(), phone.getAmountOfBuiltInMemory(),
                phone.getAmountOfRam());
        if (!phoneList.isEmpty()) {
            phone.setRating(phoneList.get(0).getRating());

            phoneRepository.save(phone);
            return phoneRepository.findFirstPhoneForSave(phone.getView(), phone.getPhoneDescription(), phone.getAmountOfBuiltInMemory(),
                    phone.getAmountOfRam()).get(0);
        }
        else {
            Rating rating = new Rating();
            rating.setNumberOfPoints(1);
            rating.setTotalPoints(0);
            rating.setPhones(List.of(phone));
            ratingRepository.save(rating);

            phone.setRating(rating);
            phoneRepository.save(phone);
            return phoneRepository.findFirstPhoneForSave(phone.getView(), phone.getPhoneDescription(), phone.getAmountOfBuiltInMemory(),
                    phone.getAmountOfRam()).get(0);
        }
    }

    public void delete(Phone phone) {
        if (!phoneInstanceRepository.findAllPhoneInstancesByPhoneAndClientCheck(phone).isEmpty()) {
            phoneInstanceRepository.deletePhoneInstancesByPhoneForAdmin(phone);
        }
        else {
            phoneInstanceRepository.deletePhoneInstancesByPhoneForAdmin(phone);

            if (phoneRepository.findFirstByRating(phone.getRating()).isEmpty()) {
                ratingRepository.deleteById(phone.getRating().getId());
            }

            phoneRepository.deleteById(phone.getId());
        }
    }

    public PhonesForMainViewList findAllForMainView(int page) {
        List<PhoneForMainView> phonesForMainView = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, NEED_PHONES);

        PhoneMapper.mapPhoneToPhoneForMainView(phoneInstanceRepository, phonesForMainView, pageable);
        int pages = getPagesCount();

        return new PhonesForMainViewList(phonesForMainView, pages);
    }

    public PhonesForMainViewList sortByPriceAsc(int page) {
        List<PhoneForMainView> phonesForMainView = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, NEED_PHONES, Sort.by("price"));

        PhoneMapper.mapPhoneToPhoneForMainView(phoneInstanceRepository, phonesForMainView, pageable);
        int pages = getPagesCount();

        return new PhonesForMainViewList(phonesForMainView, pages);
    }

    public PhonesForMainViewList sortByPriceDesc(int page) {
        List<PhoneForMainView> phonesForMainView = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, NEED_PHONES, Sort.by("price").descending());

        PhoneMapper.mapPhoneToPhoneForMainView(phoneInstanceRepository, phonesForMainView, pageable);
        int pages = getPagesCount();

        return new PhonesForMainViewList(phonesForMainView, pages);
    }

    public PhonesForMainViewList findBySearch(String searchStr) {
        List<PhoneForMainView> phonesForMainView = new ArrayList<>();
        PhoneMapper.mapPhoneToPhoneForMainViewToFindBySearch(phoneInstanceRepository, phonesForMainView);

        List<PhoneForMainView> phonesForMainViewFinalList = new ArrayList<>();
        List<Integer> listOccurrences = new ArrayList<>();

        for (int i = 0; i < phonesForMainView.size(); i++) {
            listOccurrences.add(0);
        }

        String[] searchStrArr = searchStr.split(" ");
        for (int i = 0; i < searchStrArr.length; i++) {
            searchStrArr[i].trim();
        }

        for (int i = 0; i < phonesForMainView.size(); i++) {
            for (int j = 0; j < searchStrArr.length; j++) {
                if (phonesForMainView.get(i).getBrand().contains(searchStrArr[j])) {
                    setListOccurrences(listOccurrences, i);
                }
                else if (phonesForMainView.get(i).getName().contains(searchStrArr[j])) {
                    setListOccurrences(listOccurrences, i);
                }
                else if (phonesForMainView.get(i).getSeries().contains(searchStrArr[j])) {
                    setListOccurrences(listOccurrences, i);
                }
            }
        }

        int occurrenceValue = 0;
        if (searchStrArr.length == 1) {
            occurrenceValue = 1;
        }
        else if (searchStrArr.length > 2) {
            occurrenceValue = 3;
        }
        else {
            occurrenceValue = 2;
        }

        for (int i = 0; i < phonesForMainView.size(); i++) {
            if (listOccurrences.get(i) >= occurrenceValue) {
                phonesForMainViewFinalList.add(phonesForMainView.get(i));
            }
        }

        return new PhonesForMainViewList(phonesForMainViewFinalList, 0);
    }

    public PhonesForMainViewList findBySearchAsc(String searchStr) {
        PhonesForMainViewList phones = findBySearch(searchStr);
        Collections.sort(phones.getPhonesForMainView());

        return phones;
    }

    public PhonesForMainViewList findBySearchDesc(String searchStr) {
        PhonesForMainViewList phones = findBySearch(searchStr);
        Collections.sort(phones.getPhonesForMainView(), Collections.reverseOrder());

        return phones;
    }

    public PhonesForMainViewList filterPhones(String[] searchParam) {
        List<PhoneForMainView> phonesForMainView = new ArrayList<>();

        PhoneMapper.mapPhoneToPhoneForMainViewFilter(phoneInstanceRepositoryCriteria, phonesForMainView, searchParam, NEED_PHONES);

        return new PhonesForMainViewList(phonesForMainView, 0);
    }

    public PhonesForMainViewList filterPhonesAsc(String[] searchParam) {
        PhonesForMainViewList phones = filterPhones(searchParam);
        Collections.sort(phones.getPhonesForMainView());

        return phones;
    }

    public PhonesForMainViewList filterPhonesDesc(String[] searchParam) {
        PhonesForMainViewList phones = filterPhones(searchParam);
        Collections.sort(phones.getPhonesForMainView(), Collections.reverseOrder());

        return phones;
    }

    public FullInfoAboutPhone getFullInfoAboutPhone(PhoneForMainView phoneForMainView) {
        List<PhoneColors> phoneColors = new ArrayList<>();

        return new FullInfoAboutPhone(PhoneMapper.mapPhoneForMainViewToPhoneInstance(phoneInstanceRepository, phoneForMainView),
                PhoneMapper.getColorsForPhone(phoneInstanceRepository, phoneForMainView, phoneColors));
    }

    public List<BrandForMainView> findAllAvailableBrand() {
        return FilterMapper.mapBrandToBrandForMainViews(phoneInstanceRepository.findAllAvailableBrand());
    }

    public List<ChargeTypeForMainView> findAllAvailableChargeTypes() {
        return FilterMapper.mapChargeTypeToChargeTypeForMainViews(phoneInstanceRepository.findAllAvailableChargeTypes());
    }

    public List<CommunicationStandardForMainView> findAllAvailableCommunicationStandards() {
        return FilterMapper.mapCommunicationStandardToCommunicationStandardForMainView(phoneInstanceRepository.findAllAvailableCommunicationStandards());
    }

    public List<OperationSystemForMainView> findAllAvailableOperationSystems() {
        return FilterMapper.mapOperationSystemToOperationSystemForMainView(phoneInstanceRepository.findAllAvailableOperationSystems());
    }

    public List<ProcessorForMainView> findAllAvailableProcessors() {
        return FilterMapper.mapProcessorToProcessorForMainView(phoneInstanceRepository.findAllAvailableProcessors());
    }

    public List<TypeScreenForMainView> findAllAvailableTypeScreens() {
        return FilterMapper.mapTypeScreenToTypeScreenForMainView(phoneInstanceRepository.findAllAvailableTypeScreens());
    }

    public List<DiagonalForMainView> findAllAvailableDiagonals() {
        return FilterMapper.mapDiagonalToDiagonalForMainView(phoneInstanceRepository.findAllAvailableDiagonals());
    }

    public List<DisplayResolutionForMainView> findAllAvailableDisplayResolutions() {
        return FilterMapper.mapDisplayResolutionToDisplayResolutionForMainView(phoneInstanceRepository.findAllAvailableDisplayResolutions());
    }

    public List<ScreenRefreshRateForMainView> findAllAvailableScreenRefreshRates() {
        return FilterMapper.mapScreenRefreshRateToScreenRefreshRateForMainView(phoneInstanceRepository.findAllAvailableScreenRefreshRates());
    }

    public List<NumberOfSimCardForMainView> findAllAvailableNumberOfSimCards() {
        return FilterMapper.mapNumberOfSimCardToNumberOfSimCardForMainView(phoneInstanceRepository.findAllAvailableNumberOfSimCards());
    }

    public List<AmountOfBuiltInMemoryForMainView> findAllAvailableAmountOfBuiltInMemory() {
        return FilterMapper.mapAmountOfBuiltInMemoryToAmountOfBuiltInMemoryForMainView(phoneInstanceRepository.findAllAvailableAmountOfBuiltInMemory());
    }

    public List<AmountOfRamForMainView> findAllAvailableAmountOfRam() {
        return FilterMapper.mapAmountOfRamToAmountOfRamForMainView(phoneInstanceRepository.findAllAvailableAmountOfRam());
    }

    public List<NumberOfFrontCameraForMainView> findAllAvailableNumberOfFrontCameras() {
        return FilterMapper.mapNumberOfFrontCameraToNumberOfFrontCameraForMainView(phoneInstanceRepository.findAllAvailableNumberOfFrontCameras());
    }

    public List<NumberOfMainCameraForMainView> findAllAvailableNumberOfMainCameras() {
        return FilterMapper.mapNumberOfMainCameraToNumberOfMainCameraForMainView(phoneInstanceRepository.findAllAvailableNumberOfMainCameras());
    }

    public List<DegreeOfMoistureProtectionForMainView> findAllAvailableDegreeOfMoistureProtection() {
        return FilterMapper.mapDegreeOfMoistureProtectionToDegreeOfMoistureProtectionForMainView(phoneInstanceRepository.findAllAvailableDegreeOfMoistureProtections());
    }

    public List<NfcForMainView> getNfcTypes() {
        List<NfcForMainView> list = new ArrayList<>(2);
        list.add(new NfcForMainView("true", false));
        list.add(new NfcForMainView("false", false));

        return list;
    }

    public List<String> findFirstIdPhoneForShoppingCart(String brand, String name, String series, int amountOfBuiltInMemory,
                                                        int amountOfRam, String color) {
        return phoneInstanceRepository.findFirstIdPhoneForShoppingCart(brand, name, series, amountOfBuiltInMemory, amountOfRam, color);
    }

    public void setShoppingCartForPhone(ShoppingCart shoppingCart, String phoneId) {
        phoneInstanceRepository.setShoppingCartForPhone(shoppingCart, phoneId);
    }

    public void delShoppingCartForPhone(String phoneId) {
        phoneInstanceRepository.delShoppingCartForPhone(phoneId);
    }

    public List<PhoneForShoppingCart> findAllPhoneForShoppingCartId(String shoppingCartId) {
        List<PhoneForShoppingCart> result = new ArrayList<>();

        return PhoneMapper.mapPhoneInstanceToPhoneForShoppingCart(phoneInstanceRepository, shoppingCartId, result);
    }

    public void addPhoneToClientCheck(ClientCheck clientCheck, String phoneId) {
        phoneInstanceRepository.addPhoneToClientCheck(clientCheck, phoneId);
    }

    public List<PhoneInstance> findAllPhonesForShoppingCartId(String shoppingCartId) {
        return phoneInstanceRepository.findAllPhonesForShoppingCartId(shoppingCartId);
    }

    public double findPriceForShoppingCartId(String shoppingCartId) {
        if (phoneInstanceRepository.findAllPhonesForShoppingCartId(shoppingCartId).isEmpty()) {
            return 0.0;
        }

        return phoneInstanceRepository.findPriceForShoppingCartId(shoppingCartId);
    }

    public double findPriceForClientCheckId(String clientCheckId) {
        return phoneInstanceRepository.findPriceForClientCheckId(clientCheckId);
    }

    public List<Phone> findAllPhonesInDb() {
        List<Phone> phones = phoneRepository.findAllPhonesInDb();
        Collections.sort(phones);

        return phones;
    }

    public List<Phone> findAllPhonesInDbForAdmin() {
        List<Phone> phones = new ArrayList<>();
        phoneRepository.findAll().forEach(phones::add);
        Collections.sort(phones);

        return phones;
    }

    public double findPriceForPhoneForAdmin(Phone phone) {
        Optional<PhoneInstance> phoneInstance = phoneInstanceRepository.findFirstByPhoneAndClientCheckIsNull(phone);
        if (phoneInstance.isPresent()) {
            return phoneInstance.get().getPrice();
        }

        List<PhoneInstance> phoneInstances = phoneInstanceRepository.findAllPhoneInstancesByPhoneAndClientCheck(phone);
        if (!phoneInstances.isEmpty()) {
            return phoneInstances.get(0).getPrice();
        }
        else {
            return 0.0;
        }
    }

    public void cancelOrder(String orderId) {
        phoneInstanceRepository.findAllPhoneInstancesByClientCheckId(orderId).forEach(phone -> {
            phone.setClientCheck(null);
            phoneInstanceRepository.save(phone);
        });
    }

    public void goBackToShoppingCart(String orderId, ShoppingCart cart) {
        phoneInstanceRepository.findAllPhoneInstancesByClientCheckId(orderId).forEach(phone -> {
            phone.setClientCheck(null);
            phone.setShoppingCart(cart);
            phoneInstanceRepository.save(phone);
        });
    }

    public int countPhonesInStoreForAdmin(Phone phone) {
        return phoneInstanceRepository.countPhonesInStoreForAdmin(phone);
    }

    public int countPhonesInStoreForAdminForStatistic(Phone phone) {
        return phoneInstanceRepository.countPhonesInStoreForAdminForStatistic(phone);
    }

    public double findPriceForPhoneId(String id) {
        return phoneInstanceRepository.findPriceForPhoneId(id).get(0);
    }

    public Phone findByIdPhone(String id) {
        return phoneRepository.findById(id).get();
    }

    public void updatePhone(Phone phone, int amountOfBuiltInMemoryUpdate, int amountOfRamUpdate) {
        List<Phone> phones = phoneRepository.findAllPhonesForChange(phone.getPhoneDescription().getBrand(), phone.getPhoneDescription().getName(),
                phone.getPhoneDescription().getSeries(), phone.getAmountOfBuiltInMemory(), phone.getAmountOfRam());

        phones.forEach(phoneForChange -> {
            phoneForChange.setAmountOfBuiltInMemory(amountOfBuiltInMemoryUpdate);
            phoneForChange.setAmountOfRam(amountOfRamUpdate);
            phoneRepository.save(phoneForChange);
        });
    }

    public void updatePhoneInstance(Phone phone, int amountOfBuiltInMemoryUpdate, int amountOfRamUpdate, double price) {
        List<PhoneInstance> phoneInstances = phoneInstanceRepository.findAllPhoneInstancesForUpdatePrice(phone.getPhoneDescription().getBrand(),
                phone.getPhoneDescription().getName(), phone.getPhoneDescription().getSeries(),
                amountOfBuiltInMemoryUpdate, amountOfRamUpdate);

        phoneInstances.forEach(phoneInstance -> {
            phoneInstance.setPrice(price);
            phoneInstanceRepository.save(phoneInstance);
        });
    }

    public List<PhoneInstance> findAllForAdmin() {
        List<PhoneInstance> list = phoneInstanceRepository.findAllForAdmin();
        Collections.sort(list);

        return list;
    }

    public void deleteByIdPhoneInstance(String id) {
        phoneInstanceRepository.deleteByIdPhoneInstance(id);
    }

    public List<CustomerPreferencesByAge> getCustomerPreferencesByAge() throws Exception {
        List<CustomerPreferencesByAge> customerPreferencesByAgeList = new ArrayList<>();
        int startAge = LocalDate.now().getYear() - 70;
        boolean flag = false;

        while (!flag) {
            if (startAge - 1 == LocalDate.now().getYear() - 16) {
                int year = LocalDate.now().getYear() - 16;
                customerPreferencesByAgeList.get(customerPreferencesByAgeList.size() - 1).setEndAge(START_MONTH + year);
                flag = true;
            }
            else {
                CustomerPreferencesByAge customerPreferencesByAge = new CustomerPreferencesByAge();
                customerPreferencesByAge.setStartAge(START_MONTH + startAge);
                startAge = startAge + 5;
                customerPreferencesByAge.setEndAge(START_MONTH + startAge);

                customerPreferencesByAgeList.add(customerPreferencesByAge);
            }
        }

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);

        for (CustomerPreferencesByAge customerPreferencesByAge : customerPreferencesByAgeList) {
            customerPreferencesByAge.setPhoneInstances(new ArrayList<>());

            List<PhoneInstance> phoneInstances = phoneInstanceRepository.
                    getCustomerPreferencesByAge(formatter.parse(customerPreferencesByAge.getStartAge() + START_DAY),
                            formatter.parse(customerPreferencesByAge.getEndAge() + END_DAY));

            for (int i = 0; i < phoneInstances.size(); i++) {
                boolean check = true;
                PhoneInstance phoneInstance = phoneInstances.get(i);

                for (int j = i + 1; j < phoneInstances.size(); j++) {
                    if (phoneInstance.equals(phoneInstances.get(j))) {
                        check = false;
                    }
                }

                if (check) {
                    customerPreferencesByAge.getPhoneInstances().add(phoneInstance);
                }
            }

            customerPreferencesByAge.setStartAge(Util.getAgeFromDate(customerPreferencesByAge.getStartAge()));
            customerPreferencesByAge.setEndAge(Util.getAgeFromDate(customerPreferencesByAge.getEndAge()));
        }

        return customerPreferencesByAgeList;
    }

    public List<SalesSettingsForSpecificModelsParams> getSalesSettingsForSpecificModelsParams(SalesSettingsForSpecificModels salesSettingsForSpecificModels) throws Exception {
        Phone phoneFindable = phoneRepository.findById(salesSettingsForSpecificModels.getId()).get();

        List<Phone> phones = phoneRepository.findAllPhonesForChange(phoneFindable.getPhoneDescription().getBrand(), phoneFindable.getPhoneDescription().getName(),
                phoneFindable.getPhoneDescription().getSeries(), phoneFindable.getAmountOfBuiltInMemory(), phoneFindable.getAmountOfRam());

        List<SalesSettingsForSpecificModelsParams> salesSettingsForSpecificModelsParamsList = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);

        int monthCount = 0;
        boolean checkTempYear = false;

        for (Phone phone : phones) {
            SalesSettingsForSpecificModelsParams settings = new SalesSettingsForSpecificModelsParams(phone, new ArrayList<>());

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

                SalesSettingsForSpecificModelsMonths salesSettingsForSpecificModelsMonths = new SalesSettingsForSpecificModelsMonths();
                salesSettingsForSpecificModelsMonths.setMonth(Util.getMonth(tempMonth));
                salesSettingsForSpecificModelsMonths.setSold(phoneInstanceRepository.soldSpecificModelsMonth(phone, startDate, endDate));

                settings.getFields().add(salesSettingsForSpecificModelsMonths);
            }

            salesSettingsForSpecificModelsParamsList.add(settings);
        }

        return salesSettingsForSpecificModelsParamsList;
    }

    public List<MostPopularPhoneModels> getMostPopularPhoneModels(String year) throws Exception {
        List<Phone> phones = phoneRepository.findAllPhonesInDb();

        List<MostPopularPhoneModels> mostPopularPhoneModelsList = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);

        int monthCount = 0;
        boolean checkTempYear = false;

        for (Phone phone : phones) {
            if (LocalDate.now().getYear() == Integer.parseInt(year)) {
                monthCount = LocalDate.now().getMonthValue();
                checkTempYear = true;
            }
            else {
                monthCount = 12;
            }

            for (int i = 0; i < monthCount; i++) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Integer.parseInt(year), i, 1);
                calendar.add(Calendar.MONTH, 0);

                int lastDayInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                int tempMonth = i + 1;

                if (checkTempYear && LocalDate.now().getMonthValue() == i + 1) {
                    lastDayInMonth = LocalDate.now().getDayOfMonth();
                }

                Date startDate = formatter.parse("1." + tempMonth + "." + year + START_DAY);
                Date endDate = formatter.parse(lastDayInMonth + "." + tempMonth + "." + year + END_DAY);

                MostPopularPhoneModels mostPopularPhoneModels = new MostPopularPhoneModels();
                mostPopularPhoneModels.setPhone(phone);
                mostPopularPhoneModels.setMonth(tempMonth + "");
                mostPopularPhoneModels.setSold(phoneInstanceRepository.soldMostPopularPhoneModelsMonth(phone.getPhoneDescription().getBrand(),
                        phone.getPhoneDescription().getName(), phone.getPhoneDescription().getSeries(), phone.getAmountOfBuiltInMemory(), phone.getAmountOfRam(), startDate, endDate));

                mostPopularPhoneModelsList.add(mostPopularPhoneModels);
            }
        }

        List<MostPopularPhoneModels> result = new ArrayList<>();
        int month = 1;

        while (month != monthCount + 1) {
            MostPopularPhoneModels max = new MostPopularPhoneModels();
            max.setSold(-1);

            for (MostPopularPhoneModels mostPopularPhoneModels : mostPopularPhoneModelsList) {
                if (mostPopularPhoneModels.getSold() > max.getSold() &&
                        Integer.parseInt(mostPopularPhoneModels.getMonth()) == month) {
                    max.setSold(mostPopularPhoneModels.getSold());
                    max.setPhone(mostPopularPhoneModels.getPhone());
                    max.setMonth(mostPopularPhoneModels.getMonth());
                }
            }

            max.setMonth(Util.getMonth(month));
            result.add(max);

            month++;
        }

        return result;
    }

    public List<PhoneForStoreComposition> getPhonesWithBadSales(String startDate, String endDate, SimpleDateFormat formatter,
                                                                SimpleDateFormat formatterForDateAdded, List<String> years, int countPhonesForBadSales) throws Exception {
        List<PhoneForStoreComposition> phoneForStoreCompositions = new ArrayList<>();

        phoneRepository.findAllPhonesWithBetweenTime(formatter.parse(startDate), formatter.parse(endDate)).forEach(phone -> {
            try {
                int countMonth = 0;
                int checkCountBadSales = 0;

                Date endDateTemp = formatter.parse(endDate);

                for (int i = 2; i != countPhonesForBadSales + 2; countMonth -= 2, i += 2) {
                    if (phoneInstanceRepository.soldSpecificModelsMonth(phone,
                            Util.getDateMinusTwoMonth(endDateTemp, countMonth - 2),
                            Util.getDateMinusTwoMonth(endDateTemp, countMonth)) < i) {
                        checkCountBadSales++;
                    }
                }

                if (checkCountBadSales == 6) {
                    PhoneForStoreComposition phoneForStoreComposition = new PhoneForStoreComposition();
                    phoneForStoreComposition.setPhone(phone);
                    phoneForStoreComposition.setPrice(findPriceForPhoneForAdmin(phone));
                    phoneForStoreComposition.setCountInStore(phoneInstanceRepository.countPhonesInStoreForAdminForStatistic(phone));

                    phoneForStoreCompositions.add(phoneForStoreComposition);
                    years.add(formatterForDateAdded.format(phone.getPhoneDescription().getDateAddedToDatabase()));
                }
            }
            catch (Exception e) {
            }
        });

        phoneForStoreCompositions.sort(Comparator.comparing(o -> o.getPhone().getPhoneDescription()));

        return phoneForStoreCompositions;
    }

    public Optional<PhoneInstance> findPhoneByImei(String imei) {
        return phoneInstanceRepository.findFirstByImei(imei);
    }

    private void setListOccurrences(List<Integer> listOccurrences, int i) {
        int value = listOccurrences.get(i);
        listOccurrences.set(i, value + 1);
    }

    private int getPagesCount() {
        int phonesAvailable = phoneInstanceRepository.getPagesCount().get();

        return (phonesAvailable % NEED_PHONES == 0) ? phonesAvailable / NEED_PHONES : (phonesAvailable / NEED_PHONES) + 1;
    }
}