package ua.com.webservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderParams {
    private String deliveryType;
    private String deliveryAddress;
    private String paymentType;
    private String recipient;
    private String recipientNumberPhone;
    private String checkId;
    private String nameOnCard;
    private String creditCardNumber;
    private String expiration;
    private String cvc;
    private boolean sendEmail;
}
