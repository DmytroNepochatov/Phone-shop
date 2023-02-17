package ua.com.alevel.repository.country;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.country.Country;
import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends CrudRepository<Country, String> {

    Optional<Country> findFirstByName(String name);

    @Query("select country.name from Country country order by country.name asc")
    List<String> findAllCountriesNames();
}
