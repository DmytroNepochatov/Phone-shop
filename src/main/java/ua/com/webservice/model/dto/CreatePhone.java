package ua.com.webservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePhone {
    private String view;
    private String phoneDescription;
    private String amountOfBuiltInMemory;
    private String amountOfRam;
    private String price;
    private String imei;
    private String phoneId;
}
