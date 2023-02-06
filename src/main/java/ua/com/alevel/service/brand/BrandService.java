package ua.com.alevel.service.brand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.alevel.model.accessory.Brand;
import ua.com.alevel.repository.brand.BrandRepository;
import ua.com.alevel.repository.phone.PhoneRepository;
import java.util.List;
import java.util.Optional;

@Service
public class BrandService {
    private final BrandRepository brandRepository;
    private final PhoneRepository phoneRepository;

    @Autowired
    public BrandService(BrandRepository brandRepository, PhoneRepository phoneRepository) {
        this.brandRepository = brandRepository;
        this.phoneRepository = phoneRepository;
    }

    public Optional<Brand> findBrandByName(String name) {
        return brandRepository.findFirstByName(name);
    }

    public List<String> findAllBrandsNames() {
        return brandRepository.findAllBrandsNames();
    }

    public List<Brand> findAllBrandsForAdmin() {
        return brandRepository.findAllBrandsForAdmin();
    }

    public boolean delete(String id) {
        if (phoneRepository.findFirstByBrand(brandRepository.findById(id).get()).isPresent()) {
            return false;
        }
        else {
            brandRepository.deleteById(id);
            return true;
        }
    }

    public boolean save(Brand brand) {
        if (brandRepository.findFirstByName(brand.getName()).isPresent()) {
            return false;
        }
        else {
            brandRepository.save(brand);
            return true;
        }
    }
}
