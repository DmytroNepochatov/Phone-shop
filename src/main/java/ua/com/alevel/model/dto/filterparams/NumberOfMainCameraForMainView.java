package ua.com.alevel.model.dto.filterparams;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NumberOfMainCameraForMainView {
    private int numberOfMainCameras;
    private boolean enabled;
}
