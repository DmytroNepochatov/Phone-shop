package ua.com.alevel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.com.alevel.model.check.ClientCheck;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserOrdersForAdmin implements Comparable<UserOrdersForAdmin> {
    private String lastName;
    private String firstName;
    private String middleName;
    private String phoneNumber;
    private List<ClientCheck> checks;
    private List<String> dates;
    private List<String> datesClosed;
    private List<Double> totalPrices;

    @Override
    public int compareTo(UserOrdersForAdmin o) {
        return this.lastName.compareTo(o.lastName);
    }
}
