package ua.com.alevel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PhoneForMainView {
    private String id;
    private String brand;
    private float rating;
    private String name;
    private String series;
    private int amountOfBuiltInMemory;
    private int amountOfRam;
    private String phoneFrontAndBack;
    private double price;
    private String currency;
}
