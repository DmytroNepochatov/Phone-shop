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
public class UserOrdersForAdmin {
    private String lastName;
    private String firstName;
    private String middleName;
    private String phoneNumber;
    private List<ClientCheck> checks;
}
