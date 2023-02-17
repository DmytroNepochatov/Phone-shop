package ua.com.alevel.service.country;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.alevel.model.country.Country;
import ua.com.alevel.repository.country.CountryRepository;
import java.util.List;
import java.util.Optional;

@Service
public class CountryService {
    private final CountryRepository countryRepository;

    @Autowired
    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public Optional<Country> findCountryByName(String name) {
        return countryRepository.findFirstByName(name);
    }

    public List<String> findAllCountriesNames() {
        return countryRepository.findAllCountriesNames();
    }
}
