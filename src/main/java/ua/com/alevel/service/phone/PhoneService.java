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
import ua.com.alevel.model.dto.*;
import ua.com.alevel.model.phone.Phone;
import ua.com.alevel.model.rating.Rating;
import ua.com.alevel.repository.phone.PhoneRepository;
import ua.com.alevel.repository.phone.PhoneRepositoryCriteria;
import ua.com.alevel.repository.rating.RatingRepository;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PhoneService {
    private final PhoneRepository phoneRepository;
    private final RatingRepository ratingRepository;
    private final PhoneRepositoryCriteria phoneRepositoryCriteria;
    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneService.class);
    private static final int NEED_PHONES = 10;

    @Autowired
    public PhoneService(PhoneRepository phoneRepository, RatingRepository ratingRepository,
                        @Qualifier("phoneRepositoryCriteriaImpl") PhoneRepositoryCriteria phoneRepositoryCriteria) {
        this.phoneRepository = phoneRepository;
        this.ratingRepository = ratingRepository;
        this.phoneRepositoryCriteria = phoneRepositoryCriteria;
    }

    @Transactional
    public Phone save(Phone phone) {
        if (phoneRepository.findFirstByImei(phone.getImei()).isPresent()) {
            LOGGER.warn("Phone {} has an imei that is in the database", phone);
            return new Phone();
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
                        rating.setNumberOfPoints(0);
                        rating.setTotalPoints(0);
                        phone.setRating(rating);
                        ratingRepository.save(rating);
                        LOGGER.info("Rating for {} was successfully created", phone);
                    }
            );

            phoneRepository.save(phone);
            LOGGER.info("Phone {} was successfully saved", phone);
            return phone;
        }
    }

    @Transactional
    public Phone update(Phone phone) {
        if (phoneRepository.findFirstByImei(phone.getImei()).isPresent()) {
            LOGGER.warn("Phone {} has an imei that is in the database", phone);
            return new Phone();
        }
        else {
            phoneRepository.save(phone);
            LOGGER.info("Phone {} was successfully updated", phone);
            return phone;
        }
    }

    @Transactional
    public Phone delete(Phone phone) {
        phoneRepository.deleteById(phone.getId());
        LOGGER.info("Phone {} was successfully deleted", phone);
        return phone;
    }

    public List<Phone> findAllForAdmin() {
        return phoneRepository.findAllByClientCheckNull();
    }

    public Object[] findAllForMainView(int page) {
        List<PhoneForMainView> phonesForMainView = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, NEED_PHONES);

        PhoneMapper.mapPhoneToPhoneForMainView(phoneRepository, phonesForMainView, pageable);
        int pages = getPagesCount();

        return new Object[]{phonesForMainView, pages};
    }

    public Object[] sortByPriceAsc(int page) {
        List<PhoneForMainView> phonesForMainView = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, NEED_PHONES, Sort.by("price"));

        PhoneMapper.mapPhoneToPhoneForMainView(phoneRepository, phonesForMainView, pageable);
        int pages = getPagesCount();

        return new Object[]{phonesForMainView, pages};
    }

    public Object[] sortByPriceDesc(int page) {
        List<PhoneForMainView> phonesForMainView = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, NEED_PHONES, Sort.by("price").descending());

        PhoneMapper.mapPhoneToPhoneForMainView(phoneRepository, phonesForMainView, pageable);
        int pages = getPagesCount();

        return new Object[]{phonesForMainView, pages};
    }

    public Object[] findBySearch(int page, String[] searchParam) {
        List<PhoneForMainView> phonesForMainView = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, NEED_PHONES);

        PhoneMapper.mapPhoneToPhoneForMainViewToFindBySearch(phoneRepository, phonesForMainView, pageable, searchParam);
        int pages = getPagesCountForList(phonesForMainView);

        return new Object[]{phonesForMainView, pages};
    }

    public Object[] filterPhones(int page, String[] searchParam) {
        List<PhoneForMainView> phonesForMainView = new ArrayList<>();

        PhoneMapper.mapPhoneToPhoneForMainViewFilter(phoneRepositoryCriteria, phonesForMainView, page - 1, searchParam, NEED_PHONES);
        int pages = getPagesCountForList(phonesForMainView);

        return new Object[]{phonesForMainView, pages};
    }

    public Object[] getFullInfoAboutPhone(PhoneForMainView phoneForMainView) {
        Object[] allInfo = new Object[2];
        List<PhoneColors> phoneColors = new ArrayList<>();

        allInfo[0] = PhoneMapper.mapPhoneForMainViewToPhone(phoneRepository, phoneForMainView);
        allInfo[1] = PhoneMapper.getColorsForPhone(phoneRepository, phoneForMainView, phoneColors);

        return allInfo;
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

    private int getPagesCount(){
        int phonesAvailable = phoneRepository.getPagesCount().get();

        return (phonesAvailable % NEED_PHONES == 0) ? phonesAvailable / NEED_PHONES : (phonesAvailable / NEED_PHONES) + 1;
    }

    private int getPagesCountForList(Collection<PhoneForMainView> phones) {
        int phonesAvailable = phones.size();

        return (phonesAvailable % NEED_PHONES == 0) ? phonesAvailable / NEED_PHONES : (phonesAvailable / NEED_PHONES) + 1;
    }
}