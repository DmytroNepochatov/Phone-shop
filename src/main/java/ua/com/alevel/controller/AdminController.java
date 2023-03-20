package ua.com.alevel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.alevel.mapper.PhoneMapper;
import ua.com.alevel.model.accessory.*;
import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.model.dto.*;
import ua.com.alevel.model.phone.Phone;
import ua.com.alevel.model.phone.PhoneDescription;
import ua.com.alevel.model.phone.PhoneInstance;
import ua.com.alevel.model.phone.View;
import ua.com.alevel.model.user.RegisteredUser;
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
import ua.com.alevel.util.Util;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
    private static final String ERROR_MSG = "errorMsg";
    private static final String SUCCESS = "success";
    private static final String PHONES = "phones";
    private static final String YEARS = "years";
    private static final String PHONE_FOR_STORE_COMPOSITIONS = "phoneForStoreCompositions";
    private static final String FLAG_FOR_STATISTIC = "flagStat";
    private static final String FLAG = "flag";
    private static final String SELECT_PHONE_COLOR = "Select phone color";
    private static final String SELECT_PHONE_DESCRIPTION = "Select phone description";
    private static final String BIRTHDAY_PATTERN = "dd.M.yyyy";
    private static final String CHECK_DATES_PATTERN = "dd.M.yyyy HH:mm:ss";
    private static final int COUNT_PHONES_FOR_BAD_SALES = 10;

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
        model.addAttribute(ERROR_MSG, " ");
        return "createphone";
    }

    @PostMapping("/create/phone-description")
    public String savePhoneDescription(Model model, CreatePhoneDescription phoneDescription) throws Exception {
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
        if (phoneDescription.getDateAddedToDatabase().isBlank()) {
            return errorMsg(model, "Date added to database field is empty");
        }
        if (!Util.isValidDate(phoneDescription.getDateAddedToDatabase(), BIRTHDAY_PATTERN)) {
            return errorMsg(model, "Incorrect date added to database");
        }

        PhoneDescription phoneDescriptionForDb = PhoneMapper.mapCreatePhoneDescriptionToPhoneDescription(new PhoneDescription(), phoneDescription, brandService, chargeTypeService,
                communicationStandardService, operationSystemService, processorService, typeScreenService, countryService, new SimpleDateFormat(BIRTHDAY_PATTERN, Locale.ENGLISH));

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

        List<String> photos = setPhotosForGoogleDrive(createView.getPhoneFrontAndBack(),
                createView.getLeftSideAndRightSide(), createView.getUpSideAndDownSide());

        createView.setPhoneFrontAndBack(photos.get(0));
        createView.setLeftSideAndRightSide(photos.get(1));
        createView.setUpSideAndDownSide(photos.get(2));

        if (!viewService.save(createView)) {
            return errorMsg(model, "This view already exists");
        }

        return errorMsg(model, "View saved successfully");
    }

    @PostMapping("/create/phone")
    public String savePhone(Model model, CreatePhone phone) {
        if (phone.getView().equals(SELECT_PHONE_COLOR)) {
            return errorMsg(model, "You must choose a phone color");
        }
        if (phone.getPhoneDescription().equals(SELECT_PHONE_DESCRIPTION)) {
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

        String[] imeiList = phone.getImei().split(",");
        int check = -1;
        int value = 0;

        for (int i = 0; i < imeiList.length; i++) {
            if (!imeiList[i].isBlank()) {

                PhoneInstance phoneInstance = new PhoneInstance();
                phoneInstance.setPhone(phoneResult);
                phoneInstance.setImei(imeiList[i].trim());
                phoneInstance.setPrice(Double.parseDouble(phone.getPrice()));

                if (!phoneInstanceService.save(phoneInstance)) {
                    check = i;
                    break;
                }

                value++;
            }
        }

        if (check != -1) {
            return errorMsg(model, "This IMEI: " + imeiList[check].trim() + " already exists");
        }
        else if (value == 1) {
            return errorMsg(model, "Phone saved successfully");
        }
        else {
            return errorMsg(model, "Phones saved successfully");
        }
    }

    @PostMapping("/add-existing-phones")
    public String addExistingPhones(Model model, PhoneAddExisting phoneAddExisting) {
        if (phoneAddExisting.getPhoneId().equals("Select phone")) {
            return errorMsg(model, "You must select phone which is already in the database");
        }
        if (phoneAddExisting.getImei().isBlank()) {
            return errorMsg(model, "IMEI field is empty");
        }

        Phone phoneInDatabase = phoneInstanceService.findByIdPhone(phoneAddExisting.getPhoneId());

        String[] imeiList = phoneAddExisting.getImei().split(",");
        int check = -1;
        int value = 0;

        for (int i = 0; i < imeiList.length; i++) {
            if (!imeiList[i].isBlank()) {

                PhoneInstance phoneInstance = new PhoneInstance();
                phoneInstance.setPhone(phoneInDatabase);
                phoneInstance.setImei(imeiList[i].trim());
                phoneInstance.setPrice(phoneInstanceService.findPriceForPhoneForAdmin(phoneInDatabase));

                if (!phoneInstanceService.save(phoneInstance)) {
                    check = i;
                    break;
                }

                value++;
            }
        }

        if (check != -1) {
            return errorMsg(model, "This IMEI: " + imeiList[check].trim() + " already exists");
        }
        else if (value == 1) {
            return errorMsg(model, "Phone saved successfully");
        }
        else {
            return errorMsg(model, "Phones saved successfully");
        }
    }

    @GetMapping("/delete")
    public String deletePhone(Model model, @RequestParam(value = "success") String success) {
        PhoneDescriptionForDelete deletePhoneDescription = new PhoneDescriptionForDelete();
        ViewForDelete deleteView = new ViewForDelete();
        CreatePhone phone = new CreatePhone();

        model.addAttribute("views", viewService.findAllViews());
        model.addAttribute("phoneDescriptions", phoneDescriptionService.findAllPhoneDescriptions());
        model.addAttribute("phoneDelete", phone);
        model.addAttribute("deleteView", deleteView);
        model.addAttribute("deletePhoneDescription", deletePhoneDescription);
        model.addAttribute(SUCCESS, success);
        model.addAttribute(PHONES, getPhoneForStoreCompositions(true));
        return "deletephones";
    }

    @PostMapping("/delete/phone-description")
    public String deletePhoneDescription(PhoneDescriptionForDelete deletePhoneDescription) {
        if (deletePhoneDescription.getPhoneDescription().equals(SELECT_PHONE_DESCRIPTION)) {
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
        if (deleteView.getView().equals(SELECT_PHONE_COLOR)) {
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
    public String deletePhone(CreatePhone phoneDelete) {
        if (phoneDelete.getPhoneId().equals("Select phone")) {
            return "redirect:/admin/delete?success=You must select a phone";
        }

        phoneInstanceService.delete(phoneInstanceService.findByIdPhone(phoneDelete.getPhoneId()));
        return "redirect:/admin/delete?success=This phones successfully deleted";
    }

    @GetMapping("/change")
    public String changePhone(Model model, @RequestParam(value = "success") String success) {
        return createChangePhone(model, success, new CreatePhoneDescription(), new CreateView(), new CreatePhone(), new PhoneDescription(), new View(), new PhoneForStoreComposition(new Phone(), 0, 0));
    }

    @GetMapping("/change/phone-description")
    public String changePhoneGetInputValuesForPhoneDescription(Model model, @RequestParam(value = "id") String id) {
        PhoneDescription phoneDescriptionInput = phoneDescriptionService.findById(id);
        CreatePhoneDescription changePhoneDescription = new CreatePhoneDescription();
        changePhoneDescription.setPhoneDescriptionId(phoneDescriptionInput.getId());

        return createChangePhone(model, "", changePhoneDescription, new CreateView(), new CreatePhone(), phoneDescriptionInput, new View(), new PhoneForStoreComposition(new Phone(), 0, 0));
    }

    @GetMapping("/change/phone-view")
    public String changePhoneGetInputValuesForPhoneView(Model model, @RequestParam(value = "id") String id) {
        View viewInput = viewService.findById(id);
        CreateView changeView = new CreateView();
        changeView.setViewId(viewInput.getId());

        return createChangePhone(model, "", new CreatePhoneDescription(), changeView, new CreatePhone(), new PhoneDescription(), viewInput, new PhoneForStoreComposition(new Phone(), 0, 0));
    }

    @GetMapping("/change/phone")
    public String changePhoneGetInputValuesForPhone(Model model, @RequestParam(value = "id") String id) {
        PhoneForStoreComposition phoneInput = new PhoneForStoreComposition();
        Phone phone = phoneInstanceService.findByIdPhone(id);
        phoneInput.setPhone(phone);
        phoneInput.setPrice(phoneInstanceService.findPriceForPhoneForAdmin(phone));

        CreatePhone changePhone = new CreatePhone();
        changePhone.setPhoneId(phone.getId());

        return createChangePhone(model, "", new CreatePhoneDescription(), new CreateView(), changePhone, new PhoneDescription(), new View(), phoneInput);
    }

    @PostMapping("/change/phone-description")
    public String changePhoneDescription(CreatePhoneDescription changePhoneDescription) throws Exception {
        if (changePhoneDescription.getPhoneDescriptionId().isBlank()) {
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
            return "redirect:/admin/change?success=You must choose a processor";
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
        if (changePhoneDescription.getDateAddedToDatabase().isBlank()) {
            return "redirect:/admin/change?success=Date added to database field is empty";
        }
        if (!Util.isValidDate(changePhoneDescription.getDateAddedToDatabase(), BIRTHDAY_PATTERN)) {
            return "redirect:/admin/change?success=Incorrect date added to database";
        }

        PhoneDescription phoneDescription = phoneDescriptionService.findById(changePhoneDescription.getPhoneDescriptionId());

        PhoneDescription phoneDescriptionForDb = PhoneMapper.mapCreatePhoneDescriptionToPhoneDescription(phoneDescription, changePhoneDescription, brandService, chargeTypeService,
                communicationStandardService, operationSystemService, processorService, typeScreenService, countryService, new SimpleDateFormat(BIRTHDAY_PATTERN, Locale.ENGLISH));

        phoneDescriptionService.update(phoneDescriptionForDb);
        return "redirect:/admin/change?success=Phone description updated successfully";
    }

    @PostMapping("/change/view")
    public String changeView(CreateView changeView) {
        if (changeView.getViewId().isBlank()) {
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

        List<String> photos = setPhotosForGoogleDrive(changeView.getPhoneFrontAndBack(),
                changeView.getLeftSideAndRightSide(), changeView.getUpSideAndDownSide());

        changeView.setPhoneFrontAndBack(photos.get(0));
        changeView.setLeftSideAndRightSide(photos.get(1));
        changeView.setUpSideAndDownSide(photos.get(2));

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
        if (changePhone.getPhoneId().isBlank()) {
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

        model.addAttribute(PHONES, phones);
        model.addAttribute(SUCCESS, success);
        return "allphones";
    }

    @PostMapping("/allphones")
    public String deleteFromAllPhones(@RequestParam(value = "id") String id) {
        phoneInstanceService.deleteByIdPhoneInstance(id);

        return "redirect:/admin/allphones?success=The phone has been deleted successfully";
    }

    @GetMapping("/store-composition")
    public String getStoreComposition(Model model) {
        model.addAttribute(PHONE_FOR_STORE_COMPOSITIONS, getPhoneForStoreCompositions(true));
        model.addAttribute(FLAG, true);
        model.addAttribute(FLAG_FOR_STATISTIC, false);
        return "storecomposition";
    }

    @GetMapping("/orders")
    public String getOrders(Model model, @RequestParam(value = "success") String success) {
        setOrder(model, success, true, false);
        return "adminorders";
    }

    @GetMapping("/cancellation-of-orders")
    public String getOrdersForCancellation(Model model, @RequestParam(value = "success") String success) {
        setOrder(model, success, true, true);
        return "adminorders";
    }

    @GetMapping("/orders-history")
    public String getOrdersHistory(Model model, @RequestParam(value = "success") String success) {
        setOrder(model, success, false, false);
        return "adminorders";
    }

    @PutMapping("/close-order")
    public String closeOrder(@RequestParam(value = "id") String id) {
        clientCheckService.updateCheckClosed(true, id);

        return "redirect:/admin/orders?success=Check " + id + " successfully closed";
    }

    @PutMapping("/cancel-order")
    public String cancelOrder(@RequestParam(value = "id") String id) {
        phoneInstanceService.cancelOrder(id);
        clientCheckService.cancelCheck(id);

        return "redirect:/admin/cancellation-of-orders?success=Check " + id + " successfully canceled";
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
        model.addAttribute(SUCCESS, success);
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
    public String createBrands(NewBrand newBrand) {
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
        model.addAttribute(SUCCESS, success);
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
    public String createChargeTypes(ChargeType newChargeType) {
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
        model.addAttribute(SUCCESS, success);
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
    public String createCommunicationStandards(CommunicationStandard newCommunicationStandard) {
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
        model.addAttribute(SUCCESS, success);
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
    public String createOperationSystems(OperationSystem newOperationSystem) {
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
        model.addAttribute(SUCCESS, success);
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
    public String createTypeScreens(TypeScreen newTypeScreen) {
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
        model.addAttribute(SUCCESS, success);
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
    public String createProcessors(Processor newProcessor) {
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

    @GetMapping("/statistics")
    public String getStatistics() {
        return "statistics";
    }

    @GetMapping("/statistic-1")
    public String getFirstStatistic(Model model) {
        return modelAttributesForFirstStatistic(model, "", true);
    }

    @GetMapping("/statistic-1/show")
    public String getFirstStatisticShow(Model model, SalesSettingsForSpecificModels salesSettingsForSpecificModels) throws Exception {
        if (salesSettingsForSpecificModels.getId().equals("Select phone")) {
            return modelAttributesForFirstStatistic(model, "You must select phone", true);
        }
        if (salesSettingsForSpecificModels.getYear().equals("Select year")) {
            return modelAttributesForFirstStatistic(model, "You must select year", true);
        }

        List<SalesSettingsForSpecificModelsParams> salesSettingsForSpecificModelsParamsList = phoneInstanceService.getSalesSettingsForSpecificModelsParams(salesSettingsForSpecificModels);
        List<List<Object>> chartData = new ArrayList<>();
        List<Integer> maxSoldValue = new ArrayList<>();

        salesSettingsForSpecificModelsParamsList.forEach(salesSettingsForSpecificModelsParams -> {
            List<Object> chart = new ArrayList<>();
            AtomicInteger max = new AtomicInteger(-1);

            salesSettingsForSpecificModelsParams.getFields().forEach(field -> {
                chart.add(List.of(field.getMonth(), field.getSold()));

                if (field.getSold() > max.get()) {
                    max.set(field.getSold());
                }
            });

            chartData.add(new ArrayList<>(chart));
            maxSoldValue.add(max.get());
        });

        model.addAttribute("chartData", chartData);
        model.addAttribute("maxSoldValue", maxSoldValue);
        model.addAttribute("list", salesSettingsForSpecificModelsParamsList);
        model.addAttribute("forWhatPhone", salesSettingsForSpecificModelsParamsList.get(0).getPhone());
        model.addAttribute("forWhatYear", salesSettingsForSpecificModels.getYear());
        return modelAttributesForFirstStatistic(model, "", false);
    }

    @GetMapping("/statistic-2")
    public String getSecondStatistic(Model model) {
        return modelAttributesForSecondStatistic(model, "", true);
    }

    @GetMapping("/statistic-2/show")
    public String getSecondStatisticShow(Model model, SalesSettingsForSpecificModels salesSettingsForSpecificModels) throws Exception {
        if (salesSettingsForSpecificModels.getYear().equals("Select year")) {
            return modelAttributesForSecondStatistic(model, "You must select year", true);
        }

        List<MostPopularPhoneModels> mostPopularPhoneModelsList = phoneInstanceService.getMostPopularPhoneModels(salesSettingsForSpecificModels.getYear());

        model.addAttribute("list", mostPopularPhoneModelsList);
        model.addAttribute("forWhatYear", salesSettingsForSpecificModels.getYear());
        return modelAttributesForSecondStatistic(model, "", false);
    }

    @GetMapping("/statistic-3")
    public String getThirdStatistic(Model model) throws Exception {
        List<CustomerPreferencesByAge> customerPreferencesByAgeList = phoneInstanceService.getCustomerPreferencesByAge();
        Collections.reverse(customerPreferencesByAgeList);

        model.addAttribute("customerPreferencesByAgeList", customerPreferencesByAgeList);
        return "thirdstatistic";
    }

    @GetMapping("/statistic-4")
    public String getFourthStatistic(Model model) {
        List<PhoneForStoreComposition> phoneForStoreCompositions = new ArrayList<>();

        phoneInstanceService.findAllPhonesInDbForAdmin().forEach(phone -> {
            PhoneForStoreComposition phoneForStoreComposition = new PhoneForStoreComposition();
            phoneForStoreComposition.setPhone(phone);
            phoneForStoreComposition.setPrice(phoneInstanceService.findPriceForPhoneForAdmin(phone));
            phoneForStoreComposition.setCountInStore(phoneInstanceService.countPhonesInStoreForAdminForStatistic(phone));

            phoneForStoreCompositions.add(phoneForStoreComposition);
        });

        phoneForStoreCompositions.sort(Comparator.comparing(o -> o.getPhone().getPhoneDescription()));

        model.addAttribute(PHONE_FOR_STORE_COMPOSITIONS, phoneForStoreCompositions);
        model.addAttribute(FLAG, false);
        model.addAttribute(FLAG_FOR_STATISTIC, false);
        return "storecomposition";
    }

    @GetMapping("/statistic-5")
    public String getFifthStatistic(Model model) {
        List<RegisteredUser> registeredUsers = userDetailsServiceImpl.findAllUsersWhichHaveMoreThanOnePurchases();

        return setStatisticForFifthAndSixth(model, registeredUsers, true);
    }

    @GetMapping("/statistic-6")
    public String getSixthStatistic(Model model) {
        List<RegisteredUser> registeredUsers = userDetailsServiceImpl.findAllUsersWhichDoesntHaveAnyPurchases();

        return setStatisticForFifthAndSixth(model, registeredUsers, false);
    }

    @GetMapping("/statistic-7")
    public String getSeventhStatistic(Model model) throws Exception {
        List<PhoneForStoreComposition> phoneForStoreCompositions = new ArrayList<>();
        List<String> years = new ArrayList<>();
        SimpleDateFormat formatterForDateAdded = new SimpleDateFormat(BIRTHDAY_PATTERN, Locale.ENGLISH);
        SimpleDateFormat formatter = new SimpleDateFormat(CHECK_DATES_PATTERN, Locale.ENGLISH);
        String startDate = LocalDate.now().getDayOfMonth() + "." + (LocalDate.now().getMonthValue() - 2) + "." + (LocalDate.now().getYear() - 1) + " 00:00:00";
        String endDate = LocalDate.now().getDayOfMonth() + "." + (LocalDate.now().getMonthValue() - 2) + "." + (LocalDate.now().getYear()) + " 23:59:59";

        phoneInstanceService.findAllPhonesWithBetweenTime(formatter.parse(startDate), formatter.parse(endDate)).forEach(phone -> {
            int count = phoneInstanceService.countPhonesInStoreForAdminForStatistic(phone);

            if (count <= COUNT_PHONES_FOR_BAD_SALES) {
                PhoneForStoreComposition phoneForStoreComposition = new PhoneForStoreComposition();
                phoneForStoreComposition.setPhone(phone);
                phoneForStoreComposition.setPrice(phoneInstanceService.findPriceForPhoneForAdmin(phone));
                phoneForStoreComposition.setCountInStore(count);

                phoneForStoreCompositions.add(phoneForStoreComposition);
                years.add(formatterForDateAdded.format(phone.getPhoneDescription().getDateAddedToDatabase()));
            }
        });

        phoneForStoreCompositions.sort(Comparator.comparing(o -> o.getPhone().getPhoneDescription()));

        model.addAttribute(PHONE_FOR_STORE_COMPOSITIONS, phoneForStoreCompositions);
        model.addAttribute(FLAG, false);
        model.addAttribute(FLAG_FOR_STATISTIC, true);
        model.addAttribute(YEARS, years);
        model.addAttribute("startDate", startDate.replace(" 00:00:00", ""));
        model.addAttribute("endDate", endDate.replace(" 23:59:59", ""));
        return "storecomposition";
    }

    @GetMapping("/all-registered-users")
    public String getAllRegisteredUsers(Model model) {
        List<RegisteredUser> registeredUsers = userDetailsServiceImpl.findAllUsersForAdmin();
        Collections.sort(registeredUsers);

        model.addAttribute(YEARS, Util.getYearsForListRegisteredUsers(registeredUsers, BIRTHDAY_PATTERN));
        model.addAttribute("registeredUsers", registeredUsers);
        return "allregisteredusers";
    }

    @GetMapping("/orders-for-user-id")
    public String getOrdersForUserId(@RequestParam(value = "id") String id, @RequestParam(value = "flag") boolean flag, Model model) {
        List<ClientCheck> checks = clientCheckService.findAllChecksForUserId(id);
        RegisteredUser registeredUser = userDetailsServiceImpl.findById(id);
        List<OrdersForSelectUserForAdmin> orders = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat(CHECK_DATES_PATTERN, Locale.ENGLISH);
        SimpleDateFormat formatterBirthday = new SimpleDateFormat(BIRTHDAY_PATTERN, Locale.ENGLISH);

        checks.forEach(check -> orders.add(Util.createOrderForSelectUserForAdmin(check, formatter, phoneInstanceService)));

        Collections.sort(orders);

        model.addAttribute(FLAG, flag);
        model.addAttribute("orders", orders);
        model.addAttribute("registeredUser", registeredUser);
        model.addAttribute(YEARS, formatterBirthday.format(registeredUser.getDateOfBirth()));
        return "allordersforuser";
    }

    @GetMapping("/find-by-imei")
    public String findPhoneByImei(Model model) {
        return setFindPhoneByImei(model, null, "");
    }

    @GetMapping("/find-by-imei-finding")
    public String findPhoneByImeiFinding(Model model, FindPhoneByImei findPhoneByImei) {
        if (findPhoneByImei.getImei().isBlank()) {
            return setFindPhoneByImei(model, null, "Phone IMEI field is empty");
        }

        Optional<PhoneInstance> resultPhone = phoneInstanceService.findPhoneByImei(findPhoneByImei.getImei());

        return (resultPhone.isEmpty()) ? setFindPhoneByImei(model, null, "This phone IMEI not found") :
                setFindPhoneByImei(model, resultPhone.get(), "");
    }

    @GetMapping("/find-registered-user-last-name")
    public String findUserByLastName(Model model) {
        return setFindUserByLastName(model, new ArrayList<>(), "");
    }

    @GetMapping("/find-registered-user-last-name-finding")
    public String findUserByLastNameFinding(Model model, FindUserByLastName findUserByLastName) {
        if (findUserByLastName.getLastName().isBlank()) {
            return setFindUserByLastName(model, new ArrayList<>(), "User last name field is empty");
        }

        List<RegisteredUser> users = userDetailsServiceImpl.findUserByLastName(findUserByLastName.getLastName());

        return (users.isEmpty()) ? setFindUserByLastName(model, new ArrayList<>(), "User with this last name not found") :
                setFindUserByLastName(model, users, "");
    }

    @PostMapping("/find-phone-by-imei-delete")
    public String deleteFromFindPhoneByImei(@RequestParam(value = "id") String id, Model model) {
        phoneInstanceService.deleteByIdPhoneInstance(id);

        return setFindPhoneByImei(model, null, "The phone has been deleted successfully");
    }

    @GetMapping("/find-client-order-number")
    public String findOrderByNumber(Model model) {
        return setFindOrderByNumber(model, null, "");
    }

    @GetMapping("/find-client-order-number-finding")
    public String findOrderByNumberFinding(Model model, FindOrderByNumber findOrderByNumber) {
        if (findOrderByNumber.getNumber().isBlank()) {
            return setFindOrderByNumber(model, null, "Order number field is empty");
        }

        Optional<ClientCheck> order = clientCheckService.findById(findOrderByNumber.getNumber());

        return (order.isEmpty()) ? setFindOrderByNumber(model, null, "Order with this number not found") :
                setFindOrderByNumber(model, order.get(), "");
    }

    private String setStatisticForFifthAndSixth(Model model, List<RegisteredUser> registeredUsers, boolean flag) {
        Collections.sort(registeredUsers);
        int countUsers = userDetailsServiceImpl.countAllUsers();

        String pattern = "##0.00";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);

        double percent = (double) (registeredUsers.size() * 100) / countUsers;

        SimpleDateFormat formatter = new SimpleDateFormat(BIRTHDAY_PATTERN, Locale.ENGLISH);
        List<String> years = new ArrayList<>();

        registeredUsers.forEach(user -> years.add(formatter.format(user.getDateOfBirth())));

        if (flag) {
            List<Integer> orderCount = new ArrayList<>();
            registeredUsers.forEach(user -> orderCount.add(userDetailsServiceImpl.countUserOrdersForUserId(user.getId())));

            model.addAttribute("orderCount", orderCount);
        }

        model.addAttribute(FLAG, flag);
        model.addAttribute(YEARS, years);
        model.addAttribute("percent", decimalFormat.format(percent));
        model.addAttribute("registeredUsers", registeredUsers);
        return "sixthandfifthstatistic";
    }

    private String errorMsg(Model model, String errorMsg) {
        tuneModel(model);
        model.addAttribute(ERROR_MSG, errorMsg);
        return "createphone";
    }

    private void tuneModel(Model model) {
        CreatePhoneDescription phoneDescription = new CreatePhoneDescription();
        View createView = new View();
        CreatePhone phone = new CreatePhone();

        model.addAttribute("phone", phone);
        model.addAttribute("createView", createView);
        model.addAttribute("phoneDescription", phoneDescription);
        model.addAttribute(PHONES, getPhoneForStoreCompositions(true));
        model.addAttribute("phoneAddExisting", new PhoneAddExisting());
        tuneModelForCreateAndChange(model);
    }

    private void tuneModelForCreateAndChange(Model model) {
        model.addAttribute("views", viewService.findAllViews());
        model.addAttribute("phoneDescriptions", phoneDescriptionService.findAllPhoneDescriptions());
        model.addAttribute("brands", brandService.findAllBrandsNames());
        model.addAttribute("chargeTypes", chargeTypeService.findAllChargeTypesNames());
        model.addAttribute("communicationStandards", communicationStandardService.findAllCommunicationStandardsNames());
        model.addAttribute("operationSystems", operationSystemService.findAllOperationSystemsNames());
        model.addAttribute("processors", processorService.findAllProcessorsNames());
        model.addAttribute("typeScreens", typeScreenService.findAllTypeScreensNames());
        model.addAttribute("countries", countryService.findAllCountriesNames());
    }

    private List<String> setPhotosForGoogleDrive(String phoneFrontAndBack, String leftSideAndRightSide, String upSideAndDownSide) {
        List<String> photos = new ArrayList<>();
        photos.add(phoneFrontAndBack);
        photos.add(leftSideAndRightSide);
        photos.add(upSideAndDownSide);

        if (phoneFrontAndBack.contains(REGEX)) {
            String[] linkParts = phoneFrontAndBack.split(LEFT_REGEX);
            String pictureName = linkParts[1].replace(RIGHT_REGEX, "");
            photos.set(0, GOOGLE_DRIVE_VIEW_URL + pictureName);
        }
        if (leftSideAndRightSide.contains(REGEX)) {
            String[] linkParts = leftSideAndRightSide.split(LEFT_REGEX);
            String pictureName = linkParts[1].replace(RIGHT_REGEX, "");
            photos.set(1, GOOGLE_DRIVE_VIEW_URL + pictureName);
        }
        if (upSideAndDownSide.contains(REGEX)) {
            String[] linkParts = upSideAndDownSide.split(LEFT_REGEX);
            String pictureName = linkParts[1].replace(RIGHT_REGEX, "");
            photos.set(2, GOOGLE_DRIVE_VIEW_URL + pictureName);
        }

        return photos;
    }

    private List<PhoneForStoreComposition> getPhoneForStoreCompositions(boolean state) {
        List<PhoneForStoreComposition> phoneForStoreCompositions = new ArrayList<>();
        List<Phone> phones;

        if (state) {
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

    private void setOrder(Model model, String success, boolean flag, boolean isCancellation) {
        List<UserOrdersForAdmin> orders = userDetailsServiceImpl.getUsersOrdersForAdmin(flag);
        orders.forEach(order -> order.getChecks().forEach(check -> order.getTotalPrices().add(phoneInstanceService.findPriceForClientCheckId(check.getId()))));
        Collections.sort(orders);

        SimpleDateFormat formatter = new SimpleDateFormat(CHECK_DATES_PATTERN, Locale.ENGLISH);
        orders.forEach(order -> order.getChecks().forEach(check -> order.getDates().add(formatter.format(check.getCreated()))));

        if (!flag) {
            orders.forEach(order -> order.getChecks().forEach(check -> order.getDatesClosed().add(formatter.format(check.getClosedDate()))));
        }

        model.addAttribute("orders", orders);
        model.addAttribute(SUCCESS, success);
        model.addAttribute(FLAG, flag);
        model.addAttribute("cancel", isCancellation);
    }

    private String modelAttributesForFirstStatistic(Model model, String errorMsg, boolean flag) {
        model.addAttribute(YEARS, Util.getListYears());
        model.addAttribute(PHONES, phoneInstanceService.findAllPhonesInDb());
        model.addAttribute("salesSettingsForSpecificModels", new SalesSettingsForSpecificModels());
        model.addAttribute(FLAG, flag);
        model.addAttribute(ERROR_MSG, errorMsg);
        return "firststatistic";
    }

    private String modelAttributesForSecondStatistic(Model model, String errorMsg, boolean flag) {
        model.addAttribute(YEARS, Util.getListYears());
        model.addAttribute("salesSettingsForSpecificModels", new SalesSettingsForSpecificModels());
        model.addAttribute(FLAG, flag);
        model.addAttribute(ERROR_MSG, errorMsg);
        return "secondstatistic";
    }

    private String setFindPhoneByImei(Model model, PhoneInstance resultPhone, String success) {
        model.addAttribute("resultPhone", resultPhone);
        model.addAttribute(SUCCESS, success);
        model.addAttribute("findPhoneByImei", new FindPhoneByImei());
        return "findphonebyimei";
    }

    private String setFindUserByLastName(Model model, List<RegisteredUser> users, String success) {
        if (!users.isEmpty()) {
            Collections.sort(users);
            model.addAttribute(YEARS, Util.getYearsForListRegisteredUsers(users, BIRTHDAY_PATTERN));
        }

        model.addAttribute("users", users);
        model.addAttribute(SUCCESS, success);
        model.addAttribute("findUserByLastName", new FindUserByLastName());
        return "finduserbylastname";
    }

    private String setFindOrderByNumber(Model model, ClientCheck order, String success) {
        if (order != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(CHECK_DATES_PATTERN, Locale.ENGLISH);
            SimpleDateFormat formatterBirthday = new SimpleDateFormat(BIRTHDAY_PATTERN, Locale.ENGLISH);

            RegisteredUser registeredUser = userDetailsServiceImpl.findById(clientCheckService.getUserIdForCheckId(order.getId()));

            model.addAttribute("registeredUser", registeredUser);
            model.addAttribute(YEARS, formatterBirthday.format(registeredUser.getDateOfBirth()));
            model.addAttribute("order", Util.createOrderForSelectUserForAdmin(order, formatter, phoneInstanceService));
        }
        else {
            model.addAttribute("order", null);
        }

        model.addAttribute(SUCCESS, success);
        model.addAttribute("findOrderByNumber", new FindOrderByNumber());
        return "findorderbynumber";
    }

    private String createChangePhone(Model model, String success, CreatePhoneDescription changePhoneDescription, CreateView changeView,
                                     CreatePhone changePhone, PhoneDescription phoneDescriptionInput, View viewInput, PhoneForStoreComposition phoneInput) {
        if (phoneDescriptionInput.getId() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(BIRTHDAY_PATTERN, Locale.ENGLISH);

            model.addAttribute("dateAddedToDb", formatter.format(phoneDescriptionInput.getDateAddedToDatabase()));
        }
        else {
            model.addAttribute("dateAddedToDb", "");
        }

        model.addAttribute("phoneDescriptionInput", phoneDescriptionInput);
        model.addAttribute("viewInput", viewInput);
        model.addAttribute("phoneInput", phoneInput);
        model.addAttribute(PHONES, getPhoneForStoreCompositions(false));
        model.addAttribute("changePhone", changePhone);
        model.addAttribute("changeView", changeView);
        model.addAttribute("changePhoneDescription", changePhoneDescription);
        model.addAttribute(SUCCESS, success);
        tuneModelForCreateAndChange(model);
        return "changephones";
    }
}