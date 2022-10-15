package ua.com.alevel.mapper;

import org.springframework.data.domain.Pageable;
import ua.com.alevel.model.dto.PhoneColors;
import ua.com.alevel.model.dto.PhoneForMainView;
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
                        phone.getLeftSideAndRightSide(), phone.getUpSideAndDownSide())));

        return phoneColors;
    }
}
