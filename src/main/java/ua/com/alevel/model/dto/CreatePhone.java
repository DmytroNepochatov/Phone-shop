package ua.com.alevel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePhone {
    private String brand;
    private String chargeType;
    private String communicationStandard;
    private String operationSystem;
    private String processor;
    private String typeScreen;
    private String name;
    private String series;
    private float diagonal;
    private String displayResolution;
    private int screenRefreshRate;
    private int numberOfSimCards;
    private int amountOfBuiltInMemory;
    private int amountOfRam;
    private int numberOfFrontCameras;
    private String infoAboutFrontCameras;
    private int numberOfMainCameras;
    private String infoAboutMainCameras;
    private float weight;
    private float height;
    private float width;
    private String degreeOfMoistureProtection;
    private boolean nfc;
    private String color;
    private int guaranteeTimeMonths;
    private String country;
    private String phoneFrontAndBack;
    private String leftSideAndRightSide;
    private String upSideAndDownSide;
    private String imei;
    private double price;
    private String currency;
}
