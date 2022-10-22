package ua.com.alevel.model.dto.filterparams;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DisplayResolutionForMainView {
    private String displayResolution;
    private boolean enabled;
}
