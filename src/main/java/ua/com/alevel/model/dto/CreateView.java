package ua.com.alevel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateView {
    private String color;
    private String phoneFrontAndBack;
    private String leftSideAndRightSide;
    private String upSideAndDownSide;

    private String viewId;
}