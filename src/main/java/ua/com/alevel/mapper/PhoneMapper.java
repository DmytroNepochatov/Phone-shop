package ua.com.alevel.mapper;

import org.springframework.data.domain.Pageable;
import ua.com.alevel.model.accessory.*;
import ua.com.alevel.model.dto.CreatePhone;
import ua.com.alevel.model.dto.PhoneColors;
import ua.com.alevel.model.dto.PhoneForMainView;
import ua.com.alevel.model.dto.PhoneForShoppingCart;
import ua.com.alevel.model.phone.Phone;
import ua.com.alevel.repository.phone.PhoneRepository;
import ua.com.alevel.repository.phone.PhoneRepositoryCriteria;
import java.util.List;

public final class PhoneMapper {
    private PhoneMapper() {
    }

    public static List<PhoneForMainView> mapPhoneToPhoneForMainView(PhoneRepository phoneRepository, List<PhoneForMainView> phoneForMainViewList, Pageable pageable) {
        phoneRepository.findAllForMainView(pageable).forEach(phone ->
                phoneForMainViewList.add(new PhoneForMainView(phone.getId(), phone.getBrand().getName(), (float) Math.ceil(((float) phone.getRating().getTotalPoints() / phone.getRating().getNumberOfPoints()) * Math.pow(10, 1)) / (float) Math.pow(10, 1),
                        phone.getName(), phone.getSeries(), phone.getAmountOfBuiltInMemory(), phone.getAmountOfRam(),
                        phone.getPhoneFrontAndBack(), phone.getPrice(), phone.getCurrency())));

        return phoneForMainViewList;
    }

    public static List<PhoneForMainView> mapPhoneToPhoneForMainViewToFindBySearch(PhoneRepository phoneRepository, List<PhoneForMainView> phoneForMainViewList, Pageable pageable, String[] params) {
        phoneRepository.findBySearch(params[0], params[1], params[2], pageable).forEach(phone ->
                phoneForMainViewList.add(new PhoneForMainView(phone.getId(), phone.getBrand().getName(), (float) Math.ceil(((float) phone.getRating().getTotalPoints() / phone.getRating().getNumberOfPoints()) * Math.pow(10, 1)) / (float) Math.pow(10, 1),
                        phone.getName(), phone.getSeries(), phone.getAmountOfBuiltInMemory(), phone.getAmountOfRam(),
                        phone.getPhoneFrontAndBack(), phone.getPrice(), phone.getCurrency())));

        return phoneForMainViewList;
    }

    public static List<PhoneForMainView> mapPhoneToPhoneForMainViewFilter(PhoneRepositoryCriteria phoneRepositoryCriteria, List<PhoneForMainView> phoneForMainViewList, int page, String[] params, int needPhones) {
        phoneRepositoryCriteria.filterPhones(params, page, needPhones).forEach(phone ->
                phoneForMainViewList.add(new PhoneForMainView(phone.getId(), phone.getBrand().getName(), (float) Math.ceil(((float) phone.getRating().getTotalPoints() / phone.getRating().getNumberOfPoints()) * Math.pow(10, 1)) / (float) Math.pow(10, 1),
                        phone.getName(), phone.getSeries(), phone.getAmountOfBuiltInMemory(), phone.getAmountOfRam(),
                        phone.getPhoneFrontAndBack(), phone.getPrice(), phone.getCurrency())));

        return phoneForMainViewList;
    }

    public static Phone mapPhoneForMainViewToPhone(PhoneRepository phoneRepository, PhoneForMainView phoneForMainView) {
        return phoneRepository.findAllInfoAboutPhone(phoneForMainView.getBrand(), phoneForMainView.getName(),
                phoneForMainView.getSeries(), phoneForMainView.getAmountOfBuiltInMemory(), phoneForMainView.getAmountOfRam()).get();
    }

    public static List<PhoneColors> getColorsForPhone(PhoneRepository phoneRepository, PhoneForMainView phoneForMainView, List<PhoneColors> phoneColors) {
        phoneRepository.findAllColorsForPhone(phoneForMainView.getBrand(), phoneForMainView.getName(),
                phoneForMainView.getSeries(), phoneForMainView.getAmountOfBuiltInMemory(), phoneForMainView.getAmountOfRam()).forEach(phone ->
                phoneColors.add(new PhoneColors(phone.getColor(), phone.getPhoneFrontAndBack(),
                        phone.getLeftSideAndRightSide(), phone.getUpSideAndDownSide(), false)));

        return phoneColors;
    }

    public static List<PhoneForShoppingCart> mapPhoneToPhoneForShoppingCart(PhoneRepository phoneRepository, String shoppingCartId, List<PhoneForShoppingCart> result) {
        phoneRepository.findAllPhonesForShoppingCartId(shoppingCartId).forEach(phone ->
                result.add(new PhoneForShoppingCart(phone.getId(), phone.getBrand().getName(), phone.getName(), phone.getSeries(),
                        phone.getAmountOfBuiltInMemory(), phone.getAmountOfRam(), phone.getColor(), phone.getPhoneFrontAndBack(), phone.getPrice(),
                        phone.getCurrency())));

        return result;
    }

    public static Phone mapCreatePhoneToPhone(CreatePhone phone, Brand brand, ChargeType chargeType,
                                              CommunicationStandard communicationStandard, OperationSystem operationSystem,
                                              Processor processor, TypeScreen typeScreen) {
        Phone phoneForDb = new Phone();
        phoneForDb.setBrand(brand);
        phoneForDb.setChargeType(chargeType);
        phoneForDb.setCommunicationStandard(communicationStandard);
        phoneForDb.setOperationSystem(operationSystem);
        phoneForDb.setProcessor(processor);
        phoneForDb.setTypeScreen(typeScreen);
        phoneForDb.setName(phone.getName());
        phoneForDb.setSeries(phone.getSeries());
        phoneForDb.setDiagonal(phone.getDiagonal());
        phoneForDb.setDisplayResolution(phone.getDisplayResolution());
        phoneForDb.setScreenRefreshRate(phone.getScreenRefreshRate());
        phoneForDb.setNumberOfSimCards(phone.getNumberOfSimCards());
        phoneForDb.setAmountOfBuiltInMemory(phone.getAmountOfBuiltInMemory());
        phoneForDb.setAmountOfRam(phone.getAmountOfRam());
        phoneForDb.setNumberOfFrontCameras(phone.getNumberOfFrontCameras());
        phoneForDb.setInfoAboutFrontCameras(phone.getInfoAboutFrontCameras());
        phoneForDb.setNumberOfMainCameras(phone.getNumberOfMainCameras());
        phoneForDb.setInfoAboutMainCameras(phone.getInfoAboutMainCameras());
        phoneForDb.setWeight(phone.getWeight());
        phoneForDb.setHeight(phone.getHeight());
        phoneForDb.setWidth(phone.getWidth());
        phoneForDb.setDegreeOfMoistureProtection(phone.getDegreeOfMoistureProtection());
        phoneForDb.setHaveNfc(phone.isNfc());
        phoneForDb.setColor(phone.getColor());
        phoneForDb.setGuaranteeTimeMonths(phone.getGuaranteeTimeMonths());
        phoneForDb.setCountryProducerOfTheProduct(phone.getCountryProducerOfTheProduct());
        phoneForDb.setPhoneFrontAndBack(phone.getPhoneFrontAndBack());
        phoneForDb.setLeftSideAndRightSide(phone.getLeftSideAndRightSide());
        phoneForDb.setUpSideAndDownSide(phone.getUpSideAndDownSide());
        phoneForDb.setImei(phone.getImei());
        phoneForDb.setPrice(phone.getPrice());
        phoneForDb.setCurrency(phone.getCurrency());

        return phoneForDb;
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
