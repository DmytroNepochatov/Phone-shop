package ua.com.alevel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePhoneDescription {
    private String brand;
    private String chargeType;
    private String communicationStandard;
    private String operationSystem;
    private String processor;
    private String typeScreen;
    private String name;
    private String series;
    private String diagonal;
    private String displayResolution;
    private String screenRefreshRate;
    private String numberOfSimCards;
    private String numberOfFrontCameras;
    private String infoAboutFrontCameras;
    private String numberOfMainCameras;
    private String infoAboutMainCameras;
    private String weight;
    private String height;
    private String width;
    private String degreeOfMoistureProtection;
    private boolean nfc;
    private String guaranteeTimeMonths;
    private String country;

    private String phoneDescriptionId;
}
