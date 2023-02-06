package ua.com.alevel.service.phone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.alevel.mapper.FilterMapper;
import ua.com.alevel.mapper.PhoneMapper;
import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.model.dto.*;
import ua.com.alevel.model.dto.filterparams.*;
import ua.com.alevel.model.phone.Phone;
import ua.com.alevel.model.rating.Rating;
import ua.com.alevel.model.shoppingcart.ShoppingCart;
import ua.com.alevel.repository.phone.PhoneRepository;
import ua.com.alevel.repository.phone.PhoneRepositoryCriteria;
import ua.com.alevel.repository.rating.RatingRepository;

import java.util.*;

@Service
public class PhoneService {
    private final PhoneRepository phoneRepository;
    private final RatingRepository ratingRepository;
    private final PhoneRepositoryCriteria phoneRepositoryCriteria;
    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneService.class);
    private static final int NEED_PHONES = 40;

    @Autowired
    public PhoneService(PhoneRepository phoneRepository, RatingRepository ratingRepository,
                        @Qualifier("phoneRepositoryCriteriaImpl") PhoneRepositoryCriteria phoneRepositoryCriteria) {
        this.phoneRepository = phoneRepository;
        this.ratingRepository = ratingRepository;
        this.phoneRepositoryCriteria = phoneRepositoryCriteria;
    }

    @Transactional
    public boolean save(Phone phone) {
        if (phoneRepository.findFirstByImei(phone.getImei()).isPresent()) {
            LOGGER.warn("Phone {} has an imei that is in the database", phone);
            return false;
        }
        else {
            Optional<Phone> findablePhone = phoneRepository.findFirstByBrandAndNameAndSeriesAndAmountOfBuiltInMemoryAndAmountOfRamAndRatingNotNull(
                    phone.getBrand(),
                    phone.getName(),
                    phone.getSeries(),
                    phone.getAmountOfBuiltInMemory(),
                    phone.getAmountOfRam());

            findablePhone.ifPresentOrElse(
                    value -> phone.setRating(value.getRating()),
                    () -> {
                        Rating rating = new Rating();
                        rating.setNumberOfPoints(1);
                        rating.setTotalPoints(0);
                        rating.setPhones(List.of(phone));
                        ratingRepository.save(rating);

                        phone.setRating(rating);
                        LOGGER.info("Rating for {} was successfully created", phone);
                    }
            );

            phoneRepository.save(phone);
            LOGGER.info("Phone {} was successfully saved", phone);
            return true;
        }
    }

    @Transactional
    public void delete(String id) {
        phoneRepository.deleteById(id);
        LOGGER.info("Phone with id {} was successfully deleted", id);
    }

    public List<Phone> findAllForAdmin() {
        return phoneRepository.findAllByClientCheckNullAndShoppingCartNull();
    }

    public PhonesForMainViewList findAllForMainView(int page) {
        List<PhoneForMainView> phonesForMainView = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, NEED_PHONES);

        PhoneMapper.mapPhoneToPhoneForMainView(phoneRepository, phonesForMainView, pageable);
        int pages = getPagesCount();

        return new PhonesForMainViewList(phonesForMainView, pages);
    }

    public PhonesForMainViewList sortByPriceAsc(int page) {
        List<PhoneForMainView> phonesForMainView = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, NEED_PHONES, Sort.by("price"));

        PhoneMapper.mapPhoneToPhoneForMainView(phoneRepository, phonesForMainView, pageable);
        int pages = getPagesCount();

        return new PhonesForMainViewList(phonesForMainView, pages);
    }

    public PhonesForMainViewList sortByPriceDesc(int page) {
        List<PhoneForMainView> phonesForMainView = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, NEED_PHONES, Sort.by("price").descending());

        PhoneMapper.mapPhoneToPhoneForMainView(phoneRepository, phonesForMainView, pageable);
        int pages = getPagesCount();

        return new PhonesForMainViewList(phonesForMainView, pages);
    }

    public PhonesForMainViewList findBySearch(int page, String[] searchParam) {
        List<PhoneForMainView> phonesForMainView = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, NEED_PHONES);

        PhoneMapper.mapPhoneToPhoneForMainViewToFindBySearch(phoneRepository, phonesForMainView, pageable, searchParam);
        int pages = getPagesCountForList(phonesForMainView);

        return new PhonesForMainViewList(phonesForMainView, pages);
    }

    public PhonesForMainViewList filterPhones(int page, String[] searchParam) {
        List<PhoneForMainView> phonesForMainView = new ArrayList<>();

        PhoneMapper.mapPhoneToPhoneForMainViewFilter(phoneRepositoryCriteria, phonesForMainView, page - 1, searchParam, NEED_PHONES);
        int pages = getPagesCountForList(phonesForMainView);

        return new PhonesForMainViewList(phonesForMainView, pages);
    }

    public FullInfoAboutPhone getFullInfoAboutPhone(PhoneForMainView phoneForMainView) {
        List<PhoneColors> phoneColors = new ArrayList<>();

        return new FullInfoAboutPhone(PhoneMapper.mapPhoneForMainViewToPhone(phoneRepository, phoneForMainView),
                PhoneMapper.getColorsForPhone(phoneRepository, phoneForMainView, phoneColors));
    }

    public List<BrandForMainView> findAllAvailableBrand() {
        return FilterMapper.mapBrandToBrandForMainViews(phoneRepository.findAllAvailableBrand());
    }

    public List<ChargeTypeForMainView> findAllAvailableChargeTypes() {
        return FilterMapper.mapChargeTypeToChargeTypeForMainViews(phoneRepository.findAllAvailableChargeTypes());
    }

    public List<CommunicationStandardForMainView> findAllAvailableCommunicationStandards() {
        return FilterMapper.mapCommunicationStandardToCommunicationStandardForMainView(phoneRepository.findAllAvailableCommunicationStandards());
    }

    public List<OperationSystemForMainView> findAllAvailableOperationSystems() {
        return FilterMapper.mapOperationSystemToOperationSystemForMainView(phoneRepository.findAllAvailableOperationSystems());
    }

    public List<ProcessorForMainView> findAllAvailableProcessors() {
        return FilterMapper.mapProcessorToProcessorForMainView(phoneRepository.findAllAvailableProcessors());
    }

    public List<TypeScreenForMainView> findAllAvailableTypeScreens() {
        return FilterMapper.mapTypeScreenToTypeScreenForMainView(phoneRepository.findAllAvailableTypeScreens());
    }

    public List<DiagonalForMainView> findAllAvailableDiagonals() {
        return FilterMapper.mapDiagonalToDiagonalForMainView(phoneRepository.findAllAvailableDiagonals());
    }

    public List<DisplayResolutionForMainView> findAllAvailableDisplayResolutions() {
        return FilterMapper.mapDisplayResolutionToDisplayResolutionForMainView(phoneRepository.findAllAvailableDisplayResolutions());
    }

    public List<ScreenRefreshRateForMainView> findAllAvailableScreenRefreshRates() {
        return FilterMapper.mapScreenRefreshRateToScreenRefreshRateForMainView(phoneRepository.findAllAvailableScreenRefreshRates());
    }

    public List<NumberOfSimCardForMainView> findAllAvailableNumberOfSimCards() {
        return FilterMapper.mapNumberOfSimCardToNumberOfSimCardForMainView(phoneRepository.findAllAvailableNumberOfSimCards());
    }

    public List<AmountOfBuiltInMemoryForMainView> findAllAvailableAmountOfBuiltInMemory() {
        return FilterMapper.mapAmountOfBuiltInMemoryToAmountOfBuiltInMemoryForMainView(phoneRepository.findAllAvailableAmountOfBuiltInMemory());
    }

    public List<AmountOfRamForMainView> findAllAvailableAmountOfRam() {
        return FilterMapper.mapAmountOfRamToAmountOfRamForMainView(phoneRepository.findAllAvailableAmountOfRam());
    }

    public List<NumberOfFrontCameraForMainView> findAllAvailableNumberOfFrontCameras() {
        return FilterMapper.mapNumberOfFrontCameraToNumberOfFrontCameraForMainView(phoneRepository.findAllAvailableNumberOfFrontCameras());
    }

    public List<NumberOfMainCameraForMainView> findAllAvailableNumberOfMainCameras() {
        return FilterMapper.mapNumberOfMainCameraToNumberOfMainCameraForMainView(phoneRepository.findAllAvailableNumberOfMainCameras());
    }

    public List<DegreeOfMoistureProtectionForMainView> findAllAvailableDegreeOfMoistureProtection() {
        return FilterMapper.mapDegreeOfMoistureProtectionToDegreeOfMoistureProtectionForMainView(phoneRepository.findAllAvailableDegreeOfMoistureProtections());
    }

    public List<NfcForMainView> getNfcTypes() {
        List<NfcForMainView> list = new ArrayList<>(2);
        list.add(new NfcForMainView("true", false));
        list.add(new NfcForMainView("false", false));

        return list;
    }

    public List<String> findFirstIdPhoneForShoppingCart(String brand, String name, String series, int amountOfBuiltInMemory,
                                                        int amountOfRam, String color) {
        return phoneRepository.findFirstIdPhoneForShoppingCart(brand, name, series, amountOfBuiltInMemory, amountOfRam, color);
    }

    @Transactional
    public void setShoppingCartForPhone(ShoppingCart shoppingCart, String phoneId) {
        phoneRepository.setShoppingCartForPhone(shoppingCart, phoneId);
        LOGGER.info("Phone {} in shopping cart {}", phoneId, shoppingCart.getId());
    }

    @Transactional
    public void delShoppingCartForPhone(String phoneId) {
        phoneRepository.delShoppingCartForPhone(phoneId);
        LOGGER.info("Phone {} is no longer in the shopping cart", phoneId);
    }

    public double findPriceForPhoneId(String id) {
        return phoneRepository.findPriceForPhoneId(id);
    }

    public List<PhoneForShoppingCart> findAllPhoneForShoppingCartId(String shoppingCartId) {
        List<PhoneForShoppingCart> result = new ArrayList<>();

        return PhoneMapper.mapPhoneToPhoneForShoppingCart(phoneRepository, shoppingCartId, result);
    }

    @Transactional
    public void addPhoneToClientCheck(ClientCheck clientCheck, String phoneId) {
        phoneRepository.addPhoneToClientCheck(clientCheck, phoneId);
        LOGGER.info("Phone {} added to client check {}", phoneId, clientCheck.getId());
    }

    public List<Phone> findAllPhonesForShoppingCartId(String shoppingCartId) {
        return phoneRepository.findAllPhonesForShoppingCartId(shoppingCartId);
    }

    public Phone findById(String id) {
        return phoneRepository.findById(id).get();
    }

    private int getPagesCount() {
        int phonesAvailable = phoneRepository.getPagesCount().get();

        return (phonesAvailable % NEED_PHONES == 0) ? phonesAvailable / NEED_PHONES : (phonesAvailable / NEED_PHONES) + 1;
    }

    private int getPagesCountForList(Collection<PhoneForMainView> phones) {
        int phonesAvailable = phones.size();

        return (phonesAvailable % NEED_PHONES == 0) ? phonesAvailable / NEED_PHONES : (phonesAvailable / NEED_PHONES) + 1;
    }
}