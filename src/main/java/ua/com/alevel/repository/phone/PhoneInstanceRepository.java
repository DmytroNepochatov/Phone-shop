package ua.com.alevel.repository.phone;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.com.alevel.model.accessory.*;
import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.model.phone.Phone;
import ua.com.alevel.model.phone.PhoneInstance;
import ua.com.alevel.model.shoppingcart.ShoppingCart;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhoneInstanceRepository extends CrudRepository<PhoneInstance, String>, PagingAndSortingRepository<PhoneInstance, String> {
    Optional<PhoneInstance> findFirstByImei(String imei);

    @Query(value = "select * from public.phone_instance where phone_instance.id in (\n" +
            "     select SPLIT_PART(STRING_AGG(phone_instance.id, ','), ',', 1)\n" +
            "     from public.phone_instance as phone_instance, public.phone as phone, public.phone_description as phone_description where phone_instance.check_id is null\n" +
            "     and phone_instance.phone_id=phone.id and phone.phone_description_id=phone_description.id\n" +
            "     group by phone_description.brand_id, phone_description.name, phone_description.series, \n" +
            "     phone.amount_of_built_in_memory, phone.amount_of_ram\n" +
            ")", nativeQuery = true)
    List<PhoneInstance> findAllForMainView(Pageable pageable);

    @Query(value = "select * from public.phone_instance where phone_instance.id in (\n" +
            "     select SPLIT_PART(STRING_AGG(phone_instance.id, ','), ',', 1)\n" +
            "     from public.phone_instance as phone_instance, public.phone as phone, public.phone_description as phone_description where phone_instance.check_id is null\n" +
            "     and phone_instance.phone_id=phone.id and phone.phone_description_id=phone_description.id\n" +
            "     group by phone_description.brand_id, phone_description.name, phone_description.series, \n" +
            "     phone.amount_of_built_in_memory, phone.amount_of_ram\n" +
            ")", nativeQuery = true)
    List<PhoneInstance> findAllForMainViewForSearch();

    @Query("select phoneInstance from PhoneInstance phoneInstance where phoneInstance.clientCheck is null" +
            " and phoneInstance.phone.phoneDescription.brand.id = ?1 and phoneInstance.phone.phoneDescription.name = ?2 " +
            "and phoneInstance.phone.phoneDescription.series = ?3 and phoneInstance.phone.amountOfBuiltInMemory = ?4 " +
            "and phoneInstance.phone.amountOfRam = ?5")
    List<PhoneInstance> findAllInfoAboutPhone(String brand, String name, String series, int amountOfBuiltInMemory, int amountOfRam, Pageable pageable);

    @Query(value = "select * from public.phone_instance where phone_instance.id in (\n" +
            "     select SPLIT_PART(STRING_AGG(phone_instance.id, ','), ',', 1)\n" +
            "     from public.phone_instance as phone_instance, public.phone as phone, public.phone_description as phone_description where phone_instance.check_id is null\n" +
            "     and phone_instance.phone_id=phone.id and phone.phone_description_id=phone_description.id\n" +
            "\tand phone_description.brand_id = ?1 and phone_description.name=?2 and phone_description.series = ?3 \n" +
            "\tand phone.amount_of_built_in_memory = ?4 and phone.amount_of_ram = ?5\n" +
            "     group by phone.view_id\n" +
            ")", nativeQuery = true)
    List<PhoneInstance> findAllColorsForPhone(String brand, String name, String series, int amountOfBuiltInMemory, int amountOfRam);

    @Query("select brand from Brand brand where brand.id in (select distinct(phoneInstance.phone.phoneDescription.brand.id) from PhoneInstance phoneInstance where phoneInstance.clientCheck is null )")
    List<Brand> findAllAvailableBrand();

    @Query("select chargeType from ChargeType chargeType where chargeType.id in (select distinct(phoneInstance.phone.phoneDescription.chargeType.id) from PhoneInstance phoneInstance where phoneInstance.clientCheck is null )")
    List<ChargeType> findAllAvailableChargeTypes();

    @Query("select communicationStandard from CommunicationStandard communicationStandard where communicationStandard.id in (select distinct(phoneInstance.phone.phoneDescription.communicationStandard.id) from PhoneInstance phoneInstance where phoneInstance.clientCheck is null )")
    List<CommunicationStandard> findAllAvailableCommunicationStandards();

    @Query("select operationSystem from OperationSystem operationSystem where operationSystem.id in (select distinct(phoneInstance.phone.phoneDescription.operationSystem.id) from PhoneInstance phoneInstance where phoneInstance.clientCheck is null )")
    List<OperationSystem> findAllAvailableOperationSystems();

    @Query("select processor from Processor processor where processor.id in (select distinct(phoneInstance.phone.phoneDescription.processor.id) from PhoneInstance phoneInstance where phoneInstance.clientCheck is null )")
    List<Processor> findAllAvailableProcessors();

    @Query("select typeScreen from TypeScreen typeScreen where typeScreen.id in (select distinct(phoneInstance.phone.phoneDescription.typeScreen.id) from PhoneInstance phoneInstance where phoneInstance.clientCheck is null )")
    List<TypeScreen> findAllAvailableTypeScreens();

    @Query("select distinct(phoneInstance.phone.phoneDescription.diagonal) from PhoneInstance phoneInstance where phoneInstance.clientCheck is null")
    List<Float> findAllAvailableDiagonals();

    @Query("select distinct(phoneInstance.phone.phoneDescription.displayResolution) from PhoneInstance phoneInstance where phoneInstance.clientCheck is null")
    List<String> findAllAvailableDisplayResolutions();

    @Query("select distinct(phoneInstance.phone.phoneDescription.screenRefreshRate) from PhoneInstance phoneInstance where phoneInstance.clientCheck is null")
    List<Integer> findAllAvailableScreenRefreshRates();

    @Query("select distinct(phoneInstance.phone.phoneDescription.numberOfSimCards) from PhoneInstance phoneInstance where phoneInstance.clientCheck is null")
    List<Integer> findAllAvailableNumberOfSimCards();

    @Query("select distinct(phoneInstance.phone.amountOfBuiltInMemory) from PhoneInstance phoneInstance where phoneInstance.clientCheck is null")
    List<Integer> findAllAvailableAmountOfBuiltInMemory();

    @Query("select distinct(phoneInstance.phone.amountOfRam) from PhoneInstance phoneInstance where phoneInstance.clientCheck is null")
    List<Integer> findAllAvailableAmountOfRam();

    @Query("select distinct(phoneInstance.phone.phoneDescription.numberOfFrontCameras) from PhoneInstance phoneInstance where phoneInstance.clientCheck is null")
    List<Integer> findAllAvailableNumberOfFrontCameras();

    @Query("select distinct(phoneInstance.phone.phoneDescription.numberOfMainCameras) from PhoneInstance phoneInstance where phoneInstance.clientCheck is null")
    List<Integer> findAllAvailableNumberOfMainCameras();

    @Query("select distinct(phoneInstance.phone.phoneDescription.degreeOfMoistureProtection) from PhoneInstance phoneInstance where phoneInstance.clientCheck is null")
    List<String> findAllAvailableDegreeOfMoistureProtections();

    @Query(value = "select count(*) from public.phone_instance where phone_instance.id in (\n" +
            "     select SPLIT_PART(STRING_AGG(phone_instance.id, ','), ',', 1)\n" +
            "     from public.phone_instance as phone_instance, public.phone as phone, public.phone_description as phone_description where phone_instance.check_id is null\n" +
            "     and phone_instance.phone_id=phone.id and phone.phone_description_id=phone_description.id\n" +
            "     group by phone_description.brand_id, phone_description.name, phone_description.series, \n" +
            "     phone.amount_of_built_in_memory, phone.amount_of_ram\n" +
            ")", nativeQuery = true)
    Optional<Integer> getPagesCount();

    @Query("select phoneInstance.id from PhoneInstance phoneInstance where phoneInstance.phone.phoneDescription.brand.name = ?1 and " +
            "phoneInstance.phone.phoneDescription.name = ?2 and phoneInstance.phone.phoneDescription.series =?3 " +
            "and phoneInstance.phone.amountOfBuiltInMemory = ?4 and phoneInstance.phone.amountOfRam =?5 " +
            "and phoneInstance.phone.view.color =?6 and phoneInstance.clientCheck is null " +
            "and phoneInstance.shoppingCart is null")
    List<String> findFirstIdPhoneForShoppingCart(String brand, String name, String series, int amountOfBuiltInMemory,
                                                 int amountOfRam, String color);

    @Transactional
    @Modifying
    @Query("update PhoneInstance phoneInstance set phoneInstance.shoppingCart = ?1 where phoneInstance.id = ?2")
    void setShoppingCartForPhone(ShoppingCart shoppingCart, String phoneId);

    @Transactional
    @Modifying
    @Query("update PhoneInstance phoneInstance set phoneInstance.shoppingCart = null where phoneInstance.id = ?1")
    void delShoppingCartForPhone(String phoneId);

    @Query("select phoneInstance.price from PhoneInstance phoneInstance where phoneInstance.phone.id = ?1")
    List<Double> findPriceForPhoneId(String id);

    @Query("select phoneInstance from PhoneInstance phoneInstance where phoneInstance.shoppingCart.id = ?1")
    List<PhoneInstance> findAllPhonesForShoppingCartId(String shoppingCartId);

    @Transactional
    @Modifying
    @Query("update PhoneInstance phoneInstance set phoneInstance.clientCheck =?1 where phoneInstance.id = ?2")
    void addPhoneToClientCheck(ClientCheck clientCheck, String phoneId);

    @Query("select sum(phoneInstance.price) from PhoneInstance phoneInstance where phoneInstance.shoppingCart.id = ?1")
    double findPriceForShoppingCartId(String ShoppingCartId);

    @Query("select sum(phoneInstance.price) from PhoneInstance phoneInstance where phoneInstance.clientCheck.id = ?1")
    double findPriceForClientCheckId(String ClientCheckId);

    Optional<PhoneInstance> findFirstByPhoneAndClientCheckIsNull(Phone phone);

    @Query("select phoneInstance from PhoneInstance phoneInstance where phoneInstance.phone = ?1 and phoneInstance.clientCheck is not null order by phoneInstance.clientCheck.created desc")
    List<PhoneInstance> findAllPhoneInstancesByPhoneAndClientCheck(Phone phone);

    @Query("select count(phoneInstance) from PhoneInstance phoneInstance where phoneInstance.phone = ?1 and phoneInstance.clientCheck is null")
    int countPhonesInStoreForAdmin(Phone phone);

    @Query("select count(phoneInstance) from PhoneInstance phoneInstance where phoneInstance.phone = ?1 and phoneInstance.clientCheck is not null and phoneInstance.clientCheck.closedDate is not null")
    int countPhonesInStoreForAdminForStatistic(Phone phone);

    @Transactional
    @Modifying
    @Query("delete from PhoneInstance phoneInstance where phoneInstance.phone = ?1 and phoneInstance.clientCheck is null")
    void deletePhoneInstancesByPhoneForAdmin(Phone phone);

    @Query("select phoneInstance from PhoneInstance phoneInstance where phoneInstance.phone.phoneDescription.brand = ?1 " +
            "and phoneInstance.phone.phoneDescription.name = ?2 and phoneInstance.phone.phoneDescription.series = ?3 " +
            "and phoneInstance.phone.amountOfBuiltInMemory = ?4 and phoneInstance.phone.amountOfRam = ?5 " +
            "and phoneInstance.clientCheck is null")
    List<PhoneInstance> findAllPhoneInstancesForUpdatePrice(Brand brand, String name, String series, int amountOfBuiltInMemory, int amountOfRam);

    @Query("select phoneInstance from PhoneInstance phoneInstance where phoneInstance.clientCheck is null")
    List<PhoneInstance> findAllForAdmin();

    @Transactional
    @Modifying
    @Query("delete from PhoneInstance phoneInstance where phoneInstance.id = ?1 and phoneInstance.clientCheck is null")
    void deleteByIdPhoneInstance(String id);

    @Query("select phoneInstance from PhoneInstance phoneInstance where phoneInstance.clientCheck.registeredUser.dateOfBirth between ?1 and ?2")
    List<PhoneInstance> getCustomerPreferencesByAge(Date startAge, Date endAge);

    @Query("select count(phoneInstance) from PhoneInstance phoneInstance where " +
            "phoneInstance.phone = ?1 and phoneInstance.clientCheck.closedDate between ?2 and ?3")
    int soldSpecificModelsMonth(Phone phone, Date startDate, Date endDate);

    @Query("select count(phoneInstance) from PhoneInstance phoneInstance where phoneInstance.phone.phoneDescription.brand = ?1 and phoneInstance.phone.phoneDescription.name =?2 " +
            "and phoneInstance.phone.phoneDescription.series =?3 and phoneInstance.phone.amountOfBuiltInMemory =?4 " +
            "and phoneInstance.phone.amountOfRam =?5 and phoneInstance.clientCheck.closedDate between ?6 and ?7")
    int soldMostPopularPhoneModelsMonth(Brand brand, String name, String series, int amountOfBuiltInMemory, int amountOfRam, Date startDate, Date endDate);

    @Query("select phoneInstance from PhoneInstance phoneInstance where phoneInstance.clientCheck.id = ?1")
    List<PhoneInstance> findAllPhoneInstancesByClientCheckId(String checkId);

    @Query("select phoneInstance from PhoneInstance phoneInstance where phoneInstance.shoppingCart is not null")
    List<PhoneInstance> findAllPhoneInstancesWhichInCartForCleaner();
}