package ua.com.alevel.mapper;

import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.model.dto.CreateOrderParams;

public final class OrderMapper {
    private OrderMapper() {
    }

    public static ClientCheck createOrderParamsToClientCheck(ClientCheck clientCheck, CreateOrderParams createOrderParams) {
        clientCheck.setDeliveryType(createOrderParams.getDeliveryType());
        clientCheck.setDeliveryAddress(createOrderParams.getDeliveryAddress());
        clientCheck.setPaymentType(createOrderParams.getPaymentType());
        clientCheck.setRecipient(createOrderParams.getRecipient());
        clientCheck.setRecipientNumberPhone(createOrderParams.getRecipientNumberPhone());

        return clientCheck;
    }

    public static ClientCheck fillClientCheckDefaultValues(ClientCheck clientCheck) {
        String defaultValue = "default";

        clientCheck.setDeliveryType(defaultValue);
        clientCheck.setDeliveryAddress(defaultValue);
        clientCheck.setPaymentType(defaultValue);
        clientCheck.setRecipient(defaultValue);
        clientCheck.setRecipientNumberPhone(defaultValue);

        return clientCheck;
    }
}