package ua.com.webservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.com.webservice.model.phone.Phone;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MostPopularPhoneModels {
    private Phone phone;
    private String month;
    private int sold;
}
