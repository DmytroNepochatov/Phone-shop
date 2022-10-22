package ua.com.alevel.model.dto.filterparams;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScreenRefreshRateForMainView {
    private int refreshRate;
    private boolean enabled;
}