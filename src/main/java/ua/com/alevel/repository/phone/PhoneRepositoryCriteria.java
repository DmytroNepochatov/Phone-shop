package ua.com.alevel.repository.phone;

import org.springframework.stereotype.Repository;
import ua.com.alevel.model.phone.Phone;
import java.util.List;

@Repository
public interface PhoneRepositoryCriteria {
    List<Phone> filterPhones(String[] params, int page, int needPhones);
}
