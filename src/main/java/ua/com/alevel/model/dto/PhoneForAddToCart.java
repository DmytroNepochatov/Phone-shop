package ua.com.alevel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhoneForAddToCart {
    private String brand;
    private String name;
    private String series;
    private int amountOfBuiltInMemory;
    private int amountOfRam;
    private List<PhoneColors> phoneColors;
}
