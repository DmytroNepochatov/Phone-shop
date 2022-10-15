package ua.com.alevel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NumberOfMainCameraForMainView {
    private int numberOfMainCameras;
    private boolean enabled;
}
