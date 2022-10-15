package ua.com.alevel.repository.phone;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.accessory.*;
import ua.com.alevel.model.phone.Phone;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhoneRepository extends CrudRepository<Phone, String>, PagingAndSortingRepository<Phone, String> {
    Optional<Phone> findFirstByImei(String imei);

    Optional<Phone> findFirstByBrandAndNameAndSeriesAndAmountOfBuiltInMemoryAndAmountOfRamAndRatingNotNull(Brand brand, String name, String series, int amountOfBuiltInMemory, int amountOfRam);

    List<Phone> findAllByClientCheckNull();

    @Query(value = "select * from public.phone where phone.id in (\n" +
            "     select SPLIT_PART(STRING_AGG(phone.id, ','), ',', 1)\n" +
            "     from public.phone as phone where check_id is null\n" +
            "     group by phone.brand_id, phone.name, phone.series, \n" +
            "     phone.amount_of_built_in_memory, phone.amount_of_ram\n" +
            ")", nativeQuery = true)
    List<Phone> findAllForMainView(Pageable pageable);

    @Query(value = "select * from public.phone where phone.id in (\n" +
            "     select SPLIT_PART(STRING_AGG(phone.id, ','), ',', 1)\n" +
            "     from public.phone as phone where check_id is null\n" +
            "\tand phone.brand_id = ?1 and phone.series = ?2 and phone.name = ?3\n" +
            "     group by phone.brand_id, phone.name, phone.series, \n" +
            "     phone.amount_of_built_in_memory, phone.amount_of_ram\n" +
            ")", nativeQuery = true)
    List<Phone> findBySearch(String brand, String series, String name, Pageable pageable);

    @Query(value = "select * from public.phone where phone.id in (\n" +
            "     select SPLIT_PART(STRING_AGG(phone.id, ','), ',', 1)\n" +
            "     from public.phone as phone where check_id is null\n" +
            "\tand phone.brand_id = ?1 and phone.name=?2 and phone.series = ?3 \n" +
            "\tand phone.amount_of_built_in_memory = ?4 and phone.amount_of_ram = ?5\n" +
            "     group by phone.brand_id, phone.name, phone.series, \n" +
            "     phone.amount_of_built_in_memory, phone.amount_of_ram\n" +
            ")", nativeQuery = true)
    Optional<Phone> findAllInfoAboutPhone(String brand, String name, String series, int amountOfBuiltInMemory, int amountOfRam);

    @Query(value = "select * from public.phone where phone.id in (\n" +
            "     select SPLIT_PART(STRING_AGG(phone.id, ','), ',', 1)\n" +
            "     from public.phone as phone where check_id is null\n" +
            "\tand phone.brand_id = ?1 and phone.name=?2 and phone.series = ?3 \n" +
            "\tand phone.amount_of_built_in_memory = ?4 and phone.amount_of_ram = ?5\n" +
            "     group by phone.color\n" +
            ")", nativeQuery = true)
    List<Phone> findAllColorsForPhone(String brand, String name, String series, int amountOfBuiltInMemory, int amountOfRam);

    @Query("select brand from Brand brand where brand.id in (select distinct(phone.brand.id) from Phone phone where phone.clientCheck is null )")
    List<Brand> findAllAvailableBrand();

    @Query("select chargeType from ChargeType chargeType where chargeType.id in (select distinct(phone.chargeType.id) from Phone phone where phone.clientCheck is null )")
    List<ChargeType> findAllAvailableChargeTypes();

    @Query("select communicationStandard from CommunicationStandard communicationStandard where communicationStandard.id in (select distinct(phone.communicationStandard.id) from Phone phone where phone.clientCheck is null )")
    List<CommunicationStandard> findAllAvailableCommunicationStandards();

    @Query("select operationSystem from OperationSystem operationSystem where operationSystem.id in (select distinct(phone.operationSystem.id) from Phone phone where phone.clientCheck is null )")
    List<OperationSystem> findAllAvailableOperationSystems();

    @Query("select processor from Processor processor where processor.id in (select distinct(phone.processor.id) from Phone phone where phone.clientCheck is null )")
    List<Processor> findAllAvailableProcessors();

    @Query("select typeScreen from TypeScreen typeScreen where typeScreen.id in (select distinct(phone.typeScreen.id) from Phone phone where phone.clientCheck is null )")
    List<TypeScreen> findAllAvailableTypeScreens();

    @Query("select distinct(phone.diagonal) from Phone phone where phone.clientCheck is null")
    List<Float> findAllAvailableDiagonals();

    @Query("select distinct(phone.displayResolution) from Phone phone where phone.clientCheck is null")
    List<String> findAllAvailableDisplayResolutions();

    @Query("select distinct(phone.screenRefreshRate) from Phone phone where phone.clientCheck is null")
    List<Integer> findAllAvailableScreenRefreshRates();

    @Query("select distinct(phone.numberOfSimCards) from Phone phone where phone.clientCheck is null")
    List<Integer> findAllAvailableNumberOfSimCards();

    @Query("select distinct(phone.amountOfBuiltInMemory) from Phone phone where phone.clientCheck is null")
    List<Integer> findAllAvailableAmountOfBuiltInMemory();

    @Query("select distinct(phone.amountOfRam) from Phone phone where phone.clientCheck is null")
    List<Integer> findAllAvailableAmountOfRam();

    @Query("select distinct(phone.numberOfFrontCameras) from Phone phone where phone.clientCheck is null")
    List<Integer> findAllAvailableNumberOfFrontCameras();

    @Query("select distinct(phone.numberOfMainCameras) from Phone phone where phone.clientCheck is null")
    List<Integer> findAllAvailableNumberOfMainCameras();

    @Query("select distinct(phone.degreeOfMoistureProtection) from Phone phone where phone.clientCheck is null")
    List<String> findAllAvailableDegreeOfMoistureProtections();

    @Query(value = "select count(*) from public.phone where phone.id in (\n" +
            "     select SPLIT_PART(STRING_AGG(phone.id, ','), ',', 1)\n" +
            "     from public.phone as phone where check_id is null\n" +
            "     group by phone.brand_id, phone.name, phone.series, \n" +
            "     phone.amount_of_built_in_memory, phone.amount_of_ram\n" +
            ")", nativeQuery = true)
    Optional<Integer> getPagesCount();
}
