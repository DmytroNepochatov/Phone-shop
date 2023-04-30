package ua.com.webservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.com.webservice.model.phone.Phone;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalesSettingsForSpecificModelsParams {
    private Phone phone;
    private List<SalesSettingsForSpecificModelsMonths> fields;
}
