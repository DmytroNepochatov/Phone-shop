package ua.com.alevel.service.brand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.alevel.model.accessory.Brand;
import ua.com.alevel.repository.brand.BrandRepository;
import java.util.Optional;

@Service
public class BrandService {
    private final BrandRepository brandRepository;

    @Autowired
    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public Optional<Brand> findBrandByName(String name) {
        return brandRepository.findFirstByName(name);
    }
}
