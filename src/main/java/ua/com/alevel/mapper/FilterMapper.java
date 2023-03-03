package ua.com.alevel.mapper;

import ua.com.alevel.model.accessory.*;
import ua.com.alevel.model.dto.filterparams.*;
import ua.com.alevel.service.phone.PhoneInstanceService;
import java.util.ArrayList;
import java.util.List;

public final class FilterMapper {
    private FilterMapper() {
    }

    public static List<BrandForMainView> mapBrandToBrandForMainViews(List<Brand> list) {
        List<BrandForMainView> brandForMainViewList = new ArrayList<>();
        list.forEach(brand -> brandForMainViewList.add(new BrandForMainView(brand.getId(), brand.getName(), false)));

        return brandForMainViewList;
    }

    public static List<ChargeTypeForMainView> mapChargeTypeToChargeTypeForMainViews(List<ChargeType> list) {
        List<ChargeTypeForMainView> chargeTypeForMainViewList = new ArrayList<>();
        list.forEach(chargeType -> chargeTypeForMainViewList.add(new ChargeTypeForMainView(chargeType.getId(), chargeType.getName(), false)));

        return chargeTypeForMainViewList;
    }

    public static List<CommunicationStandardForMainView> mapCommunicationStandardToCommunicationStandardForMainView(List<CommunicationStandard> list) {
        List<CommunicationStandardForMainView> communicationStandardForMainViewList = new ArrayList<>();
        list.forEach(communicationStandard -> communicationStandardForMainViewList.add(new CommunicationStandardForMainView(communicationStandard.getId(), communicationStandard.getName(), false)));

        return communicationStandardForMainViewList;
    }

    public static List<OperationSystemForMainView> mapOperationSystemToOperationSystemForMainView(List<OperationSystem> list) {
        List<OperationSystemForMainView> operationSystemForMainViewList = new ArrayList<>();
        list.forEach(operationSystem -> operationSystemForMainViewList.add(new OperationSystemForMainView(operationSystem.getId(), operationSystem.getName(), false)));

        return operationSystemForMainViewList;
    }

    public static List<ProcessorForMainView> mapProcessorToProcessorForMainView(List<Processor> list) {
        List<ProcessorForMainView> processorForMainViewList = new ArrayList<>();
        list.forEach(processor -> processorForMainViewList.add(new ProcessorForMainView(processor.getId(), processor.getName(), false)));

        return processorForMainViewList;
    }

    public static List<TypeScreenForMainView> mapTypeScreenToTypeScreenForMainView(List<TypeScreen> list) {
        List<TypeScreenForMainView> typeScreenForMainViewList = new ArrayList<>();
        list.forEach(typeScreen -> typeScreenForMainViewList.add(new TypeScreenForMainView(typeScreen.getId(), typeScreen.getName(), false)));

        return typeScreenForMainViewList;
    }

    public static List<DiagonalForMainView> mapDiagonalToDiagonalForMainView(List<Float> list) {
        List<DiagonalForMainView> diagonalForMainViewList = new ArrayList<>();
        list.forEach(diagonal -> diagonalForMainViewList.add(new DiagonalForMainView(diagonal, false)));

        return diagonalForMainViewList;
    }

    public static List<DisplayResolutionForMainView> mapDisplayResolutionToDisplayResolutionForMainView(List<String> list) {
        List<DisplayResolutionForMainView> displayResolutionForMainViewList = new ArrayList<>();
        list.forEach(displayResolution -> displayResolutionForMainViewList.add(new DisplayResolutionForMainView(displayResolution, false)));

        return displayResolutionForMainViewList;
    }

    public static List<ScreenRefreshRateForMainView> mapScreenRefreshRateToScreenRefreshRateForMainView(List<Integer> list) {
        List<ScreenRefreshRateForMainView> screenRefreshRateForMainViewList = new ArrayList<>();
        list.forEach(screenRefreshRate -> screenRefreshRateForMainViewList.add(new ScreenRefreshRateForMainView(screenRefreshRate, false)));

        return screenRefreshRateForMainViewList;
    }

    public static List<NumberOfSimCardForMainView> mapNumberOfSimCardToNumberOfSimCardForMainView(List<Integer> list) {
        List<NumberOfSimCardForMainView> numberOfSimCardForMainViewList = new ArrayList<>();
        list.forEach(numberOfSimCard -> numberOfSimCardForMainViewList.add(new NumberOfSimCardForMainView(numberOfSimCard, false)));

        return numberOfSimCardForMainViewList;
    }

    public static List<AmountOfBuiltInMemoryForMainView> mapAmountOfBuiltInMemoryToAmountOfBuiltInMemoryForMainView(List<Integer> list) {
        List<AmountOfBuiltInMemoryForMainView> amountOfBuiltInMemoryForMainViewList = new ArrayList<>();
        list.forEach(amountOfBuiltInMemory -> amountOfBuiltInMemoryForMainViewList.add(new AmountOfBuiltInMemoryForMainView(amountOfBuiltInMemory, false)));

        return amountOfBuiltInMemoryForMainViewList;
    }

    public static List<AmountOfRamForMainView> mapAmountOfRamToAmountOfRamForMainView(List<Integer> list) {
        List<AmountOfRamForMainView> amountOfRamForMainViewList = new ArrayList<>();
        list.forEach(amountOfRam -> amountOfRamForMainViewList.add(new AmountOfRamForMainView(amountOfRam, false)));

        return amountOfRamForMainViewList;
    }

    public static List<NumberOfFrontCameraForMainView> mapNumberOfFrontCameraToNumberOfFrontCameraForMainView(List<Integer> list) {
        List<NumberOfFrontCameraForMainView> numberOfFrontCameraForMainViewList = new ArrayList<>();
        list.forEach(numberOfFrontCamera -> numberOfFrontCameraForMainViewList.add(new NumberOfFrontCameraForMainView(numberOfFrontCamera, false)));

        return numberOfFrontCameraForMainViewList;
    }

    public static List<NumberOfMainCameraForMainView> mapNumberOfMainCameraToNumberOfMainCameraForMainView(List<Integer> list) {
        List<NumberOfMainCameraForMainView> numberOfMainCameraForMainViewList = new ArrayList<>();
        list.forEach(numberOfMainCamera -> numberOfMainCameraForMainViewList.add(new NumberOfMainCameraForMainView(numberOfMainCamera, false)));

        return numberOfMainCameraForMainViewList;
    }

    public static List<DegreeOfMoistureProtectionForMainView> mapDegreeOfMoistureProtectionToDegreeOfMoistureProtectionForMainView(List<String> list) {
        List<DegreeOfMoistureProtectionForMainView> degreeOfMoistureProtectionForMainViewList = new ArrayList<>();
        list.forEach(degreeOfMoistureProtection -> degreeOfMoistureProtectionForMainViewList.add(new DegreeOfMoistureProtectionForMainView(degreeOfMoistureProtection, false)));

        return degreeOfMoistureProtectionForMainViewList;
    }

    public static FilterSettings mapParamsToFilterSettings(PhoneInstanceService phoneInstanceService) {
        List<BrandForMainView> brandForMainViewList = phoneInstanceService.findAllAvailableBrand();
        List<ChargeTypeForMainView> chargeTypeForMainViewList = phoneInstanceService.findAllAvailableChargeTypes();
        List<CommunicationStandardForMainView> communicationStandardForMainViewList = phoneInstanceService.findAllAvailableCommunicationStandards();
        List<OperationSystemForMainView> operationSystemForMainViewList = phoneInstanceService.findAllAvailableOperationSystems();
        List<ProcessorForMainView> processorForMainViewList = phoneInstanceService.findAllAvailableProcessors();
        List<TypeScreenForMainView> typeScreenForMainViewList = phoneInstanceService.findAllAvailableTypeScreens();
        List<DiagonalForMainView> diagonalForMainViewList = phoneInstanceService.findAllAvailableDiagonals();
        List<DisplayResolutionForMainView> displayResolutionForMainViewList = phoneInstanceService.findAllAvailableDisplayResolutions();
        List<ScreenRefreshRateForMainView> screenRefreshRateForMainViewList = phoneInstanceService.findAllAvailableScreenRefreshRates();
        List<NumberOfSimCardForMainView> numberOfSimCardForMainViewList = phoneInstanceService.findAllAvailableNumberOfSimCards();
        List<AmountOfBuiltInMemoryForMainView> amountOfBuiltInMemoryForMainViewList = phoneInstanceService.findAllAvailableAmountOfBuiltInMemory();
        List<AmountOfRamForMainView> amountOfRamForMainViewList = phoneInstanceService.findAllAvailableAmountOfRam();
        List<NumberOfFrontCameraForMainView> numberOfFrontCameraForMainViewList = phoneInstanceService.findAllAvailableNumberOfFrontCameras();
        List<NumberOfMainCameraForMainView> numberOfMainCameraForMainViewList = phoneInstanceService.findAllAvailableNumberOfMainCameras();
        List<DegreeOfMoistureProtectionForMainView> degreeOfMoistureProtectionForMainViewList = phoneInstanceService.findAllAvailableDegreeOfMoistureProtection();
        List<NfcForMainView> nfcForMainViewList = phoneInstanceService.getNfcTypes();

        return new FilterSettings(brandForMainViewList, chargeTypeForMainViewList, communicationStandardForMainViewList,
                operationSystemForMainViewList, processorForMainViewList, typeScreenForMainViewList, diagonalForMainViewList,
                displayResolutionForMainViewList, screenRefreshRateForMainViewList, numberOfSimCardForMainViewList,
                amountOfBuiltInMemoryForMainViewList, amountOfRamForMainViewList, numberOfFrontCameraForMainViewList,
                numberOfMainCameraForMainViewList, degreeOfMoistureProtectionForMainViewList, nfcForMainViewList);
    }
}
