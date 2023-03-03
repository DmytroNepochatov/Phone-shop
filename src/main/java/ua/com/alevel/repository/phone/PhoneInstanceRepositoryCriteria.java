package ua.com.alevel.repository.phone;

import org.springframework.stereotype.Repository;
import ua.com.alevel.model.phone.PhoneInstance;
import java.util.List;

@Repository
public interface PhoneInstanceRepositoryCriteria {
    List<PhoneInstance> filterPhones(String[] params, int needPhones);
}
