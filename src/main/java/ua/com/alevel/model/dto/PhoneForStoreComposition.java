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
public class PhoneForStoreComposition implements Comparable<PhoneForStoreComposition> {
    private Phone phone;
    private int price;
    private int countInStore;

    @Override
    public int compareTo(PhoneForStoreComposition o) {
        if (countInStore > o.countInStore) {
            return 1;
        }
        if (countInStore < o.countInStore) {
            return -1;
        }
        return 0;
    }
}
