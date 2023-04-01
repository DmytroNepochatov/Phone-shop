package ua.com.alevel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhoneForShoppingCart {
    private String id;
    private String brand;
    private String name;
    private String series;
    private int amountOfBuiltInMemory;
    private int amountOfRam;
    private String color;
    private String phoneFrontAndBack;
    private int price;
}