package ua.com.alevel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ChargeTypeForMainView {
    private String id;
    private String name;
    private boolean enabled;
}
