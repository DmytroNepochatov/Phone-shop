package ua.com.webservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.com.webservice.model.phone.PhoneInstance;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerPreferencesByAge {
    private String startAge;
    private String endAge;
    private List<PhoneInstance> phoneInstances;
}