package ua.com.alevel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class DisplayResolutionForMainView {
    private String displayResolution;
    private boolean enabled;
}
