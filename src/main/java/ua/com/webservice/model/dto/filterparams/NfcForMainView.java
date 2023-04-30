package ua.com.webservice.model.dto.filterparams;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NfcForMainView {
    private String isHaveNfc;
    private boolean enabled;
}
