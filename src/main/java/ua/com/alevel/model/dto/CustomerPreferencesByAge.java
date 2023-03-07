package ua.com.alevel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.com.alevel.model.phone.PhoneInstance;
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