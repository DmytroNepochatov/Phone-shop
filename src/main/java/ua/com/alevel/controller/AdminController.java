package ua.com.alevel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.alevel.mapper.PhoneMapper;
import ua.com.alevel.model.accessory.*;
import ua.com.alevel.model.country.Country;
import ua.com.alevel.model.dto.*;
import ua.com.alevel.model.phone.Phone;
import ua.com.alevel.model.phone.PhoneDescription;
import ua.com.alevel.model.phone.PhoneInstance;
import ua.com.alevel.model.phone.View;
import ua.com.alevel.service.brand.BrandService;
import ua.com.alevel.service.chargetype.ChargeTypeService;
import ua.com.alevel.service.clientcheck.ClientCheckService;
import ua.com.alevel.service.communicationstandard.CommunicationStandardService;
import ua.com.alevel.service.country.CountryService;
import ua.com.alevel.service.operationsystem.OperationSystemService;
import ua.com.alevel.service.phone.PhoneInstanceService;
import ua.com.alevel.service.phonedescription.PhoneDescriptionService;
import ua.com.alevel.service.processor.ProcessorService;
import ua.com.alevel.service.typescreen.TypeScreenService;
import ua.com.alevel.service.user.UserDetailsServiceImpl;
import ua.com.alevel.service.view.ViewService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.apache.commons.lang.NumberUtils.isNumber;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final PhoneInstanceService phoneInstanceService;
    private final BrandService brandService;
    private final ChargeTypeService chargeTypeService;
    private final CommunicationStandardService communicationStandardService;
    private final OperationSystemService operationSystemService;
    private final ProcessorService processorService;
    private final TypeScreenService typeScreenService;
    private final ClientCheckService clientCheckService;
    private final CountryService countryService;
    private final ViewService viewService;
    private final PhoneDescriptionService phoneDescriptionService;
    private static final String REGEX = "https://drive.google.com";
    private static final String GOOGLE_DRIVE_VIEW_URL = "http://drive.google.com/uc?export=view&id=";
    private static final String LEFT_REGEX = "id=";
    private static final String RIGHT_REGEX = "&usp=drive_copy";

    public AdminController(UserDetailsServiceImpl userDetailsServiceImpl, PhoneInstanceService phoneInstanceService, BrandService brandService,
                           ChargeTypeService chargeTypeService, CommunicationStandardService communicationStandardService,
                           OperationSystemService operationSystemService, ProcessorService processorService,
                           TypeScreenService typeScreenService, ClientCheckService clientCheckService,
                           CountryService countryService, ViewService viewService, PhoneDescriptionService phoneDescriptionService) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.phoneInstanceService = phoneInstanceService;
        this.brandService = brandService;
        this.chargeTypeService = chargeTypeService;
        this.clientCheckService = clientCheckService;
        this.communicationStandardService = communicationStandardService;
        this.operationSystemService = operationSystemService;
        this.processorService = processorService;
        this.typeScreenService = typeScreenService;
        this.countryService = countryService;
        this.viewService = viewService;
        this.phoneDescriptionService = phoneDescriptionService;
    }

    @GetMapping("/profile")
    public String getProfile() {
        return "adminprofile";
    }

    @GetMapping("/create")
    public String createPhone(Model model) {
        tuneModel(model);
        model.addAttribute("errorMsg", " ");
        return "createphone";
    }

    @PostMapping("/create/phone-description")
    public String savePhoneDescription(Model model, CreatePhoneDescription phoneDescription) {
        if (phoneDescription.getBrand().equals("Select brand")) {
            return errorMsg(model, "You must choose a brand");
        }
        if (phoneDescription.getChargeType().equals("Select charge type")) {
            return errorMsg(model, "You must choose a charge type");
        }
        if (phoneDescription.getCommunicationStandard().equals("Select communication standard")) {
            return errorMsg(model, "You must choose a communication standard");
        }
        if (phoneDescription.getOperationSystem().equals("Select operation system")) {
            return errorMsg(model, "You must choose an operation system");
        }
        if (phoneDescription.getProcessor().equals("Select processor")) {
            return errorMsg(model, "You must choose a processor");
        }
        if (phoneDescription.getTypeScreen().equals("Select type screen")) {
            return errorMsg(model, "You must choose a type screen");
        }
        if (phoneDescription.getCountry().equals("Select country producer")) {
            return errorMsg(model, "You must choose a country producer");
        }
        if (phoneDescription.getName().isBlank()) {
            return errorMsg(model, "Name field is empty");
        }
        if (phoneDescription.getSeries().isBlank()) {
            return errorMsg(model, "Series field is empty");
        }
        if (!isNumber(phoneDescription.getDiagonal())) {
            return errorMsg(model, "Incorrect diagonal");
        }
        if (Float.parseFloat(phoneDescription.getDiagonal()) <= 0.0) {
            return errorMsg(model, "Incorrect diagonal");
        }
        if (phoneDescription.getDisplayResolution().isBlank()) {
            return errorMsg(model, "Display resolution field is empty");
        }
        if (!isNumber(phoneDescription.getScreenRefreshRate())) {
            return errorMsg(model, "Incorrect screen refresh rate");
        }
        if (Integer.parseInt(phoneDescription.getScreenRefreshRate()) <= 0.0) {
            return errorMsg(model, "Incorrect screen refresh rate");
        }
        if (!isNumber(phoneDescription.getNumberOfSimCards())) {
            return errorMsg(model, "Incorrect number of sim cards");
        }
        if (Integer.parseInt(phoneDescription.getNumberOfSimCards()) <= 0.0) {
            return errorMsg(model, "Incorrect number of sim cards");
        }
        if (!isNumber(phoneDescription.getNumberOfFrontCameras())) {
            return errorMsg(model, "Incorrect number of front cameras");
        }
        if (Integer.parseInt(phoneDescription.getNumberOfFrontCameras()) <= 0.0) {
            return errorMsg(model, "Incorrect number of front cameras");
        }
        if (phoneDescription.getInfoAboutFrontCameras().isBlank()) {
            return errorMsg(model, "Information about front cameras field is empty");
        }
        if (!isNumber(phoneDescription.getNumberOfMainCameras())) {
            return errorMsg(model, "Incorrect number of main cameras");
        }
        if (Integer.parseInt(phoneDescription.getNumberOfMainCameras()) <= 0.0) {
            return errorMsg(model, "Incorrect number of main cameras");
        }
        if (phoneDescription.getInfoAboutMainCameras().isBlank()) {
            return errorMsg(model, "Information about main cameras field is empty");
        }
        if (!isNumber(phoneDescription.getWeight())) {
            return errorMsg(model, "Incorrect weight");
        }
        if (Float.parseFloat(phoneDescription.getWeight()) <= 0.0) {
            return errorMsg(model, "Incorrect weight");
        }
        if (!isNumber(phoneDescription.getHeight())) {
            return errorMsg(model, "Incorrect height");
        }
        if (Float.parseFloat(phoneDescription.getHeight()) <= 0.0) {
            return errorMsg(model, "Incorrect height");
        }
        if (!isNumber(phoneDescription.getWidth())) {
            return errorMsg(model, "Incorrect width");
        }
        if (Float.parseFloat(phoneDescription.getWidth()) <= 0.0) {
            return errorMsg(model, "Incorrect width");
        }
        if (phoneDescription.getDegreeOfMoistureProtection().isBlank()) {
            return errorMsg(model, "Degree of moisture protection field is empty");
        }
        if (!isNumber(phoneDescription.getGuaranteeTimeMonths())) {
            return errorMsg(model, "Incorrect guarantee time in months");
        }
        if (Integer.parseInt(phoneDescription.getGuaranteeTimeMonths()) <= 0.0) {
            return errorMsg(model, "Incorrect guarantee time in months");
        }


        Brand brand = brandService.findBrandByName(phoneDescription.getBrand()).get();
        ChargeType chargeType = chargeTypeService.findFirstByName(phoneDescription.getChargeType()).get();
        CommunicationStandard communicationStandard = communicationStandardService.findFirstByName(phoneDescription.getCommunicationStandard()).get();
        OperationSystem operationSystem = operationSystemService.findFirstByName(phoneDescription.getOperationSystem()).get();
        Processor processor = processorService.findFirstByName(phoneDescription.getProcessor()).get();
        TypeScreen typeScreen = typeScreenService.findFirstByName(phoneDescription.getTypeScreen()).get();
        Country country = countryService.findCountryByName(phoneDescription.getCountry()).get();

        PhoneDescription phoneDescriptionForDb = PhoneMapper.mapCreatePhoneDescriptionToPhoneDescription(new PhoneDescription(), phoneDescription, brand, chargeType,
                communicationStandard, operationSystem, processor, typeScreen, country);

        if (!phoneDescriptionService.save(phoneDescriptionForDb)) {
            return errorMsg(model, "Phone description for this phone already exists");
        }

        return errorMsg(model, "Phone description saved successfully");
    }

    @PostMapping("/create/view")
    public String saveView(Model model, View createView) {
        if (createView.getColor().isBlank()) {
            return errorMsg(model, "Color field is empty");
        }
        if (createView.getColorForWhichPhone().equals("Select color for which phone")) {
            return errorMsg(model, "You must select color for which phone");
        }
        if (createView.getPhoneFrontAndBack().isBlank()) {
            return errorMsg(model, "Picture 1 field is empty");
        }
        if (createView.getLeftSideAndRightSide().isBlank()) {
            return errorMsg(model, "Picture 2 field is empty");
        }
        if (createView.getUpSideAndDownSide().isBlank()) {
            return errorMsg(model, "Picture 3 field is empty");
        }

        if (createView.getPhoneFrontAndBack().contains(REGEX)) {
            String[] linkParts = createView.getPhoneFrontAndBack().split(LEFT_REGEX);
            String pictureName = linkParts[1].replace(RIGHT_REGEX, "");
            createView.setPhoneFrontAndBack(GOOGLE_DRIVE_VIEW_URL + pictureName);
        }
        if (createView.getLeftSideAndRightSide().contains(REGEX)) {
            String[] linkParts = createView.getLeftSideAndRightSide().split(LEFT_REGEX);
            String pictureName = linkParts[1].replace(RIGHT_REGEX, "");
            createView.setLeftSideAndRightSide(GOOGLE_DRIVE_VIEW_URL + pictureName);
        }
        if (createView.getUpSideAndDownSide().contains(REGEX)) {
            String[] linkParts = createView.getUpSideAndDownSide().split(LEFT_REGEX);
            String pictureName = linkParts[1].replace(RIGHT_REGEX, "");
            createView.setUpSideAndDownSide(GOOGLE_DRIVE_VIEW_URL + pictureName);
        }

        if (!viewService.save(createView)) {
            return errorMsg(model, "This view already exists");
        }

        return errorMsg(model, "View saved successfully");
    }

    @PostMapping("/create/phone")
    public String savePhone(Model model, CreatePhone phone) {
        if (phone.getView().equals("Select phone color")) {
            return errorMsg(model, "You must choose a phone color");
        }
        if (phone.getPhoneDescription().equals("Select phone description")) {
            return errorMsg(model, "You must choose a phone description");
        }
        if (!isNumber(phone.getAmountOfBuiltInMemory())) {
            return errorMsg(model, "Incorrect amount of built in memory");
        }
        if (Integer.parseInt(phone.getAmountOfBuiltInMemory()) <= 0.0) {
            return errorMsg(model, "Incorrect amount of built in memory");
        }
        if (!isNumber(phone.getAmountOfRam())) {
            return errorMsg(model, "Incorrect amount of RAM");
        }
        if (Integer.parseInt(phone.getAmountOfRam()) <= 0.0) {
            return errorMsg(model, "Incorrect amount of RAM");
        }
        if (!isNumber(phone.getPrice())) {
            return errorMsg(model, "Incorrect price");
        }
        if (Double.parseDouble(phone.getPrice()) <= 0.0) {
            return errorMsg(model, "Incorrect price");
        }
        if (phone.getImei().isBlank()) {
            return errorMsg(model, "IMEI field is empty");
        }

        View view = viewService.findById(phone.getView());
        PhoneDescription phoneDescription = phoneDescriptionService.findById(phone.getPhoneDescription());
        Phone newPhone = new Phone();
        newPhone.setView(view);
        newPhone.setPhoneDescription(phoneDescription);
        newPhone.setAmountOfBuiltInMemory(Integer.parseInt(phone.getAmountOfBuiltInMemory()));
        newPhone.setAmountOfRam(Integer.parseInt(phone.getAmountOfRam()));

        Phone phoneResult = phoneInstanceService.savePhone(newPhone);

        String[] imeis = phone.getImei().split(",");
        int check = -1;
        int value = 0;

        for (int i = 0; i < imeis.length; i++) {
            if (!imeis[i].isBlank()) {

                PhoneInstance phoneInstance = new PhoneInstance();
                phoneInstance.setPhone(phoneResult);
                phoneInstance.setImei(imeis[i].trim());
                phoneInstance.setPrice(Double.parseDouble(phone.getPrice()));

                if (!phoneInstanceService.save(phoneInstance)) {
                    check = i;
                    break;
                }

                value++;
            }
        }

        if (check != -1) {
            return errorMsg(model, "This IMEI: " + imeis[check].trim() + " already exists");
        }
        else if (value == 1) {
            return errorMsg(model, "Phone saved successfully");
        }
        else {
            return errorMsg(model, "Phones saved successfully");
        }
    }

    private String errorMsg(Model model, String errorMsg) {
        tuneModel(model);
        model.addAttribute("errorMsg", errorMsg);
        return "createphone";
    }

    private void tuneModel(Model model) {
        CreatePhoneDescription phoneDescription = new CreatePhoneDescription();
        View createView = new View();
        CreatePhone phone = new CreatePhone();

        model.addAttribute("views", viewService.findAllViews());
        model.addAttribute("phoneDescriptions", phoneDescriptionService.findAllPhoneDescriptions());
        model.addAttribute("phone", phone);
        model.addAttribute("createView", createView);
        model.addAttribute("phoneDescription", phoneDescription);
        model.addAttribute("brands", brandService.findAllBrandsNames());
        model.addAttribute("chargeTypes", chargeTypeService.findAllChargeTypesNames());
        model.addAttribute("communicationStandards", communicationStandardService.findAllCommunicationStandardsNames());
        model.addAttribute("operationSystems", operationSystemService.findAllOperationSystemsNames());
        model.addAttribute("processors", processorService.findAllProcessorsNames());
        model.addAttribute("typeScreens", typeScreenService.findAllTypeScreensNames());
        model.addAttribute("countries", countryService.findAllCountriesNames());
    }

    @GetMapping("/delete")
    public String deletePhone(Model model, @RequestParam(value = "success") String success) {
        PhoneDescriptionForDelete deletePhoneDescription = new PhoneDescriptionForDelete();
        ViewForDelete deleteView = new ViewForDelete();
        CreatePhone phone = new CreatePhone();

        model.addAttribute("views", viewService.findAllViews());
        model.addAttribute("phoneDescriptions", phoneDescriptionService.findAllPhoneDescriptions());
        model.addAttribute("phone", phone);
        model.addAttribute("deleteView", deleteView);
        model.addAttribute("deletePhoneDescription", deletePhoneDescription);
        model.addAttribute("success", success);
        return "deletephones";
    }

    @PostMapping("/delete/phone-description")
    public String deletePhoneDescription(PhoneDescriptionForDelete deletePhoneDescription) {
        if (deletePhoneDescription.getPhoneDescription().equals("Select phone description")) {
            return "redirect:/admin/delete?success=You must select phone description";
        }

        if (!phoneDescriptionService.delete(deletePhoneDescription.getPhoneDescription())) {
            return "redirect:/admin/delete?success=This phone description already used";
        }
        else {
            return "redirect:/admin/delete?success=This phone description successfully deleted";
        }
    }

    @PostMapping("/delete/view")
    public String deleteView(ViewForDelete deleteView) {
        if (deleteView.getView().equals("Select phone color")) {
            return "redirect:/admin/delete?success=You must select phone color";
        }

        if (!viewService.delete(deleteView.getView())) {
            return "redirect:/admin/delete?success=This phone view already used";
        }
        else {
            return "redirect:/admin/delete?success=This phone view successfully deleted";
        }
    }

    @PostMapping("/delete/phone")
    public String deletePhone(CreatePhone phone) {
        if (phone.getView().equals("Select phone color")) {
            return "redirect:/admin/delete?success=You must choose a phone color";
        }
        if (phone.getPhoneDescription().equals("Select phone description")) {
            return "redirect:/admin/delete?success=You must choose a phone description";
        }
        if (!isNumber(phone.getAmountOfBuiltInMemory())) {
            return "redirect:/admin/delete?success=Incorrect amount of built in memory";
        }
        if (Integer.parseInt(phone.getAmountOfBuiltInMemory()) <= 0.0) {
            return "redirect:/admin/delete?success=Incorrect amount of built in memory";
        }
        if (!isNumber(phone.getAmountOfRam())) {
            return "redirect:/admin/delete?success=Incorrect amount of RAM";
        }
        if (Integer.parseInt(phone.getAmountOfRam()) <= 0.0) {
            return "redirect:/admin/delete?success=Incorrect amount of RAM";
        }

        View view = viewService.findById(phone.getView());
        PhoneDescription phoneDescription = phoneDescriptionService.findById(phone.getPhoneDescription());

        Phone phoneFromBd = phoneInstanceService.findFirstForDelete(view, phoneDescription,
                Integer.parseInt(phone.getAmountOfBuiltInMemory()), Integer.parseInt(phone.getAmountOfRam()));

        if (phoneFromBd == null) {
            return "redirect:/admin/delete?success=This phones is not in the database";
        }
        else {
            phoneInstanceService.delete(phoneFromBd);
            return "redirect:/admin/delete?success=This phones successfully deleted";
        }
    }

    @GetMapping("/change")
    public String changePhone(Model model, @RequestParam(value = "success") String success) {
        CreatePhoneDescription changePhoneDescription = new CreatePhoneDescription();
        CreateView changeView = new CreateView();
        CreatePhone changePhone = new CreatePhone();

        model.addAttribute("views", viewService.findAllViews());
        model.addAttribute("phoneDescriptions", phoneDescriptionService.findAllPhoneDescriptions());
        model.addAttribute("phones", getPhoneForStoreCompositions(false));
        model.addAttribute("changePhone", changePhone);
        model.addAttribute("changeView", changeView);
        model.addAttribute("changePhoneDescription", changePhoneDescription);
        model.addAttribute("brands", brandService.findAllBrandsNames());
        model.addAttribute("chargeTypes", chargeTypeService.findAllChargeTypesNames());
        model.addAttribute("communicationStandards", communicationStandardService.findAllCommunicationStandardsNames());
        model.addAttribute("operationSystems", operationSystemService.findAllOperationSystemsNames());
        model.addAttribute("processors", processorService.findAllProcessorsNames());
        model.addAttribute("typeScreens", typeScreenService.findAllTypeScreensNames());
        model.addAttribute("countries", countryService.findAllCountriesNames());
        model.addAttribute("success", success);
        return "changephones";
    }

    @PostMapping("/change/phone-description")
    public String changePhoneDescription(CreatePhoneDescription changePhoneDescription) {
        if(changePhoneDescription.getPhoneDescriptionId().equals("Select phone description")) {
            return "redirect:/admin/change?success=You must select phone description";
        }
        if (changePhoneDescription.getBrand().equals("Select brand")) {
            return "redirect:/admin/change?success=You must choose a brand";
        }
        if (changePhoneDescription.getChargeType().equals("Select charge type")) {
            return "redirect:/admin/change?success=You must choose a charge type";
        }
        if (changePhoneDescription.getCommunicationStandard().equals("Select communication standard")) {
            return "redirect:/admin/change?success=You must choose a communication standard";
        }
        if (changePhoneDescription.getOperationSystem().equals("Select operation system")) {
            return "redirect:/admin/change?success=You must choose an operation system";
        }
        if (changePhoneDescription.getProcessor().equals("Select processor")) {
            return"redirect:/admin/change?success=You must choose a processor";
        }
        if (changePhoneDescription.getTypeScreen().equals("Select type screen")) {
            return "redirect:/admin/change?success=You must choose a type screen";
        }
        if (changePhoneDescription.getCountry().equals("Select country producer")) {
            return "redirect:/admin/change?success=You must choose a country producer";
        }
        if (changePhoneDescription.getName().isBlank()) {
            return "redirect:/admin/change?success=Name field is empty";
        }
        if (changePhoneDescription.getSeries().isBlank()) {
            return "redirect:/admin/change?success=Series field is empty";
        }
        if (!isNumber(changePhoneDescription.getDiagonal())) {
            return "redirect:/admin/change?success=Incorrect diagonal";
        }
        if (Float.parseFloat(changePhoneDescription.getDiagonal()) <= 0.0) {
            return "redirect:/admin/change?success=Incorrect diagonal";
        }
        if (changePhoneDescription.getDisplayResolution().isBlank()) {
            return "redirect:/admin/change?success=Display resolution field is empty";
        }
        if (!isNumber(changePhoneDescription.getScreenRefreshRate())) {
            return "redirect:/admin/change?success=Incorrect screen refresh rate";
        }
        if (Integer.parseInt(changePhoneDescription.getScreenRefreshRate()) <= 0.0) {
            return "redirect:/admin/change?success=Incorrect screen refresh rate";
        }
        if (!isNumber(changePhoneDescription.getNumberOfSimCards())) {
            return "redirect:/admin/change?success=Incorrect number of sim cards";
        }
        if (Integer.parseInt(changePhoneDescription.getNumberOfSimCards()) <= 0.0) {
            return "redirect:/admin/change?success=Incorrect number of sim cards";
        }
        if (!isNumber(changePhoneDescription.getNumberOfFrontCameras())) {
            return "redirect:/admin/change?success=Incorrect number of front cameras";
        }
        if (Integer.parseInt(changePhoneDescription.getNumberOfFrontCameras()) <= 0.0) {
            return "redirect:/admin/change?success=Incorrect number of front cameras";
        }
        if (changePhoneDescription.getInfoAboutFrontCameras().isBlank()) {
            return "redirect:/admin/change?success=Information about front cameras field is empty";
        }
        if (!isNumber(changePhoneDescription.getNumberOfMainCameras())) {
            return "redirect:/admin/change?success=Incorrect number of main cameras";
        }
        if (Integer.parseInt(changePhoneDescription.getNumberOfMainCameras()) <= 0.0) {
            return "redirect:/admin/change?success=Incorrect number of main cameras";
        }
        if (changePhoneDescription.getInfoAboutMainCameras().isBlank()) {
            return "redirect:/admin/change?success=Information about main cameras field is empty";
        }
        if (!isNumber(changePhoneDescription.getWeight())) {
            return "redirect:/admin/change?success=Incorrect weight";
        }
        if (Float.parseFloat(changePhoneDescription.getWeight()) <= 0.0) {
            return "redirect:/admin/change?success=Incorrect weight";
        }
        if (!isNumber(changePhoneDescription.getHeight())) {
            return "redirect:/admin/change?success=Incorrect height";
        }
        if (Float.parseFloat(changePhoneDescription.getHeight()) <= 0.0) {
            return "redirect:/admin/change?success=Incorrect height";
        }
        if (!isNumber(changePhoneDescription.getWidth())) {
            return "redirect:/admin/change?success=Incorrect width";
        }
        if (Float.parseFloat(changePhoneDescription.getWidth()) <= 0.0) {
            return "redirect:/admin/change?success=Incorrect width";
        }
        if (changePhoneDescription.getDegreeOfMoistureProtection().isBlank()) {
            return "redirect:/admin/change?success=Degree of moisture protection field is empty";
        }
        if (!isNumber(changePhoneDescription.getGuaranteeTimeMonths())) {
            return "redirect:/admin/change?success=Incorrect guarantee time in months";
        }
        if (Integer.parseInt(changePhoneDescription.getGuaranteeTimeMonths()) <= 0.0) {
            return "redirect:/admin/change?success=Incorrect guarantee time in months";
        }

        PhoneDescription phoneDescription = phoneDescriptionService.findById(changePhoneDescription.getPhoneDescriptionId());
        Brand brand = brandService.findBrandByName(changePhoneDescription.getBrand()).get();
        ChargeType chargeType = chargeTypeService.findFirstByName(changePhoneDescription.getChargeType()).get();
        CommunicationStandard communicationStandard = communicationStandardService.findFirstByName(changePhoneDescription.getCommunicationStandard()).get();
        OperationSystem operationSystem = operationSystemService.findFirstByName(changePhoneDescription.getOperationSystem()).get();
        Processor processor = processorService.findFirstByName(changePhoneDescription.getProcessor()).get();
        TypeScreen typeScreen = typeScreenService.findFirstByName(changePhoneDescription.getTypeScreen()).get();
        Country country = countryService.findCountryByName(changePhoneDescription.getCountry()).get();

        PhoneDescription phoneDescriptionForDb = PhoneMapper.mapCreatePhoneDescriptionToPhoneDescription(phoneDescription, changePhoneDescription, brand, chargeType,
                communicationStandard, operationSystem, processor, typeScreen, country);

        phoneDescriptionService.update(phoneDescriptionForDb);
        return "redirect:/admin/change?success=Phone description updated successfully";
    }

    @PostMapping("/change/view")
    public String changeView(CreateView changeView) {
        if(changeView.getViewId().equals("Select phone color")) {
            return "redirect:/admin/change?success=You must select phone color";
        }
        if (changeView.getColor().isBlank()) {
            return "redirect:/admin/change?success=Color field is empty";
        }
        if (changeView.getPhoneFrontAndBack().isBlank()) {
            return "redirect:/admin/change?success=Picture 1 field is empty";
        }
        if (changeView.getLeftSideAndRightSide().isBlank()) {
            return "redirect:/admin/change?success=Picture 2 field is empty";
        }
        if (changeView.getUpSideAndDownSide().isBlank()) {
            return "redirect:/admin/change?success=Picture 3 field is empty";
        }

        if (changeView.getPhoneFrontAndBack().contains(REGEX)) {
            String[] linkParts = changeView.getPhoneFrontAndBack().split(LEFT_REGEX);
            String pictureName = linkParts[1].replace(RIGHT_REGEX, "");
            changeView.setPhoneFrontAndBack(GOOGLE_DRIVE_VIEW_URL + pictureName);
        }
        if (changeView.getLeftSideAndRightSide().contains(REGEX)) {
            String[] linkParts = changeView.getLeftSideAndRightSide().split(LEFT_REGEX);
            String pictureName = linkParts[1].replace(RIGHT_REGEX, "");
            changeView.setLeftSideAndRightSide(GOOGLE_DRIVE_VIEW_URL + pictureName);
        }
        if (changeView.getUpSideAndDownSide().contains(REGEX)) {
            String[] linkParts = changeView.getUpSideAndDownSide().split(LEFT_REGEX);
            String pictureName = linkParts[1].replace(RIGHT_REGEX, "");
            changeView.setUpSideAndDownSide(GOOGLE_DRIVE_VIEW_URL + pictureName);
        }

        View view = viewService.findById(changeView.getViewId());
        view.setColor(changeView.getColor());
        view.setPhoneFrontAndBack(changeView.getPhoneFrontAndBack());
        view.setLeftSideAndRightSide(changeView.getLeftSideAndRightSide());
        view.setUpSideAndDownSide(changeView.getUpSideAndDownSide());

        viewService.update(view);
        return "redirect:/admin/change?success=View updated successfully";
    }

    @PostMapping("/change/phone")
    public String changePhone(CreatePhone changePhone) {
        if(changePhone.getPhoneId().equals("Select phone")) {
            return "redirect:/admin/change?success=You must select phone";
        }
        if (!isNumber(changePhone.getAmountOfBuiltInMemory())) {
            return "redirect:/admin/change?success=Incorrect amount of built in memory";
        }
        if (Integer.parseInt(changePhone.getAmountOfBuiltInMemory()) <= 0.0) {
            return "redirect:/admin/change?success=Incorrect amount of built in memory";
        }
        if (!isNumber(changePhone.getAmountOfRam())) {
            return "redirect:/admin/change?success=Incorrect amount of RAM";
        }
        if (Integer.parseInt(changePhone.getAmountOfRam()) <= 0.0) {
            return "redirect:/admin/change?success=Incorrect amount of RAM";
        }
        if (!isNumber(changePhone.getPrice())) {
            return "redirect:/admin/change?success=Incorrect price";
        }
        if (Double.parseDouble(changePhone.getPrice()) <= 0.0) {
            return "redirect:/admin/change?success=Incorrect price";
        }

        Phone phone = phoneInstanceService.findByIdPhone(changePhone.getPhoneId());

        phoneInstanceService.updatePhone(phone, Integer.parseInt(changePhone.getAmountOfBuiltInMemory()),
                Integer.parseInt(changePhone.getAmountOfRam()));
        phoneInstanceService.updatePhoneInstance(phone, Integer.parseInt(changePhone.getAmountOfBuiltInMemory()),
                Integer.parseInt(changePhone.getAmountOfRam()), Double.parseDouble(changePhone.getPrice()));
        return "redirect:/admin/change?success=Phones updated successfully";
    }

    @GetMapping("/allphones")
    public String allPhones(Model model, @RequestParam(value = "success") String success) {
        List<PhoneInstance> phones = phoneInstanceService.findAllForAdmin();

        model.addAttribute("phones", phones);
        model.addAttribute("success", success);
        return "allphones";
    }

    @PostMapping("/allphones")
    public String deleteFromAllPhones(@RequestParam(value = "id") String id) {
        phoneInstanceService.deleteByIdPhoneInstance(id);

        return "redirect:/admin/allphones?success=The phone has been deleted successfully";
    }

    @GetMapping("/store-composition")
    public String getStoreComposition(Model model) {
        model.addAttribute("phoneForStoreCompositions", getPhoneForStoreCompositions(true));
        return "storecomposition";
    }

    private List<PhoneForStoreComposition> getPhoneForStoreCompositions(boolean state) {
        List<PhoneForStoreComposition> phoneForStoreCompositions = new ArrayList<>();
        List<Phone> phones;

        if(state) {
            phones = phoneInstanceService.findAllPhonesInDbForAdmin();
        }
        else {
            phones = phoneInstanceService.findAllPhonesInDb();
        }

        phones.forEach(phone -> {
            PhoneForStoreComposition phoneForStoreComposition = new PhoneForStoreComposition();
            phoneForStoreComposition.setPhone(phone);
            phoneForStoreComposition.setPrice(phoneInstanceService.findPriceForPhoneForAdmin(phone));
            phoneForStoreComposition.setCountInStore(phoneInstanceService.countPhonesInStoreForAdmin(phone));

            phoneForStoreCompositions.add(phoneForStoreComposition);
        });

        return phoneForStoreCompositions;
    }

    @GetMapping("/orders")
    public String getOrders(Model model, @RequestParam(value = "success") String success) {
        setOrder(model, success, true);
        return "adminorders";
    }

    @GetMapping("/orders-history")
    public String getOrdersHistory(Model model, @RequestParam(value = "success") String success) {
        setOrder(model, success, false);
        return "adminorders";
    }

    private void setOrder(Model model, String success, boolean flag) {
        List<UserOrdersForAdmin> orders = userDetailsServiceImpl.getUsersOrdersForAdmin(flag);
        orders.forEach(order -> order.getChecks().forEach(check -> order.getTotalPrices().add(phoneInstanceService.findPriceForClientCheckId(check.getId()))));
        Collections.sort(orders);

        SimpleDateFormat formatter = new SimpleDateFormat("dd.M.yyyy HH:mm:ss", Locale.ENGLISH);
        orders.forEach(order -> order.getChecks().forEach(check -> order.getDates().add(formatter.format(check.getCreated()))));

        if(!flag) {
            orders.forEach(order -> order.getChecks().forEach(check -> order.getDatesClosed().add(formatter.format(check.getClosedDate()))));
        }

        model.addAttribute("orders", orders);
        model.addAttribute("success", success);
        model.addAttribute("flag", flag);
    }

    @PutMapping("/close-order")
    public String closeOrder(@RequestParam(value = "id") String id) {
        clientCheckService.updateCheckClosed(true, id);

        return "redirect:/admin/orders?success=Check " + id + " successfully closed";
    }

    @GetMapping("/characteristics")
    public String getCharacteristics() {
        return "phonecharacteristics";
    }

    @GetMapping("/characteristics/brands")
    public String customizeBrands(Model model, @RequestParam(value = "success") String success) {
        List<Brand> brands = brandService.findAllBrandsForAdmin();
        NewBrand newBrand = new NewBrand();

        model.addAttribute("brands", brands);
        model.addAttribute("newBrand", newBrand);
        model.addAttribute("countries", countryService.findAllCountriesNames());
        model.addAttribute("success", success);
        return "сustomizebrands";
    }

    @PostMapping("/characteristics/brands")
    public String deleteBrands(@RequestParam(value = "id") String id) {
        if (!brandService.delete(id)) {
            return "redirect:/admin/characteristics/brands?success=This brand cannot be removed as it is already in use";
        }
        else {
            return "redirect:/admin/characteristics/brands?success=The brand has been successfully removed";
        }
    }

    @PostMapping("/characteristics/brands/create")
    public String createBrands(Model model, NewBrand newBrand) {
        if (newBrand.getName().isBlank()) {
            return "redirect:/admin/characteristics/brands?success=Name field is empty";
        }
        if (newBrand.getCountry().equals("Select country")) {
            return "redirect:/admin/characteristics/brands?success=You must select country";
        }

        Brand brand = new Brand();
        brand.setName(newBrand.getName());
        brand.setCountry(countryService.findCountryByName(newBrand.getCountry()).get());
        brand.setPhoneDescriptions(new ArrayList<>());
        if (!brandService.save(brand)) {
            return "redirect:/admin/characteristics/brands?success=This brand already exists";
        }
        else {
            return "redirect:/admin/characteristics/brands?success=Brand successfully saved";
        }
    }

    @GetMapping("/characteristics/chargetypes")
    public String customizeChargeTypes(Model model, @RequestParam(value = "success") String success) {
        List<ChargeType> chargeTypes = chargeTypeService.findAllChargeTypesForAdmin();
        ChargeType newChargeType = new ChargeType();

        model.addAttribute("chargeTypes", chargeTypes);
        model.addAttribute("newChargeType", newChargeType);
        model.addAttribute("success", success);
        return "customizechargetypes";
    }

    @PostMapping("/characteristics/chargetypes")
    public String deleteChargeTypes(@RequestParam(value = "id") String id) {
        if (!chargeTypeService.delete(id)) {
            return "redirect:/admin/characteristics/chargetypes?success=This charge type cannot be removed as it is already in use";
        }
        else {
            return "redirect:/admin/characteristics/chargetypes?success=The charge type has been successfully removed";
        }
    }

    @PostMapping("/characteristics/chargetypes/create")
    public String createChargeTypes(Model model, ChargeType newChargeType) {
        if (newChargeType.getName().isBlank()) {
            return "redirect:/admin/characteristics/chargetypes?success=Name field is empty";
        }

        newChargeType.setPhoneDescriptions(new ArrayList<>());
        if (!chargeTypeService.save(newChargeType)) {
            return "redirect:/admin/characteristics/chargetypes?success=This charge type already exists";
        }
        else {
            return "redirect:/admin/characteristics/chargetypes?success=Charge type successfully saved";
        }
    }

    @GetMapping("/characteristics/communicationstandards")
    public String customizeCommunicationStandards(Model model, @RequestParam(value = "success") String success) {
        List<CommunicationStandard> communicationStandards = communicationStandardService.findAllCommunicationStandardsForAdmin();
        CommunicationStandard newCommunicationStandard = new CommunicationStandard();

        model.addAttribute("communicationStandards", communicationStandards);
        model.addAttribute("newCommunicationStandard", newCommunicationStandard);
        model.addAttribute("success", success);
        return "customizecommunicationstandards";
    }

    @PostMapping("/characteristics/communicationstandards")
    public String deleteCommunicationStandards(@RequestParam(value = "id") String id) {
        if (!communicationStandardService.delete(id)) {
            return "redirect:/admin/characteristics/communicationstandards?success=This communication standard cannot be removed as it is already in use";
        }
        else {
            return "redirect:/admin/characteristics/communicationstandards?success=The communication standard has been successfully removed";
        }
    }

    @PostMapping("/characteristics/communicationstandards/create")
    public String createCommunicationStandards(Model model, CommunicationStandard newCommunicationStandard) {
        if (newCommunicationStandard.getName().isBlank()) {
            return "redirect:/admin/characteristics/communicationstandards?success=Name field is empty";
        }

        newCommunicationStandard.setPhoneDescriptions(new ArrayList<>());
        if (!communicationStandardService.save(newCommunicationStandard)) {
            return "redirect:/admin/characteristics/communicationstandards?success=This communication standard already exists";
        }
        else {
            return "redirect:/admin/characteristics/communicationstandards?success=Communication standard successfully saved";
        }
    }

    @GetMapping("/characteristics/operationsystems")
    public String customizeOperationSystems(Model model, @RequestParam(value = "success") String success) {
        List<OperationSystem> operationSystems = operationSystemService.findAllOperationSystemsForAdmin();
        OperationSystem newOperationSystem = new OperationSystem();

        model.addAttribute("operationSystems", operationSystems);
        model.addAttribute("newOperationSystem", newOperationSystem);
        model.addAttribute("success", success);
        return "customizeoperationsystems";
    }

    @PostMapping("/characteristics/operationsystems")
    public String deleteOperationSystems(@RequestParam(value = "id") String id) {
        if (!operationSystemService.delete(id)) {
            return "redirect:/admin/characteristics/operationsystems?success=This operation system cannot be removed as it is already in use";
        }
        else {
            return "redirect:/admin/characteristics/operationsystems?success=The operation system has been successfully removed";
        }
    }

    @PostMapping("/characteristics/operationsystems/create")
    public String createOperationSystems(Model model, OperationSystem newOperationSystem) {
        if (newOperationSystem.getName().isBlank()) {
            return "redirect:/admin/characteristics/operationsystems?success=Name field is empty";
        }

        newOperationSystem.setPhoneDescriptions(new ArrayList<>());
        if (!operationSystemService.save(newOperationSystem)) {
            return "redirect:/admin/characteristics/operationsystems?success=This operation system already exists";
        }
        else {
            return "redirect:/admin/characteristics/operationsystems?success=Operation system successfully saved";
        }
    }

    @GetMapping("/characteristics/typescreens")
    public String customizeTypeScreens(Model model, @RequestParam(value = "success") String success) {
        List<TypeScreen> typeScreens = typeScreenService.findAllTypeScreensForAdmin();
        TypeScreen newTypeScreen = new TypeScreen();

        model.addAttribute("typeScreens", typeScreens);
        model.addAttribute("newTypeScreen", newTypeScreen);
        model.addAttribute("success", success);
        return "customizetypescreens";
    }

    @PostMapping("/characteristics/typescreens")
    public String deleteTypeScreens(@RequestParam(value = "id") String id) {
        if (!typeScreenService.delete(id)) {
            return "redirect:/admin/characteristics/typescreens?success=This type screen cannot be removed as it is already in use";
        }
        else {
            return "redirect:/admin/characteristics/typescreens?success=The type screen has been successfully removed";
        }
    }

    @PostMapping("/characteristics/typescreens/create")
    public String createTypeScreens(Model model, TypeScreen newTypeScreen) {
        if (newTypeScreen.getName().isBlank()) {
            return "redirect:/admin/characteristics/typescreens?success=Name field is empty";
        }

        newTypeScreen.setPhoneDescriptions(new ArrayList<>());
        if (!typeScreenService.save(newTypeScreen)) {
            return "redirect:/admin/characteristics/typescreens?success=This type screen already exists";
        }
        else {
            return "redirect:/admin/characteristics/typescreens?success=Type screen successfully saved";
        }
    }

    @GetMapping("/characteristics/processors")
    public String customizeProcessors(Model model, @RequestParam(value = "success") String success) {
        List<Processor> processors = processorService.findAllProcessorsForAdmin();
        Processor newProcessor = new Processor();

        model.addAttribute("processors", processors);
        model.addAttribute("newProcessor", newProcessor);
        model.addAttribute("success", success);
        return "customizeprocessors";
    }

    @PostMapping("/characteristics/processors")
    public String deleteProcessors(@RequestParam(value = "id") String id) {
        if (!processorService.delete(id)) {
            return "redirect:/admin/characteristics/processors?success=This processor cannot be removed as it is already in use";
        }
        else {
            return "redirect:/admin/characteristics/processors?success=The processor has been successfully removed";
        }
    }

    @PostMapping("/characteristics/processors/create")
    public String createProcessors(Model model, Processor newProcessor) {
        if (newProcessor.getName().isBlank()) {
            return "redirect:/admin/characteristics/processors?success=Name field is empty";
        }
        if (newProcessor.getCoreFrequency() <= 0.0) {
            return "redirect:/admin/characteristics/processors?success=Incorrect core frequency";
        }
        if (newProcessor.getNumberOfCores() == 0) {
            return "redirect:/admin/characteristics/processors?success=You must select the number of cores";
        }

        newProcessor.setPhoneDescriptions(new ArrayList<>());
        if (!processorService.save(newProcessor)) {
            return "redirect:/admin/characteristics/processors?success=This processor already exists";
        }
        else {
            return "redirect:/admin/characteristics/processors?success=Processor successfully saved";
        }
    }
}