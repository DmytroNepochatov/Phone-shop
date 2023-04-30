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
public class TablesForFirstStatistic {
    private Phone phone;
    private List<SalesSettingsForSpecificModelsMonths> tempYear;
    private List<SalesSettingsForSpecificModelsMonths> lastYear;
    private List<SalesSettingsForSpecificModelsMonths> yearBeforeLast;
}
