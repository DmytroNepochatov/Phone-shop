package ua.com.alevel.model.dto.filterparams;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilterSettings {
    private List<BrandForMainView> brandForMainViewList;
    private List<ChargeTypeForMainView> chargeTypeForMainViewList;
    private List<CommunicationStandardForMainView> communicationStandardForMainViewList;
    private List<OperationSystemForMainView> operationSystemForMainViewList;
    private List<ProcessorForMainView> processorForMainViewList;
    private List<TypeScreenForMainView> typeScreenForMainViewList;
    private List<DiagonalForMainView> diagonalForMainViewList;
    private List<DisplayResolutionForMainView> displayResolutionForMainViewList;
    private List<ScreenRefreshRateForMainView> screenRefreshRateForMainViewList;
    private List<NumberOfSimCardForMainView> numberOfSimCardForMainViewList;
    private List<AmountOfBuiltInMemoryForMainView> amountOfBuiltInMemoryForMainViewList;
    private List<AmountOfRamForMainView> amountOfRamForMainViewList;
    private List<NumberOfFrontCameraForMainView> numberOfFrontCameraForMainViewList;
    private List<NumberOfMainCameraForMainView> numberOfMainCameraForMainViewList;
    private List<DegreeOfMoistureProtectionForMainView> degreeOfMoistureProtectionForMainViewList;
    private List<NfcForMainView> nfcForMainViewList;
}
