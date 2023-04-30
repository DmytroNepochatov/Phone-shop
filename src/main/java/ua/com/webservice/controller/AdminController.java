package ua.com.webservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.webservice.mapper.OrderMapper;
import ua.com.webservice.mapper.PhoneMapper;
import ua.com.webservice.model.accessory.*;
import ua.com.webservice.model.check.ClientCheck;
import ua.com.webservice.model.dto.*;
import ua.com.webservice.model.phone.Phone;
import ua.com.webservice.model.phone.PhoneDescription;
import ua.com.webservice.model.phone.PhoneInstance;
import ua.com.webservice.model.phone.View;
import ua.com.webservice.model.user.RegisteredUser;
import ua.com.webservice.service.brand.BrandService;
import ua.com.webservice.service.chargetype.ChargeTypeService;
import ua.com.webservice.service.clientcheck.ClientCheckService;
import ua.com.webservice.service.communicationstandard.CommunicationStandardService;
import ua.com.webservice.service.country.CountryService;
import ua.com.webservice.service.mailsender.MailSender;
import ua.com.webservice.service.operationsystem.OperationSystemService;
import ua.com.webservice.service.phone.PhoneInstanceService;
import ua.com.webservice.service.phonedescription.PhoneDescriptionService;
import ua.com.webservice.service.processor.ProcessorService;
import ua.com.webservice.service.typescreen.TypeScreenService;
import ua.com.webservice.service.user.UserDetailsServiceImpl;
import ua.com.webservice.service.view.ViewService;
import ua.com.webservice.util.Util;
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
    private final MailSender mailSender;
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
    private static final String SELECT_PHONE_COLOR = "Виберіть колір телефону";
    private static final String SELECT_PHONE_DESCRIPTION = "Виберіть опис телефону";
    private static final String BIRTHDAY_PATTERN = "dd.M.yyyy";
    private static final String CHECK_DATES_PATTERN = "dd.M.yyyy HH:mm:ss";
    private static final String FOR_WHAT_YEAR = "forWhatYear";
    private static final String THREE_YEARS = "3 роки";
    private static final int COUNT_PHONES_FOR_BAD_SALES = 12;

    public AdminController(UserDetailsServiceImpl userDetailsServiceImpl, PhoneInstanceService phoneInstanceService, BrandService brandService,
                           ChargeTypeService chargeTypeService, CommunicationStandardService communicationStandardService,
                           OperationSystemService operationSystemService, ProcessorService processorService,
                           TypeScreenService typeScreenService, ClientCheckService clientCheckService,
                           CountryService countryService, ViewService viewService, PhoneDescriptionService phoneDescriptionService, MailSender mailSender) {
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
        this.mailSender = mailSender;
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
        if (phoneDescription.getBrand().equals("Виберіть бренд")) {
            return errorMsg(model, "Ви повинні вибрати бренд");
        }
        if (phoneDescription.getChargeType().equals("Виберіть тип зарядного пристрою")) {
            return errorMsg(model, "Ви повинні вибрати тип зарядного пристрою");
        }
        if (phoneDescription.getCommunicationStandard().equals("Виберіть стандарт звязку")) {
            return errorMsg(model, "Ви повинні вибрати тип стандарту зв'язку");
        }
        if (phoneDescription.getOperationSystem().equals("Виберіть операційну систему")) {
            return errorMsg(model, "Ви повинні вибрати операційну систему");
        }
        if (phoneDescription.getProcessor().equals("Виберіть процесор")) {
            return errorMsg(model, "Ви повинні вибрати процесор");
        }
        if (phoneDescription.getTypeScreen().equals("Виберіть тип екрану")) {
            return errorMsg(model, "Ви повинні вибрати тип екрану");
        }
        if (phoneDescription.getCountry().equals("Виберіть країну виробника")) {
            return errorMsg(model, "Ви повинні вибрати країну виробника");
        }
        if (phoneDescription.getName().isBlank()) {
            return errorMsg(model, "Поле Назва порожнє");
        }
        if (phoneDescription.getSeries().isBlank()) {
            return errorMsg(model, "Поле Серія порожнє");
        }
        if (!isNumber(phoneDescription.getDiagonal())) {
            return errorMsg(model, "Неправильна діагональ");
        }
        if (Float.parseFloat(phoneDescription.getDiagonal()) <= 0.0) {
            return errorMsg(model, "Неправильна діагональ");
        }
        if (phoneDescription.getDisplayResolution().isBlank()) {
            return errorMsg(model, "Поле Роздільна здатність дисплея порожнє");
        }
        if (!isNumber(phoneDescription.getScreenRefreshRate())) {
            return errorMsg(model, "Неправильна частота оновлення екрана");
        }
        if (Integer.parseInt(phoneDescription.getScreenRefreshRate()) <= 0.0) {
            return errorMsg(model, "Неправильна частота оновлення екрана");
        }
        if (!isNumber(phoneDescription.getNumberOfSimCards())) {
            return errorMsg(model, "Неправильна кількість сім-карт");
        }
        if (Integer.parseInt(phoneDescription.getNumberOfSimCards()) <= 0.0) {
            return errorMsg(model, "Неправильна кількість сім-карт");
        }
        if (!isNumber(phoneDescription.getNumberOfFrontCameras())) {
            return errorMsg(model, "Неправильна кількість фронтальних камер");
        }
        if (Integer.parseInt(phoneDescription.getNumberOfFrontCameras()) <= 0.0) {
            return errorMsg(model, "Неправильна кількість фронтальних камер");
        }
        if (phoneDescription.getInfoAboutFrontCameras().isBlank()) {
            return errorMsg(model, "Поле Інформація про фронтальні камери порожнє");
        }
        if (!isNumber(phoneDescription.getNumberOfMainCameras())) {
            return errorMsg(model, "Неправильна кількість основних камер");
        }
        if (Integer.parseInt(phoneDescription.getNumberOfMainCameras()) <= 0.0) {
            return errorMsg(model, "Неправильна кількість основних камер");
        }
        if (phoneDescription.getInfoAboutMainCameras().isBlank()) {
            return errorMsg(model, "Поле Інформація про основні камери порожнє");
        }
        if (!isNumber(phoneDescription.getWeight())) {
            return errorMsg(model, "Неправильна вага");
        }
        if (Float.parseFloat(phoneDescription.getWeight()) <= 0.0) {
            return errorMsg(model, "Неправильна вага");
        }
        if (!isNumber(phoneDescription.getHeight())) {
            return errorMsg(model, "Неправильна висота");
        }
        if (Float.parseFloat(phoneDescription.getHeight()) <= 0.0) {
            return errorMsg(model, "Неправильна висота");
        }
        if (!isNumber(phoneDescription.getWidth())) {
            return errorMsg(model, "Неправильна ширина");
        }
        if (Float.parseFloat(phoneDescription.getWidth()) <= 0.0) {
            return errorMsg(model, "Неправильна ширина");
        }
        if (phoneDescription.getDegreeOfMoistureProtection().isBlank()) {
            return errorMsg(model, "Поле Ступінь вологозахисту порожнє");
        }
        if (!isNumber(phoneDescription.getGuaranteeTimeMonths())) {
            return errorMsg(model, "Неправильний термін гарантії в місяцях");
        }
        if (Integer.parseInt(phoneDescription.getGuaranteeTimeMonths()) <= 0.0) {
            return errorMsg(model, "Неправильний термін гарантії в місяцях");
        }

        PhoneDescription phoneDescriptionForDb = PhoneMapper.mapCreatePhoneDescriptionToPhoneDescription(new PhoneDescription(), phoneDescription, brandService, chargeTypeService,
                communicationStandardService, operationSystemService, processorService, typeScreenService, countryService, new SimpleDateFormat(BIRTHDAY_PATTERN, Locale.ENGLISH), true);

        if (!phoneDescriptionService.save(phoneDescriptionForDb)) {
            return errorMsg(model, "Опис телефону для цієї  моделі телефону вже існує");
        }

        return errorMsg(model, "Опис телефону успішно збережено");
    }

    @PostMapping("/create/view")
    public String saveView(Model model, View createView) {
        if (createView.getColor().isBlank()) {
            return errorMsg(model, "Поле кольору порожнє");
        }
        if (createView.getColorForWhichPhone().equals("Виберіть колір для якого телефону")) {
            return errorMsg(model, "Ви повинні вибрати колір для якого телефону");
        }
        if (createView.getPhoneFrontAndBack().isBlank()) {
            return errorMsg(model, "Поле Зображення 1 порожнє");
        }
        if (createView.getLeftSideAndRightSide().isBlank()) {
            return errorMsg(model, "Поле Зображення 2 порожнє");
        }
        if (createView.getUpSideAndDownSide().isBlank()) {
            return errorMsg(model, "Поле Зображення 3 порожнє");
        }

        List<String> photos = setPhotosForGoogleDrive(createView.getPhoneFrontAndBack(),
                createView.getLeftSideAndRightSide(), createView.getUpSideAndDownSide());

        createView.setPhoneFrontAndBack(photos.get(0));
        createView.setLeftSideAndRightSide(photos.get(1));
        createView.setUpSideAndDownSide(photos.get(2));

        if (!viewService.save(createView)) {
            return errorMsg(model, "Такий зовнішній вигляд вже існує");
        }

        return errorMsg(model, "Зовнішній вигляд успішно збережено");
    }

    @PostMapping("/create/phone")
    public String savePhone(Model model, CreatePhone phone) {
        if (phone.getView().equals(SELECT_PHONE_COLOR)) {
            return errorMsg(model, "Ви повинні вибрати колір телефону");
        }
        if (phone.getPhoneDescription().equals(SELECT_PHONE_DESCRIPTION)) {
            return errorMsg(model, "Ви повинні вибрати опис телефону");
        }
        if (!isNumber(phone.getAmountOfBuiltInMemory())) {
            return errorMsg(model, "Неправильний обсяг вбудованої пам'яті");
        }
        if (Integer.parseInt(phone.getAmountOfBuiltInMemory()) <= 0.0) {
            return errorMsg(model, "Неправильний обсяг вбудованої пам'яті");
        }
        if (!isNumber(phone.getAmountOfRam())) {
            return errorMsg(model, "Неправильний обсяг оперативної пам'яті");
        }
        if (Integer.parseInt(phone.getAmountOfRam()) <= 0.0) {
            return errorMsg(model, "Неправильний обсяг оперативної пам'яті");
        }
        if (!isNumber(phone.getPrice())) {
            return errorMsg(model, "Неправильна ціна");
        }
        if (Integer.parseInt(phone.getPrice()) <= 0) {
            return errorMsg(model, "Неправильна ціна");
        }
        if (phone.getImei().isBlank()) {
            return errorMsg(model, "Поле IMEI порожнє");
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
                phoneInstance.setPrice(Integer.parseInt(phone.getPrice()));

                if (!phoneInstanceService.save(phoneInstance)) {
                    check = i;
                    break;
                }

                value++;
            }
        }

        if (check != -1) {
            return errorMsg(model, "Цей IMEI: " + imeiList[check].trim() + " вже існує");
        }
        else if (value == 1) {
            return errorMsg(model, "Телефон успішно збережено");
        }
        else {
            return errorMsg(model, "Телефони успішно збережено");
        }
    }

    @PostMapping("/add-existing-phones")
    public String addExistingPhones(Model model, PhoneAddExisting phoneAddExisting) {
        if (phoneAddExisting.getPhoneId().equals("Виберіть телефон")) {
            return errorMsg(model, "Ви повинні вибрати телефон, який вже є в базі даних");
        }
        if (phoneAddExisting.getImei().isBlank()) {
            return errorMsg(model, "Поле IMEI порожнє");
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
            return errorMsg(model, "Цей IMEI: " + imeiList[check].trim() + " вже існує");
        }
        else if (value == 1) {
            return errorMsg(model, "Телефон успішно збережено");
        }
        else {
            return errorMsg(model, "Телефони успішно збережено");
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
    public String deletePhoneDescription(Model model, PhoneDescriptionForDelete deletePhoneDescription) {
        if (deletePhoneDescription.getPhoneDescription().equals(SELECT_PHONE_DESCRIPTION)) {
            return deletePhone(model, "Ви повинні вибрати опис телефону");
        }

        if (!phoneDescriptionService.delete(deletePhoneDescription.getPhoneDescription())) {
            return deletePhone(model, "Цей опис телефону вже використовувався");
        }
        else {
            return deletePhone(model, "Цей опис телефону успішно видалено");
        }
    }

    @PostMapping("/delete/view")
    public String deleteView(Model model, ViewForDelete deleteView) {
        if (deleteView.getView().equals(SELECT_PHONE_COLOR)) {
            return deletePhone(model, "Ви повинні вибрати колір телефону");
        }

        if (!viewService.delete(deleteView.getView())) {
            return deletePhone(model, "Цей зовнішній вигляд телефону вже використовувався");
        }
        else {
            return deletePhone(model, "Цей зовнішній вигляд телефону успішно видалено");
        }
    }

    @PostMapping("/delete/phone")
    public String deletePhone(Model model, CreatePhone phoneDelete) {
        if (phoneDelete.getPhoneId().equals("Виберіть телефон")) {
            return deletePhone(model, "Ви повинні вибрати телефон");
        }

        phoneInstanceService.delete(phoneInstanceService.findByIdPhone(phoneDelete.getPhoneId()));
        return deletePhone(model, "Ці телефони успішно видалено");
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
    public String changePhoneDescription(Model model, CreatePhoneDescription changePhoneDescription) throws Exception {
        if (changePhoneDescription.getPhoneDescriptionId().isBlank()) {
            return changePhone(model,"Ви повинні вибрати опис телефону");
        }
        if (changePhoneDescription.getBrand().equals("Виберіть бренд")) {
            return changePhone(model,"Ви повинні вибрати бренд");
        }
        if (changePhoneDescription.getChargeType().equals("Виберіть тип зарядного пристрою")) {
            return changePhone(model,"Ви повинні вибрати тип зарядного пристрою");
        }
        if (changePhoneDescription.getCommunicationStandard().equals("Виберіть стандарт звязку")) {
            return changePhone(model,"Ви повинні вибрати тип стандарту зв'язку");
        }
        if (changePhoneDescription.getOperationSystem().equals("Виберіть операційну систему")) {
            return changePhone(model,"Ви повинні вибрати операційну систему");
        }
        if (changePhoneDescription.getProcessor().equals("Виберіть процесор")) {
            return changePhone(model,"Ви повинні вибрати процесор");
        }
        if (changePhoneDescription.getTypeScreen().equals("Виберіть тип екрану")) {
            return changePhone(model,"Ви повинні вибрати тип екрану");
        }
        if (changePhoneDescription.getCountry().equals("Виберіть країну виробника")) {
            return changePhone(model,"Ви повинні вибрати країну виробника");
        }
        if (changePhoneDescription.getName().isBlank()) {
            return changePhone(model,"Поле Назва порожнє");
        }
        if (changePhoneDescription.getSeries().isBlank()) {
            return changePhone(model,"Поле Серія порожнє");
        }
        if (!isNumber(changePhoneDescription.getDiagonal())) {
            return changePhone(model,"Неправильна діагональ");
        }
        if (Float.parseFloat(changePhoneDescription.getDiagonal()) <= 0.0) {
            return changePhone(model,"Неправильна діагональ");
        }
        if (changePhoneDescription.getDisplayResolution().isBlank()) {
            return changePhone(model,"Поле Роздільна здатність дисплея порожнє");
        }
        if (!isNumber(changePhoneDescription.getScreenRefreshRate())) {
            return changePhone(model,"Неправильна частота оновлення екрана");
        }
        if (Integer.parseInt(changePhoneDescription.getScreenRefreshRate()) <= 0.0) {
            return changePhone(model,"Неправильна частота оновлення екрана");
        }
        if (!isNumber(changePhoneDescription.getNumberOfSimCards())) {
            return changePhone(model,"Неправильна кількість сім-карт");
        }
        if (Integer.parseInt(changePhoneDescription.getNumberOfSimCards()) <= 0.0) {
            return changePhone(model,"Неправильна кількість сім-карт");
        }
        if (!isNumber(changePhoneDescription.getNumberOfFrontCameras())) {
            return changePhone(model,"Неправильна кількість фронтальних камер");
        }
        if (Integer.parseInt(changePhoneDescription.getNumberOfFrontCameras()) <= 0.0) {
            return changePhone(model,"Неправильна кількість фронтальних камер");
        }
        if (changePhoneDescription.getInfoAboutFrontCameras().isBlank()) {
            return changePhone(model,"Поле Інформація про фронтальні камери порожнє");
        }
        if (!isNumber(changePhoneDescription.getNumberOfMainCameras())) {
            return changePhone(model,"Неправильна кількість основних камер");
        }
        if (Integer.parseInt(changePhoneDescription.getNumberOfMainCameras()) <= 0.0) {
            return changePhone(model,"Неправильна кількість основних камер");
        }
        if (changePhoneDescription.getInfoAboutMainCameras().isBlank()) {
            return changePhone(model,"Поле Інформація про основні камери порожнє");
        }
        if (!isNumber(changePhoneDescription.getWeight())) {
            return changePhone(model,"Неправильна вага");
        }
        if (Float.parseFloat(changePhoneDescription.getWeight()) <= 0.0) {
            return changePhone(model,"Неправильна вага");
        }
        if (!isNumber(changePhoneDescription.getHeight())) {
            return changePhone(model,"Неправильна висота");
        }
        if (Float.parseFloat(changePhoneDescription.getHeight()) <= 0.0) {
            return changePhone(model,"Неправильна висота");
        }
        if (!isNumber(changePhoneDescription.getWidth())) {
            return changePhone(model,"Неправильна ширина");
        }
        if (Float.parseFloat(changePhoneDescription.getWidth()) <= 0.0) {
            return changePhone(model,"Неправильна ширина");
        }
        if (changePhoneDescription.getDegreeOfMoistureProtection().isBlank()) {
            return changePhone(model,"Поле Ступінь вологозахисту порожнє");
        }
        if (!isNumber(changePhoneDescription.getGuaranteeTimeMonths())) {
            return changePhone(model,"Неправильний термін гарантії в місяцях");
        }
        if (Integer.parseInt(changePhoneDescription.getGuaranteeTimeMonths()) <= 0.0) {
            return changePhone(model,"Неправильний термін гарантії в місяцях");
        }
        if (changePhoneDescription.getDateAddedToDatabase().isBlank()) {
            return changePhone(model,"Поле Дата додавання до бази даних порожнє");
        }
        if (!Util.isValidDate(changePhoneDescription.getDateAddedToDatabase(), BIRTHDAY_PATTERN)) {
            return changePhone(model,"Неправильна дата додавання до бази даних");
        }

        PhoneDescription phoneDescription = phoneDescriptionService.findById(changePhoneDescription.getPhoneDescriptionId());

        PhoneDescription phoneDescriptionForDb = PhoneMapper.mapCreatePhoneDescriptionToPhoneDescription(phoneDescription, changePhoneDescription, brandService, chargeTypeService,
                communicationStandardService, operationSystemService, processorService, typeScreenService, countryService, new SimpleDateFormat(BIRTHDAY_PATTERN, Locale.ENGLISH), false);

        phoneDescriptionService.update(phoneDescriptionForDb);
        return changePhone(model,"Опис телефону успішно оновлено");
    }

    @PostMapping("/change/view")
    public String changeView(Model model, CreateView changeView) {
        if (changeView.getViewId().isBlank()) {
            return changePhone(model,"Ви повинні вибрати колір для якого телефону");
        }
        if (changeView.getColor().isBlank()) {
            return changePhone(model,"Поле кольору порожнє");
        }
        if (changeView.getPhoneFrontAndBack().isBlank()) {
            return changePhone(model,"Поле Зображення 1 порожнє");
        }
        if (changeView.getLeftSideAndRightSide().isBlank()) {
            return changePhone(model,"Поле Зображення 2 порожнє");
        }
        if (changeView.getUpSideAndDownSide().isBlank()) {
            return changePhone(model,"Поле Зображення 3 порожнє");
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
        return changePhone(model,"Зовнішній вигляд успішно оновлено");
    }

    @PostMapping("/change/phone")
    public String changePhone(Model model, CreatePhone changePhone) {
        if (changePhone.getPhoneId().isBlank()) {
            return changePhone(model,"Ви повинні вибрати телефон");
        }
        if (!isNumber(changePhone.getAmountOfBuiltInMemory())) {
            return changePhone(model,"Неправильний обсяг вбудованої пам'яті");
        }
        if (Integer.parseInt(changePhone.getAmountOfBuiltInMemory()) <= 0.0) {
            return changePhone(model,"Неправильний обсяг вбудованої пам'яті");
        }
        if (!isNumber(changePhone.getAmountOfRam())) {
            return changePhone(model,"Неправильний обсяг оперативної пам'яті");
        }
        if (Integer.parseInt(changePhone.getAmountOfRam()) <= 0.0) {
            return changePhone(model,"Неправильний обсяг оперативної пам'яті");
        }
        if (!isNumber(changePhone.getPrice())) {
            return changePhone(model,"Неправильна ціна");
        }
        if (Integer.parseInt(changePhone.getPrice()) <= 0) {
            return changePhone(model,"Неправильна ціна");
        }

        Phone phone = phoneInstanceService.findByIdPhone(changePhone.getPhoneId());

        phoneInstanceService.updatePhone(phone, Integer.parseInt(changePhone.getAmountOfBuiltInMemory()),
                Integer.parseInt(changePhone.getAmountOfRam()));
        phoneInstanceService.updatePhoneInstance(phone, Integer.parseInt(changePhone.getAmountOfBuiltInMemory()),
                Integer.parseInt(changePhone.getAmountOfRam()), Integer.parseInt(changePhone.getPrice()));
        return changePhone(model,"Телефони успішно оновлено");
    }

    @GetMapping("/allphones")
    public String allPhones(Model model, @RequestParam(value = "success") String success) {
        List<PhoneInstance> phones = phoneInstanceService.findAllForAdmin();

        model.addAttribute(PHONES, phones);
        model.addAttribute(SUCCESS, success);
        return "allphones";
    }

    @PostMapping("/allphones")
    public String deleteFromAllPhones(Model model, @RequestParam(value = "id") String id) {
        phoneInstanceService.deleteByIdPhoneInstance(id);

        return allPhones(model,"Телефон успішно видалено");
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
        setOrder(model, success, true);
        return "adminorders";
    }

    @GetMapping("/cancellation-of-orders")
    public String getOrdersForCancellation(Model model, @RequestParam(value = "success") String success) {
        setOrder(model, success, true);
        return "adminorders";
    }

    @GetMapping("/orders-history")
    public String getOrdersHistory(Model model, @RequestParam(value = "success") String success) {
        setOrder(model, success, false);
        return "adminorders";
    }

    @PutMapping("/close-order")
    public String closeOrder(Model model, @RequestParam(value = "id") String id) {
        clientCheckService.updateCheckClosed(true, id);

        return getOrders(model, "Замовлення " + id + " успішно закрите");
    }

    @PutMapping("/cancel-order")
    public String cancelOrder(Model model, @RequestParam(value = "id") String id) {
        ClientCheck clientCheckFromDB = clientCheckService.findById(id).get();
        RegisteredUser registeredUser = userDetailsServiceImpl.findById(clientCheckService.getUserIdForCheckId(id));
        SimpleDateFormat formatter = new SimpleDateFormat(CHECK_DATES_PATTERN, Locale.ENGLISH);

        mailSender.sendMailPurchaseNotice(registeredUser.getEmailAddress(), "Ваше замовлення " + clientCheckFromDB.getId() + " було скасовано",
                new OrderInfoForMail(clientCheckFromDB, formatter.format(clientCheckFromDB.getCreated()),
                        phoneInstanceService.findPriceForClientCheckId(clientCheckFromDB.getId()), false, registeredUser));

        phoneInstanceService.cancelOrder(id);
        clientCheckService.cancelCheck(id);

        return getOrders(model, "Замовлення " + id + " успішно скасоване");
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
    public String deleteBrands(Model model, @RequestParam(value = "id") String id) {
        if (!brandService.delete(id)) {
            return customizeBrands(model, "Цей бренд не може бути видалений, оскільки він вже використовувався");
        }
        else {
            return customizeBrands(model, "Бренд успішно видалено");
        }
    }

    @PostMapping("/characteristics/brands/create")
    public String createBrands(Model model, NewBrand newBrand) {
        if (newBrand.getName().isBlank()) {
            return customizeBrands(model,"Поле Ім'я порожнє");
        }
        if (newBrand.getCountry().equals("Виберіть країну")) {
            return  customizeBrands(model,"Ви повинні вибрати країну");
        }

        Brand brand = new Brand();
        brand.setName(newBrand.getName());
        brand.setCountry(countryService.findCountryByName(newBrand.getCountry()).get());
        brand.setPhoneDescriptions(new ArrayList<>());
        if (!brandService.save(brand)) {
            return customizeBrands(model, "Цей бренд вже існує");
        }
        else {
            return customizeBrands(model, "Бренд успішно збережено");
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
    public String deleteChargeTypes(Model model, @RequestParam(value = "id") String id) {
        if (!chargeTypeService.delete(id)) {
            return customizeChargeTypes(model, "Цей тип зарядного пристрою не можна видалити, оскільки він вже використовувався");
        }
        else {
            return customizeChargeTypes(model, "Тип зарядного пристрою успішно видалено");
        }
    }

    @PostMapping("/characteristics/chargetypes/create")
    public String createChargeTypes(Model model, ChargeType newChargeType) {
        if (newChargeType.getName().isBlank()) {
            return customizeChargeTypes(model, "Поле Ім'я порожнє");
        }

        newChargeType.setPhoneDescriptions(new ArrayList<>());
        if (!chargeTypeService.save(newChargeType)) {
            return customizeChargeTypes(model, "Цей тип зарядного пристрою вже існує");
        }
        else {
            return customizeChargeTypes(model, "Тип зарядного пристрою успішно збережено");
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
    public String deleteCommunicationStandards(Model model, @RequestParam(value = "id") String id) {
        if (!communicationStandardService.delete(id)) {
            return customizeCommunicationStandards(model, "Цей стандарт зв'язку не може бути вилучений, оскільки він вже використовувався");
        }
        else {
            return customizeCommunicationStandards(model, "Стандарт зв'язку успішно видалено");
        }
    }

    @PostMapping("/characteristics/communicationstandards/create")
    public String createCommunicationStandards(Model model, CommunicationStandard newCommunicationStandard) {
        if (newCommunicationStandard.getName().isBlank()) {
            return customizeCommunicationStandards(model, "Поле Ім'я порожнє");
        }

        newCommunicationStandard.setPhoneDescriptions(new ArrayList<>());
        if (!communicationStandardService.save(newCommunicationStandard)) {
            return customizeCommunicationStandards(model, "Цей стандарт зв'язку вже існує");
        }
        else {
            return customizeCommunicationStandards(model, "Стандарт зв'язку успішно збережено");
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
    public String deleteOperationSystems(Model model, @RequestParam(value = "id") String id) {
        if (!operationSystemService.delete(id)) {
            return customizeOperationSystems(model, "Цю операційну систему не можна видалити, оскільки вона вже використовувалася");
        }
        else {
            return customizeOperationSystems(model, "Операційну систему успішно видалено");
        }
    }

    @PostMapping("/characteristics/operationsystems/create")
    public String createOperationSystems(Model model, OperationSystem newOperationSystem) {
        if (newOperationSystem.getName().isBlank()) {
            return customizeOperationSystems(model, "Поле Назва порожнє");
        }

        newOperationSystem.setPhoneDescriptions(new ArrayList<>());
        if (!operationSystemService.save(newOperationSystem)) {
            return customizeOperationSystems(model, "Ця операційна система вже існує");
        }
        else {
            return customizeOperationSystems(model, "Операційну систему успішно збережено");
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
    public String deleteTypeScreens(Model model, @RequestParam(value = "id") String id) {
        if (!typeScreenService.delete(id)) {
            return customizeTypeScreens(model, "Екран цього типу не можна видалити, оскільки він вже використовувався");
        }
        else {
            return customizeTypeScreens(model, "Екран цього типу успішно видалено");
        }
    }

    @PostMapping("/characteristics/typescreens/create")
    public String createTypeScreens(Model model, TypeScreen newTypeScreen) {
        if (newTypeScreen.getName().isBlank()) {
            return customizeTypeScreens(model, "Поле Ім'я порожнє");
        }

        newTypeScreen.setPhoneDescriptions(new ArrayList<>());
        if (!typeScreenService.save(newTypeScreen)) {
            return customizeTypeScreens(model, "Екран цього типу вже існує");
        }
        else {
            return customizeTypeScreens(model, "Екран цього типу успішно збережено");
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
    public String deleteProcessors(Model model, @RequestParam(value = "id") String id) {
        if (!processorService.delete(id)) {
            return customizeProcessors(model, "Цей процесор не можна видалити, оскільки він вже використовувався");
        }
        else {
            return customizeProcessors(model, "Процесор успішно видалено");
        }
    }

    @PostMapping("/characteristics/processors/create")
    public String createProcessors(Model model, Processor newProcessor) {
        if (newProcessor.getName().isBlank()) {
            return customizeProcessors(model, "Поле Ім'я порожнє");
        }
        if (newProcessor.getCoreFrequency() <= 0.0) {
            return customizeProcessors(model, "Неправильна частота ядра");
        }
        if (newProcessor.getNumberOfCores() == 0) {
            return customizeProcessors(model, "Ви повинні вибрати кількість ядер");
        }

        newProcessor.setPhoneDescriptions(new ArrayList<>());
        if (!processorService.save(newProcessor)) {
            return customizeProcessors(model, "Цей процесор вже існує");
        }
        else {
            return customizeProcessors(model, "Процесор успішно збережено");
        }
    }

    @GetMapping("/statistics")
    public String getStatistics() {
        return "statistics";
    }

    @GetMapping("/statistic-1")
    public String getFirstStatistic(Model model) {
        return modelAttributesForFirstStatisticAndEighth(model, "", true, false, "firststatistic");
    }

    @GetMapping("/statistic-1/show")
    public String getFirstStatisticShow(Model model, SalesSettingsForSpecificModels salesSettingsForSpecificModels) throws Exception {
        if (salesSettingsForSpecificModels.getId().equals("Виберіть телефон")) {
            return modelAttributesForFirstStatisticAndEighth(model, "Ви повинні вибрати телефон", true, false, "firststatistic");
        }
        if (salesSettingsForSpecificModels.getYear().equals("Виберіть рік")) {
            return modelAttributesForFirstStatisticAndEighth(model, "Ви повинні вибрати рік", true, false, "firststatistic");
        }

        if (!salesSettingsForSpecificModels.getYear().equals(THREE_YEARS)) {
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
            model.addAttribute(FOR_WHAT_YEAR, salesSettingsForSpecificModels.getYear());
            return modelAttributesForFirstStatisticAndEighth(model, "", false, false, "firststatistic");
        }
        else {
            List<TablesForFirstStatistic> tablesForFirstStatisticList = PhoneMapper.getListTablesForFirstStatistic(salesSettingsForSpecificModels, phoneInstanceService);
            List<String> monthNames = new ArrayList<>();
            for (int i = 1; i < 13; i++) {
                monthNames.add(Util.getMonth(i));
            }

            model.addAttribute("monthNames", monthNames);
            model.addAttribute("list", tablesForFirstStatisticList);
            model.addAttribute("forWhatPhone", tablesForFirstStatisticList.get(0).getPhone());
            model.addAttribute(FOR_WHAT_YEAR, THREE_YEARS);
            return modelAttributesForFirstStatisticAndEighth(model, "", false, true, "firststatistic");
        }
    }

    @GetMapping("/statistic-2")
    public String getSecondStatistic(Model model) {
        return modelAttributesForSecondStatistic(model, "", true);
    }

    @GetMapping("/statistic-2/show")
    public String getSecondStatisticShow(Model model, SalesSettingsForSpecificModels salesSettingsForSpecificModels) throws Exception {
        if (salesSettingsForSpecificModels.getYear().equals("Виберіть рік")) {
            return modelAttributesForSecondStatistic(model, "Ви повинні вибрати рік", true);
        }

        List<MostPopularPhoneModels> mostPopularPhoneModelsList = phoneInstanceService.getMostPopularPhoneModels(salesSettingsForSpecificModels.getYear());

        model.addAttribute("list", mostPopularPhoneModelsList);
        model.addAttribute(FOR_WHAT_YEAR, salesSettingsForSpecificModels.getYear());
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
        String startDate = LocalDate.now().getDayOfMonth() + "." + (LocalDate.now().getMonthValue() - 2) + "." + (LocalDate.now().getYear() - 1) + " 00:00:00";
        String endDate = LocalDate.now().getDayOfMonth() + "." + (LocalDate.now().getMonthValue() - 2) + "." + (LocalDate.now().getYear()) + " 23:59:59";
        List<String> years = new ArrayList<>();
        SimpleDateFormat formatterForDateAdded = new SimpleDateFormat(BIRTHDAY_PATTERN, Locale.ENGLISH);
        SimpleDateFormat formatter = new SimpleDateFormat(CHECK_DATES_PATTERN, Locale.ENGLISH);

        model.addAttribute(PHONE_FOR_STORE_COMPOSITIONS, phoneInstanceService.getPhonesWithBadSales(startDate,
                endDate, formatter, formatterForDateAdded, years, COUNT_PHONES_FOR_BAD_SALES));
        model.addAttribute(FLAG, false);
        model.addAttribute(FLAG_FOR_STATISTIC, true);
        model.addAttribute(YEARS, years);
        model.addAttribute("startDate", startDate.replace(" 00:00:00", ""));
        model.addAttribute("endDate", endDate.replace(" 23:59:59", ""));
        return "storecomposition";
    }

    @GetMapping("/statistic-8")
    public String getEighthStatistic(Model model) {
        return modelAttributesForFirstStatisticAndEighth(model, "", true, false, "eighthstatistic");
    }

    @GetMapping("/statistic-8/show")
    public String getEighthStatisticShow(Model model, SalesSettingsForSpecificModels salesSettingsForSpecificModels) throws Exception {
        if (salesSettingsForSpecificModels.getYear().equals("Виберіть рік")) {
            return modelAttributesForFirstStatisticAndEighth(model, "Ви повинні вибрати рік", true, false, "eighthstatistic");
        }

        if (!salesSettingsForSpecificModels.getYear().equals(THREE_YEARS)) {
            SalesSettingsForSpecificModelsParams salesSettingsForSpecificModelsParam = clientCheckService.getStoreEarningsByMonthAndYear(salesSettingsForSpecificModels);
            List<Object> chart = new ArrayList<>();
            AtomicInteger max = new AtomicInteger(-1);

            salesSettingsForSpecificModelsParam.getFields().forEach(field -> {
                chart.add(List.of(field.getMonth(), field.getSold() / 100));

                if (field.getSold() > max.get()) {
                    max.set(field.getSold());
                }
            });

            AtomicInteger sum = new AtomicInteger(0);
            salesSettingsForSpecificModelsParam.getFields().forEach(field -> sum.addAndGet(field.getSold()));

            model.addAttribute("chartData", chart);
            model.addAttribute("maxPrice", max.get() / 100);
            model.addAttribute("sum", sum.get() / 100);
            model.addAttribute(FOR_WHAT_YEAR, salesSettingsForSpecificModels.getYear());
            return modelAttributesForFirstStatisticAndEighth(model, "", false, false, "eighthstatistic");
        }
        else {
            TablesForFirstStatistic tablesForStatistic = OrderMapper.getListTablesForEighthStatistic(salesSettingsForSpecificModels, clientCheckService);
            List<String> monthNames = new ArrayList<>();
            for (int i = 1; i < 13; i++) {
                monthNames.add(Util.getMonth(i));
            }

            List<Integer> sum = new ArrayList<>();
            sum.add(0);
            sum.add(0);
            sum.add(0);
            tablesForStatistic.getTempYear().forEach(field -> sum.set(0, sum.get(0) + (field.getSold() / 100)));
            tablesForStatistic.getLastYear().forEach(field -> sum.set(1, sum.get(1) + (field.getSold() / 100)));
            tablesForStatistic.getYearBeforeLast().forEach(field -> sum.set(2, sum.get(2) + (field.getSold() / 100)));

            model.addAttribute("monthNames", monthNames);
            model.addAttribute("listOpt", tablesForStatistic);
            model.addAttribute(FOR_WHAT_YEAR, THREE_YEARS);
            model.addAttribute("sumList", sum);
            return modelAttributesForFirstStatisticAndEighth(model, "", false, true, "eighthstatistic");
        }
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
    public String getOrdersForUserId(@RequestParam(value = "id") String id, @RequestParam(value = "flag") boolean flag,
                                     @RequestParam(value = "flag-stat") boolean flagStat, Model model) {
        List<ClientCheck> checks = clientCheckService.findAllChecksForUserId(id);
        RegisteredUser registeredUser = userDetailsServiceImpl.findById(id);
        List<OrdersForSelectUserForAdmin> orders = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat(CHECK_DATES_PATTERN, Locale.ENGLISH);
        SimpleDateFormat formatterBirthday = new SimpleDateFormat(BIRTHDAY_PATTERN, Locale.ENGLISH);

        checks.forEach(check -> orders.add(Util.createOrderForSelectUserForAdmin(check, formatter, phoneInstanceService)));

        Collections.sort(orders);

        model.addAttribute(FLAG, flag);
        model.addAttribute(FLAG_FOR_STATISTIC, flagStat);
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
            return setFindPhoneByImei(model, null, "Поле IMEI телефону порожнє");
        }

        Optional<PhoneInstance> resultPhone = phoneInstanceService.findPhoneByImei(findPhoneByImei.getImei());

        return (resultPhone.isEmpty()) ? setFindPhoneByImei(model, null, "IMEI цього телефону не знайдено") :
                setFindPhoneByImei(model, resultPhone.get(), "");
    }

    @GetMapping("/find-registered-user-last-name")
    public String findUserByLastName(Model model) {
        return setFindUserByLastName(model, new ArrayList<>(), "");
    }

    @GetMapping("/find-registered-user-last-name-finding")
    public String findUserByLastNameFinding(Model model, FindUserByLastName findUserByLastName) {
        if (findUserByLastName.getLastName().isBlank()) {
            return setFindUserByLastName(model, new ArrayList<>(), "Поле Прізвище користувача порожнє");
        }

        List<RegisteredUser> users = userDetailsServiceImpl.findUserByLastName(findUserByLastName.getLastName());

        return (users.isEmpty()) ? setFindUserByLastName(model, new ArrayList<>(), "Користувача з таким прізвищем не знайдено") :
                setFindUserByLastName(model, users, "");
    }

    @PostMapping("/find-phone-by-imei-delete")
    public String deleteFromFindPhoneByImei(@RequestParam(value = "id") String id, Model model) {
        phoneInstanceService.deleteByIdPhoneInstance(id);

        return setFindPhoneByImei(model, null, "Телефон успішно видалено");
    }

    @GetMapping("/find-client-order-number")
    public String findOrderByNumber(Model model) {
        return setFindOrderByNumber(model, null, "");
    }

    @GetMapping("/find-client-order-number-finding")
    public String findOrderByNumberFinding(Model model, FindOrderByNumber findOrderByNumber) {
        if (findOrderByNumber.getNumber().isBlank()) {
            return setFindOrderByNumber(model, null, "Поле Номер замовлення порожнє");
        }

        Optional<ClientCheck> order = clientCheckService.findById(findOrderByNumber.getNumber());

        return (order.isEmpty()) ? setFindOrderByNumber(model, null, "Замовлення з таким номером не знайдено") :
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

    private void setOrder(Model model, String success, boolean flag) {
        List<UserOrdersForAdmin> orders = userDetailsServiceImpl.getUsersOrdersForAdmin(flag);
        orders.forEach(order -> order.setTotalPrices(phoneInstanceService.findPriceForClientCheckId(order.getCheck().getId())));
        Collections.sort(orders);

        SimpleDateFormat formatter = new SimpleDateFormat(CHECK_DATES_PATTERN, Locale.ENGLISH);
        orders.forEach(order -> order.setDates(formatter.format(order.getCheck().getCreated())));

        if (!flag) {
            orders.forEach(order -> order.setDatesClosed(formatter.format(order.getCheck().getClosedDate())));
        }

        model.addAttribute("orders", orders);
        model.addAttribute(SUCCESS, success);
        model.addAttribute(FLAG, flag);
    }

    private String modelAttributesForFirstStatisticAndEighth(Model model, String errorMsg, boolean flag, boolean flagTables,
                                                             String fileName) {
        List<String> years = Util.getListYears();
        years.add(THREE_YEARS);

        model.addAttribute(YEARS, years);
        model.addAttribute(PHONES, phoneInstanceService.findAllPhonesInDb());
        model.addAttribute("salesSettingsForSpecificModels", new SalesSettingsForSpecificModels());
        model.addAttribute(FLAG, flag);
        model.addAttribute("flagTables", flagTables);
        model.addAttribute(ERROR_MSG, errorMsg);
        return fileName;
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