package ua.com.alevel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.com.alevel.model.phone.Phone;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MostPopularPhoneModels {
    private Phone phone;
    private String month;
    private String sold;
}
