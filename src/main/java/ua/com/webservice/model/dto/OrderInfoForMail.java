package ua.com.webservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.com.webservice.model.check.ClientCheck;
import ua.com.webservice.model.user.RegisteredUser;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoForMail {
    private ClientCheck clientCheck;
    private String created;
    private int totalPrice;
    private boolean flag;
    private RegisteredUser registeredUser;
}
