package ua.com.alevel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.alevel.model.accessory.*;
import ua.com.alevel.model.dto.CreatePhone;
import ua.com.alevel.model.dto.UserOrdersForAdmin;
import ua.com.alevel.model.phone.Phone;
import ua.com.alevel.service.brand.BrandService;
import ua.com.alevel.service.chargetype.ChargeTypeService;
import ua.com.alevel.service.clientcheck.ClientCheckService;
import ua.com.alevel.service.communicationstandard.CommunicationStandardService;
import ua.com.alevel.service.operationsystem.OperationSystemService;
import ua.com.alevel.service.phone.PhoneService;
import ua.com.alevel.service.processor.ProcessorService;
import ua.com.alevel.service.typescreen.TypeScreenService;
import ua.com.alevel.service.user.UserDetailsServiceImpl;
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

    public AdminController(UserDetailsServiceImpl userDetailsServiceImpl, PhoneService phoneService, BrandService brandService,
                           ChargeTypeService chargeTypeService, CommunicationStandardService communicationStandardService,
                           OperationSystemService operationSystemService, ProcessorService processorService,
                           TypeScreenService typeScreenService, ClientCheckService clientCheckService) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.phoneService = phoneService;
        this.brandService = brandService;
        this.chargeTypeService = chargeTypeService;
        this.clientCheckService = clientCheckService;
        this.communicationStandardService = communicationStandardService;
        this.operationSystemService = operationSystemService;
        this.processorService = processorService;
        this.typeScreenService = typeScreenService;
    }

    @GetMapping("/profile")
    public String getProfile() {
        return "adminprofile";
    }

    @GetMapping("/create")
    public String createPhone(Model model) {
        CreatePhone phone = new CreatePhone();

        model.addAttribute("phone", phone);
        model.addAttribute("errorMsg", " ");
        return "createphone";
    }

    @PostMapping("/create")
    public String savePhone(Model model, CreatePhone phone) {
        Brand brand = brandService.findBrandByName(phone.getBrand()).get();
        if (brand == null) {
            return errorMsg(model, phone, "Brand not found");
        }
        ChargeType chargeType = chargeTypeService.findFirstByName(phone.getChargeType()).get();
        if (chargeType == null) {
            return errorMsg(model, phone, "Charge type not found");
        }
        CommunicationStandard communicationStandard = communicationStandardService.findFirstByName(phone.getCommunicationStandard()).get();
        if (communicationStandard == null) {
            return errorMsg(model, phone, "Communication standard not found");
        }
        OperationSystem operationSystem = operationSystemService.findFirstByName(phone.getOperationSystem()).get();
        if (operationSystem == null) {
            return errorMsg(model, phone, "Operation system not found");
        }
        Processor processor = processorService.findFirstByName(phone.getProcessor()).get();
        if (processor == null) {
            return errorMsg(model, phone, "Operation system not found");
        }
        TypeScreen typeScreen = typeScreenService.findFirstByName(phone.getTypeScreen()).get();
        if (typeScreen == null) {
            return errorMsg(model, phone, "Type screen not found");
        }
        if (phone.getName().isBlank()) {
            return errorMsg(model, phone, "Name field is empty");
        }
        if (phone.getSeries().isBlank()) {
            return errorMsg(model, phone, "Series field is empty");
        }
        if (phone.getDiagonal() <= 0.0) {
            return errorMsg(model, phone, "Incorrect diagonal");
        }
        if (phone.getDisplayResolution().isBlank()) {
            return errorMsg(model, phone, "Display resolution field is empty");
        }
        if (phone.getScreenRefreshRate() <= 0.0) {
            return errorMsg(model, phone, "Incorrect screen refresh rate");
        }
        if (phone.getNumberOfSimCards() <= 0.0) {
            return errorMsg(model, phone, "Incorrect number of sim cards");
        }
        if (phone.getAmountOfBuiltInMemory() <= 0.0) {
            return errorMsg(model, phone, "Incorrect amount of built in memory");
        }
        if (phone.getAmountOfRam() <= 0.0) {
            return errorMsg(model, phone, "Incorrect amount of RAM");
        }
        if (phone.getNumberOfFrontCameras() <= 0.0) {
            return errorMsg(model, phone, "Incorrect number of front cameras");
        }
        if (phone.getInfoAboutFrontCameras().isBlank()) {
            return errorMsg(model, phone, "Information about front cameras field is empty");
        }
        if (phone.getNumberOfMainCameras() <= 0.0) {
            return errorMsg(model, phone, "Incorrect number of main cameras");
        }
        if (phone.getInfoAboutMainCameras().isBlank()) {
            return errorMsg(model, phone, "Information about main cameras field is empty");
        }
        if (phone.getWeight() <= 0.0) {
            return errorMsg(model, phone, "Incorrect weight");
        }
        if (phone.getHeight() <= 0.0) {
            return errorMsg(model, phone, "Incorrect height");
        }
        if (phone.getWidth() <= 0.0) {
            return errorMsg(model, phone, "Incorrect width");
        }
        if (phone.getDegreeOfMoistureProtection().isBlank()) {
            return errorMsg(model, phone, "Degree of moisture protection field is empty");
        }
        if (phone.getColor().isBlank()) {
            return errorMsg(model, phone, "Color field is empty");
        }
        if (phone.getGuaranteeTimeMonths() <= 0.0) {
            return errorMsg(model, phone, "Incorrect guarantee time in months");
        }
        if (phone.getCountryProducerOfTheProduct().isBlank()) {
            return errorMsg(model, phone, "Country producer of the product field is empty");
        }
        if (phone.getPhoneFrontAndBack().isBlank() || phone.getLeftSideAndRightSide().isBlank()
                || phone.getUpSideAndDownSide().isBlank()) {
            return errorMsg(model, phone, "Address for phone picture field is empty");
        }
        if (phone.getImei().isBlank()) {
            return errorMsg(model, phone, "IMEI field is empty");
        }
        if (phone.getPrice() <= 0.0) {
            return errorMsg(model, phone, "Incorrect price");
        }
        if (phone.getCurrency().isBlank()) {
            return errorMsg(model, phone, "Currency field is empty");
        }

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

        if (!phoneService.save(phoneForDb)) {
            return errorMsg(model, phone, "This IMEI already exists");
        }
        else {
            return errorMsg(model, phone, "Phone saved successfully");
        }
    }

    private String errorMsg(Model model, CreatePhone phone, String errorMsg) {
        phone = new CreatePhone();

        model.addAttribute("phone", phone);
        model.addAttribute("errorMsg", errorMsg);
        return "createphone";
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
}
