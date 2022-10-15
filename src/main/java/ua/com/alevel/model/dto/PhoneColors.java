package ua.com.alevel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PhoneColors {
    private String color;
    private String phoneFrontAndBack;
    private String leftSideAndRightSide;
    private String upSideAndDownSide;
}
