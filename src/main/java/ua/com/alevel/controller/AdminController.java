package ua.com.alevel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.alevel.mapper.PhoneMapper;
import ua.com.alevel.model.accessory.*;
import ua.com.alevel.model.country.Country;
import ua.com.alevel.model.dto.CreatePhone;
import ua.com.alevel.model.dto.NewBrand;
import ua.com.alevel.model.dto.UserOrdersForAdmin;
import ua.com.alevel.model.phone.Phone;
import ua.com.alevel.service.brand.BrandService;
import ua.com.alevel.service.chargetype.ChargeTypeService;
import ua.com.alevel.service.clientcheck.ClientCheckService;
import ua.com.alevel.service.communicationstandard.CommunicationStandardService;
import ua.com.alevel.service.country.CountryService;
import ua.com.alevel.service.operationsystem.OperationSystemService;
import ua.com.alevel.service.phone.PhoneService;
import ua.com.alevel.service.processor.ProcessorService;
import ua.com.alevel.service.typescreen.TypeScreenService;
import ua.com.alevel.service.user.UserDetailsServiceImpl;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final PhoneService phoneService;
    private final BrandService brandService;
    private final ChargeTypeService chargeTypeService;
    private final CommunicationStandardService communicationStandardService;
    private final OperationSystemService operationSystemService;
    private final ProcessorService processorService;
    private final TypeScreenService typeScreenService;
    private final ClientCheckService clientCheckService;
    private final CountryService countryService;
    private static final String REGEX = "https://drive.google.com";
    private static final String LEFT_REGEX = "id=";
    private static final String RIGHT_REGEX = "&usp=drive_copy";

    public AdminController(UserDetailsServiceImpl userDetailsServiceImpl, PhoneService phoneService, BrandService brandService,
                           ChargeTypeService chargeTypeService, CommunicationStandardService communicationStandardService,
                           OperationSystemService operationSystemService, ProcessorService processorService,
                           TypeScreenService typeScreenService, ClientCheckService clientCheckService,
                           CountryService countryService) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.phoneService = phoneService;
        this.brandService = brandService;
        this.chargeTypeService = chargeTypeService;
        this.clientCheckService = clientCheckService;
        this.communicationStandardService = communicationStandardService;
        this.operationSystemService = operationSystemService;
        this.processorService = processorService;
        this.typeScreenService = typeScreenService;
        this.countryService = countryService;
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

    @PostMapping("/create")
    public String savePhone(Model model, CreatePhone phone) {
        if (phone.getBrand().equals("Select brand")) {
            return errorMsg(model, "You must choose a brand");
        }
        if (phone.getChargeType().equals("Select charge type")) {
            return errorMsg(model, "You must choose a charge type");
        }
        if (phone.getCommunicationStandard().equals("Select communication standard")) {
            return errorMsg(model, "You must choose a communication standard");
        }
        if (phone.getOperationSystem().equals("Select operation system")) {
            return errorMsg(model, "You must choose an operation system");
        }
        if (phone.getProcessor().equals("Select processor")) {
            return errorMsg(model, "You must choose a processor");
        }
        if (phone.getTypeScreen().equals("Select type screen")) {
            return errorMsg(model, "You must choose a type screen");
        }
        if (phone.getCountry().equals("Select country producer")) {
            return errorMsg(model, "You must choose a country producer");
        }
        if (phone.getName().isBlank()) {
            return errorMsg(model, "Name field is empty");
        }
        if (phone.getSeries().isBlank()) {
            return errorMsg(model, "Series field is empty");
        }
        if (phone.getDiagonal() <= 0.0) {
            return errorMsg(model, "Incorrect diagonal");
        }
        if (phone.getDisplayResolution().isBlank()) {
            return errorMsg(model, "Display resolution field is empty");
        }
        if (phone.getScreenRefreshRate() <= 0.0) {
            return errorMsg(model, "Incorrect screen refresh rate");
        }
        if (phone.getNumberOfSimCards() <= 0.0) {
            return errorMsg(model, "Incorrect number of sim cards");
        }
        if (phone.getAmountOfBuiltInMemory() <= 0.0) {
            return errorMsg(model, "Incorrect amount of built in memory");
        }
        if (phone.getAmountOfRam() <= 0.0) {
            return errorMsg(model, "Incorrect amount of RAM");
        }
        if (phone.getNumberOfFrontCameras() <= 0.0) {
            return errorMsg(model, "Incorrect number of front cameras");
        }
        if (phone.getInfoAboutFrontCameras().isBlank()) {
            return errorMsg(model, "Information about front cameras field is empty");
        }
        if (phone.getNumberOfMainCameras() <= 0.0) {
            return errorMsg(model, "Incorrect number of main cameras");
        }
        if (phone.getInfoAboutMainCameras().isBlank()) {
            return errorMsg(model, "Information about main cameras field is empty");
        }
        if (phone.getWeight() <= 0.0) {
            return errorMsg(model, "Incorrect weight");
        }
        if (phone.getHeight() <= 0.0) {
            return errorMsg(model, "Incorrect height");
        }
        if (phone.getWidth() <= 0.0) {
            return errorMsg(model, "Incorrect width");
        }
        if (phone.getDegreeOfMoistureProtection().isBlank()) {
            return errorMsg(model, "Degree of moisture protection field is empty");
        }
        if (phone.getColor().isBlank()) {
            return errorMsg(model, "Color field is empty");
        }
        if (phone.getGuaranteeTimeMonths() <= 0.0) {
            return errorMsg(model, "Incorrect guarantee time in months");
        }
        if (phone.getPhoneFrontAndBack().isBlank() || phone.getLeftSideAndRightSide().isBlank()
                || phone.getUpSideAndDownSide().isBlank()) {
            return errorMsg(model, "Address for phone picture field is empty");
        }
        if (phone.getImei().isBlank()) {
            return errorMsg(model, "IMEI field is empty");
        }
        if (phone.getPrice() <= 0.0) {
            return errorMsg(model, "Incorrect price");
        }
        if (phone.getCurrency().isBlank()) {
            return errorMsg(model, "Currency field is empty");
        }

        Brand brand = brandService.findBrandByName(phone.getBrand()).get();
        ChargeType chargeType = chargeTypeService.findFirstByName(phone.getChargeType()).get();
        CommunicationStandard communicationStandard = communicationStandardService.findFirstByName(phone.getCommunicationStandard()).get();
        OperationSystem operationSystem = operationSystemService.findFirstByName(phone.getOperationSystem()).get();
        Processor processor = processorService.findFirstByName(phone.getProcessor()).get();
        TypeScreen typeScreen = typeScreenService.findFirstByName(phone.getTypeScreen()).get();
        Country country = countryService.findCountryByName(phone.getCountry()).get();

        if (phone.getPhoneFrontAndBack().contains(REGEX)) {
            String[] linkParts = phone.getPhoneFrontAndBack().split(LEFT_REGEX);
            String pictureName = linkParts[1].replace(RIGHT_REGEX, "");
            phone.setPhoneFrontAndBack("http://drive.google.com/uc?export=view&id=" + pictureName);
        }
        if (phone.getLeftSideAndRightSide().contains(REGEX)) {
            String[] linkParts = phone.getLeftSideAndRightSide().split(LEFT_REGEX);
            String pictureName = linkParts[1].replace(RIGHT_REGEX, "");
            phone.setLeftSideAndRightSide("http://drive.google.com/uc?export=view&id=" + pictureName);
        }
        if (phone.getUpSideAndDownSide().contains(REGEX)) {
            String[] linkParts = phone.getUpSideAndDownSide().split(LEFT_REGEX);
            String pictureName = linkParts[1].replace(RIGHT_REGEX, "");
            phone.setUpSideAndDownSide("http://drive.google.com/uc?export=view&id=" + pictureName);
        }

        String[] imeis = phone.getImei().split(",");
        int check = -1;
        int value = 0;

        for (int i = 0; i < imeis.length; i++) {
            if (!imeis[i].isBlank()) {
                phone.setImei(imeis[i].trim());

                Phone phoneForDb = PhoneMapper.mapCreatePhoneToPhone(phone, brand, chargeType,
                        communicationStandard, operationSystem, processor, typeScreen, country);

                if (!phoneService.save(phoneForDb)) {
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
        CreatePhone phone = new CreatePhone();

        model.addAttribute("phone", phone);
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
        List<Phone> phones = phoneService.findAllForAdmin();

        model.addAttribute("phones", phones);
        model.addAttribute("success", success);
        return "deletephones";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam(value = "id") String id) {
        phoneService.delete(id);

        return "redirect:/admin/delete?success=The phone has been deleted successfully";
    }

    @GetMapping("/orders")
    public String getOrders(Model model, @RequestParam(value = "success") String success) {
        List<UserOrdersForAdmin> orders = userDetailsServiceImpl.getUsersOrdersForAdmin();

        model.addAttribute("orders", orders);
        model.addAttribute("success", success);
        return "adminorders";
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
        brand.setPhones(new ArrayList<>());
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

        newChargeType.setPhones(new ArrayList<>());
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

        newCommunicationStandard.setPhones(new ArrayList<>());
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

        newOperationSystem.setPhones(new ArrayList<>());
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

        newTypeScreen.setPhones(new ArrayList<>());
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

        newProcessor.setPhones(new ArrayList<>());
        if (!processorService.save(newProcessor)) {
            return "redirect:/admin/characteristics/processors?success=This processor already exists";
        }
        else {
            return "redirect:/admin/characteristics/processors?success=Processor successfully saved";
        }
    }
}