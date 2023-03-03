package ua.com.alevel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhoneForMainView implements Comparable<PhoneForMainView> {
    private String id;
    private String brand;
    private float rating;
    private String name;
    private String series;
    private int amountOfBuiltInMemory;
    private int amountOfRam;
    private String phoneFrontAndBack;
    private double price;

    @Override
    public int compareTo(PhoneForMainView o) {
        if (price > o.price) {
            return 1;
        }
        if (price < o.price) {
            return -1;
        }
        return 0;
    }
}
