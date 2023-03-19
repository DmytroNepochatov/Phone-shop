package ua.com.alevel.mapper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ua.com.alevel.model.accessory.*;
import ua.com.alevel.model.dto.CreatePhoneDescription;
import ua.com.alevel.model.dto.PhoneColors;
import ua.com.alevel.model.dto.PhoneForMainView;
import ua.com.alevel.model.dto.PhoneForShoppingCart;
import ua.com.alevel.model.phone.PhoneDescription;
import ua.com.alevel.model.phone.PhoneInstance;
import ua.com.alevel.repository.phone.PhoneInstanceRepository;
import ua.com.alevel.repository.phone.PhoneInstanceRepositoryCriteria;
import ua.com.alevel.service.brand.BrandService;
import ua.com.alevel.service.chargetype.ChargeTypeService;
import ua.com.alevel.service.communicationstandard.CommunicationStandardService;
import ua.com.alevel.service.country.CountryService;
import ua.com.alevel.service.operationsystem.OperationSystemService;
import ua.com.alevel.service.processor.ProcessorService;
import ua.com.alevel.service.typescreen.TypeScreenService;
import java.text.SimpleDateFormat;
import java.util.List;

public final class PhoneMapper {
    private PhoneMapper() {
    }

    public static List<PhoneForMainView> mapPhoneToPhoneForMainView(PhoneInstanceRepository phoneInstanceRepository, List<PhoneForMainView> phoneForMainViewList, Pageable pageable) {
        phoneInstanceRepository.findAllForMainView(pageable).forEach(phoneInstance -> addToList(phoneInstance, phoneForMainViewList));

        return phoneForMainViewList;
    }

    public static List<PhoneForMainView> mapPhoneToPhoneForMainViewToFindBySearch(PhoneInstanceRepository phoneInstanceRepository, List<PhoneForMainView> phoneForMainViewList) {
        phoneInstanceRepository.findAllForMainViewForSearch().forEach(phoneInstance -> addToList(phoneInstance, phoneForMainViewList));

        return phoneForMainViewList;
    }

    public static List<PhoneForMainView> mapPhoneToPhoneForMainViewFilter(PhoneInstanceRepositoryCriteria phoneInstanceRepositoryCriteria, List<PhoneForMainView> phoneForMainViewList, String[] params, int needPhones) {
        phoneInstanceRepositoryCriteria.filterPhones(params, needPhones).forEach(phoneInstance -> addToList(phoneInstance, phoneForMainViewList));

        return phoneForMainViewList;
    }

    private static void addToList(PhoneInstance phoneInstance, List<PhoneForMainView> phoneForMainViewList) {
        phoneForMainViewList.add(new PhoneForMainView(phoneInstance.getId(), phoneInstance.getPhone().getPhoneDescription().getBrand().getName(), (float) Math.ceil(((float) phoneInstance.getPhone().getRating().getTotalPoints() / phoneInstance.getPhone().getRating().getNumberOfPoints()) * Math.pow(10, 1)) / (float) Math.pow(10, 1),
                phoneInstance.getPhone().getPhoneDescription().getName(), phoneInstance.getPhone().getPhoneDescription().getSeries(), phoneInstance.getPhone().getAmountOfBuiltInMemory(), phoneInstance.getPhone().getAmountOfRam(),
                phoneInstance.getPhone().getView().getPhoneFrontAndBack(), phoneInstance.getPrice()));
    }

    public static PhoneInstance mapPhoneForMainViewToPhoneInstance(PhoneInstanceRepository phoneInstanceRepository, PhoneForMainView phoneForMainView) {
        return phoneInstanceRepository.findAllInfoAboutPhone(phoneForMainView.getBrand(), phoneForMainView.getName(),
                phoneForMainView.getSeries(), phoneForMainView.getAmountOfBuiltInMemory(), phoneForMainView.getAmountOfRam(), PageRequest.of(0, 1)).get(0);
    }

    public static List<PhoneColors> getColorsForPhone(PhoneInstanceRepository phoneInstanceRepository, PhoneForMainView phoneForMainView, List<PhoneColors> phoneColors) {
        phoneInstanceRepository.findAllColorsForPhone(phoneForMainView.getBrand(), phoneForMainView.getName(),
                phoneForMainView.getSeries(), phoneForMainView.getAmountOfBuiltInMemory(), phoneForMainView.getAmountOfRam()).forEach(phoneInstance ->
                phoneColors.add(new PhoneColors(phoneInstance.getPhone().getView().getColor(), phoneInstance.getPhone().getView().getPhoneFrontAndBack(),
                        phoneInstance.getPhone().getView().getLeftSideAndRightSide(), phoneInstance.getPhone().getView().getUpSideAndDownSide(), false)));

        return phoneColors;
    }

    public static List<PhoneForShoppingCart> mapPhoneInstanceToPhoneForShoppingCart(PhoneInstanceRepository phoneInstanceRepository, String shoppingCartId, List<PhoneForShoppingCart> result) {
        phoneInstanceRepository.findAllPhonesForShoppingCartId(shoppingCartId).forEach(phoneInstance ->
                result.add(new PhoneForShoppingCart(phoneInstance.getId(), phoneInstance.getPhone().getPhoneDescription().getBrand().getName(), phoneInstance.getPhone().getPhoneDescription().getName(), phoneInstance.getPhone().getPhoneDescription().getSeries(),
                        phoneInstance.getPhone().getAmountOfBuiltInMemory(), phoneInstance.getPhone().getAmountOfRam(), phoneInstance.getPhone().getView().getColor(), phoneInstance.getPhone().getView().getPhoneFrontAndBack(), phoneInstance.getPrice())));

        return result;
    }

    public static PhoneDescription mapCreatePhoneDescriptionToPhoneDescription(PhoneDescription phoneDescriptionForDb, CreatePhoneDescription phoneDescription, BrandService brandService, ChargeTypeService chargeTypeService,
                                                                               CommunicationStandardService communicationStandardService, OperationSystemService operationSystemService,
                                                                               ProcessorService processorService, TypeScreenService typeScreenService, CountryService countryService, SimpleDateFormat formatter) throws Exception {
        phoneDescriptionForDb.setBrand(brandService.findBrandByName(phoneDescription.getBrand()).get());
        phoneDescriptionForDb.setChargeType(chargeTypeService.findFirstByName(phoneDescription.getChargeType()).get());
        phoneDescriptionForDb.setCommunicationStandard(communicationStandardService.findFirstByName(phoneDescription.getCommunicationStandard()).get());
        phoneDescriptionForDb.setOperationSystem(operationSystemService.findFirstByName(phoneDescription.getOperationSystem()).get());
        phoneDescriptionForDb.setProcessor(processorService.findFirstByName(phoneDescription.getProcessor()).get());
        phoneDescriptionForDb.setTypeScreen(typeScreenService.findFirstByName(phoneDescription.getTypeScreen()).get());
        phoneDescriptionForDb.setCountry(countryService.findCountryByName(phoneDescription.getCountry()).get());
        phoneDescriptionForDb.setName(phoneDescription.getName());
        phoneDescriptionForDb.setSeries(phoneDescription.getSeries());
        phoneDescriptionForDb.setDiagonal(Float.parseFloat(phoneDescription.getDiagonal()));
        phoneDescriptionForDb.setDisplayResolution(phoneDescription.getDisplayResolution());
        phoneDescriptionForDb.setScreenRefreshRate(Integer.parseInt(phoneDescription.getScreenRefreshRate()));
        phoneDescriptionForDb.setNumberOfSimCards(Integer.parseInt(phoneDescription.getNumberOfSimCards()));
        phoneDescriptionForDb.setNumberOfFrontCameras(Integer.parseInt(phoneDescription.getNumberOfFrontCameras()));
        phoneDescriptionForDb.setInfoAboutFrontCameras(phoneDescription.getInfoAboutFrontCameras());
        phoneDescriptionForDb.setNumberOfMainCameras(Integer.parseInt(phoneDescription.getNumberOfMainCameras()));
        phoneDescriptionForDb.setInfoAboutMainCameras(phoneDescription.getInfoAboutMainCameras());
        phoneDescriptionForDb.setWeight(Float.parseFloat(phoneDescription.getWeight()));
        phoneDescriptionForDb.setHeight(Float.parseFloat(phoneDescription.getHeight()));
        phoneDescriptionForDb.setWidth(Float.parseFloat(phoneDescription.getWidth()));
        phoneDescriptionForDb.setDegreeOfMoistureProtection(phoneDescription.getDegreeOfMoistureProtection());
        phoneDescriptionForDb.setHaveNfc(phoneDescription.isNfc());
        phoneDescriptionForDb.setGuaranteeTimeMonths(Integer.parseInt(phoneDescription.getGuaranteeTimeMonths()));
        phoneDescriptionForDb.setDateAddedToDatabase(formatter.parse(phoneDescription.getDateAddedToDatabase()));

        return phoneDescriptionForDb;
    }

    public static PhoneForMainView mapParamsToPhoneForMainView(Brand brand, String name, String series,
                                                               int amountOfBuiltInMemory, int amountOfRam) {
        PhoneForMainView phoneForMainView = new PhoneForMainView();
        phoneForMainView.setBrand(brand.getId());
        phoneForMainView.setName(name);
        phoneForMainView.setSeries(series);
        phoneForMainView.setAmountOfBuiltInMemory(amountOfBuiltInMemory);
        phoneForMainView.setAmountOfRam(amountOfRam);

        return phoneForMainView;
    }
}
