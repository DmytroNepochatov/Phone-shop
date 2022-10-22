package ua.com.alevel.repository.brand;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.accessory.Brand;
import java.util.Optional;

@Repository
public interface BrandRepository extends CrudRepository<Brand, String> {

    Optional<Brand> findFirstByName(String name);
}
