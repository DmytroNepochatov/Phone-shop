package ua.com.webservice.repository.phone;

import org.springframework.stereotype.Repository;
import ua.com.webservice.model.phone.PhoneInstance;
import java.util.List;

@Repository
public interface PhoneInstanceRepositoryCriteria {
    List<PhoneInstance> filterPhones(String[] params, int needPhones);
}
