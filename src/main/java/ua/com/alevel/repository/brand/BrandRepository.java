package ua.com.alevel.repository.brand;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.accessory.Brand;
import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends CrudRepository<Brand, String> {

    Optional<Brand> findFirstByName(String name);

    @Query("select brand.name from Brand brand order by brand.name asc")
    List<String> findAllBrandsNames();

    @Query("select brand from Brand brand order by brand.name asc")
    List<Brand> findAllBrandsForAdmin();
}
