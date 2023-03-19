package ua.com.alevel.repository.phone;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.accessory.Brand;
import ua.com.alevel.model.phone.Phone;
import ua.com.alevel.model.phone.PhoneDescription;
import ua.com.alevel.model.phone.View;
import ua.com.alevel.model.rating.Rating;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhoneRepository extends CrudRepository<Phone, String>, PagingAndSortingRepository<Phone, String> {

    @Query(value = "select * from public.phone where phone.id in (\n" +
            "     select SPLIT_PART(STRING_AGG(phone.id, ','), ',', 1)\n" +
            "     from public.phone as phone, public.phone_description as phone_description where\n" +
            "     phone.phone_description_id=phone_description.id\n" +
            "     group by phone_description.brand_id, phone_description.name, phone_description.series, \n" +
            "     phone.amount_of_built_in_memory, phone.amount_of_ram\n" +
            ")", nativeQuery = true)
    List<Phone> findAllPhonesInDb();

    Optional<Phone> findFirstByView(View view);

    Optional<Phone> findFirstByPhoneDescription(PhoneDescription phoneDescription);

    Optional<Phone> findFirstByRating(Rating rating);

    @Query("select phone from Phone phone where phone.view = ?1 and phone.phoneDescription = ?2 and phone.amountOfBuiltInMemory =?3 and phone.amountOfRam =?4")
    List<Phone> findFirstPhoneForSave(View view, PhoneDescription phoneDescription, int amountOfBuiltInMemory, int amountOfRam);

    @Query("select phone from Phone phone where phone.phoneDescription = ?1 and phone.amountOfBuiltInMemory =?2 and phone.amountOfRam =?3")
    List<Phone> findFirstPhoneForRating(PhoneDescription phoneDescription, int amountOfBuiltInMemory, int amountOfRam);

    @Query("select phone from Phone phone where phone.phoneDescription.brand = ?1 and phone.phoneDescription.name = ?2 " +
            "and phone.phoneDescription.series = ?3 and phone.amountOfBuiltInMemory = ?4 and phone.amountOfRam = ?5")
    List<Phone> findAllPhonesForChange(Brand brand, String name, String series, int amountOfBuiltInMemory, int amountOfRam);

    @Query("select phone from Phone phone where phone.phoneDescription.dateAddedToDatabase between ?1 and ?2")
    List<Phone> findAllPhonesWithBetweenTime(Date startDate, Date endDate);
}
