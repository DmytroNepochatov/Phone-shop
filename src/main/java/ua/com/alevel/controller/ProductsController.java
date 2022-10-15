package ua.com.alevel.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.alevel.model.accessory.Brand;
import ua.com.alevel.model.dto.*;
import ua.com.alevel.service.brand.BrandService;
import ua.com.alevel.service.comment.CommentService;
import ua.com.alevel.service.phone.PhoneService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/")
public class ProductsController {
    private final PhoneService phoneService;
    private final CommentService commentService;
    private final BrandService brandService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductsController.class);

    public ProductsController(PhoneService phoneService, CommentService commentService, BrandService brandService) {
        this.phoneService = phoneService;
        this.commentService = commentService;
        this.brandService = brandService;
    }

    @GetMapping
    public String showMainPage(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        if (page == 0) {
            return showMainPageRealization(model, 1);
        }
        else {
            return showMainPageRealization(model, page);
        }
    }

    private String showMainPageRealization(Model model, int page) {
        try {
            Object[] products = phoneService.findAllForMainView(page);

            List<PhoneForMainView> phones = (List<PhoneForMainView>) products[0];
            if (phones.isEmpty()) {
                throw new Exception();
            }

            List<Integer> pages = new ArrayList<>((int) products[1]);

            for (int i = 0; i < (int) products[1]; i++) {
                pages.add(i + 1);
            }

            String pageForWhat = "/?page=";
            FilterSettings filterSettings = createFilterSettings();
            List<String> listSorts = List.of("No sort", "Sort by price ascending", "Sort by price descending");
            List<String> listSortsLinks = List.of("/", "/sort-by-price-ascending", "/sort-by-price-descending");

            model.addAttribute("phones", phones);
            model.addAttribute("pages", pages);
            model.addAttribute("pageForWhat", pageForWhat);
            model.addAttribute("filterSettings", filterSettings);
            model.addAttribute("listSorts", listSorts);
            model.addAttribute("listSortsLinks", listSortsLinks);
            return "products";
        }
        catch (Exception e) {
            LOGGER.warn("Page {} not found", page);
            return "error";
        }
    }

    @GetMapping("/search")
    public String searchPhones(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "searchKeyword") String searchKeyword) {
        if (page == 0) {
            if (searchKeyword.isBlank()) {
                return showMainPage(model, 1);
            }
            else {
                return searchPhonesRealization(model, 1, searchKeyword);
            }
        }
        else {
            return searchPhonesRealization(model, page, searchKeyword);
        }
    }

    private String searchPhonesRealization(Model model, int page, String searchKeyword) {
        try {
            String regex = "[0-9]";
            String regex2 = "[a-zA-Z]";

            if (Pattern.compile(regex).matcher(searchKeyword).find() && Pattern.compile(regex2).matcher(searchKeyword).find()) {
                String name = searchKeyword.replaceAll(regex2, "").trim();
                String brandAndSeries = searchKeyword.replaceAll(regex, "").trim();

                String[] brandAndSeriesArray = brandAndSeries.split(" ", 2);
                if (brandAndSeriesArray.length == 1) {
                    return showMainPage(model, 1);
                }
                else {
                    brandAndSeriesArray[1] = brandAndSeriesArray[1].trim();
                }

                String idBrand;
                Brand brand = brandService.findBrandByName(brandAndSeriesArray[0]).get();
                if (brand != null) {
                    idBrand = brand.getId();
                }
                else {
                    return showMainPage(model, 1);
                }

                String[] params = new String[]{idBrand, brandAndSeriesArray[1].replaceAll("  ", " "), name};
                Object[] products = phoneService.findBySearch(page, params);

                List<PhoneForMainView> phones = (List<PhoneForMainView>) products[0];
                List<Integer> pages = new ArrayList<>((int) products[1]);

                for (int i = 0; i < (int) products[1]; i++) {
                    pages.add(i + 1);
                }

                String pageForWhat = "/search?page=";
                FilterSettings filterSettings = createFilterSettings();
                List<String> listSorts = List.of("No sort", "Sort by price ascending", "Sort by price descending");
                List<String> listSortsLinks = List.of("/", "/sort-by-price-ascending", "/sort-by-price-descending");

                model.addAttribute("phones", phones);
                model.addAttribute("pages", pages);
                model.addAttribute("pageForWhat", pageForWhat);
                model.addAttribute("filterSettings", filterSettings);
                model.addAttribute("listSorts", listSorts);
                model.addAttribute("listSortsLinks", listSortsLinks);
                model.addAttribute("searchKeyword", searchKeyword);
                return "products";
            }
            else {
                return showMainPage(model, 1);
            }
        }
        catch (Exception e) {
            LOGGER.warn("Page {} not found", page);
            return "error";
        }
    }

    @GetMapping("/sort-by-price-ascending")
    public String sortByPriceAscending(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        if (page == 0) {
            return sortByPriceAscendingRealization(model, 1);
        }
        else {
            return sortByPriceAscendingRealization(model, page);
        }
    }

    private String sortByPriceAscendingRealization(Model model, int page) {
        try {
            Object[] products = phoneService.sortByPriceAsc(page);

            List<PhoneForMainView> phones = (List<PhoneForMainView>) products[0];
            if (phones.isEmpty()) {
                throw new Exception();
            }

            List<Integer> pages = new ArrayList<>((int) products[1]);

            for (int i = 0; i < (int) products[1]; i++) {
                pages.add(i + 1);
            }

            String pageForWhat = "/sort-by-price-ascending?page=";
            FilterSettings filterSettings = createFilterSettings();
            List<String> listSorts = List.of("Sort by price ascending", "No sort", "Sort by price descending");
            List<String> listSortsLinks = List.of("/sort-by-price-ascending", "/", "/sort-by-price-descending");

            model.addAttribute("phones", phones);
            model.addAttribute("pages", pages);
            model.addAttribute("pageForWhat", pageForWhat);
            model.addAttribute("filterSettings", filterSettings);
            model.addAttribute("listSorts", listSorts);
            model.addAttribute("listSortsLinks", listSortsLinks);
            return "products";
        }
        catch (Exception e) {
            LOGGER.warn("Page {} not found", page);
            return "error";
        }
    }

    @GetMapping("/sort-by-price-descending")
    public String sortByPriceDescending(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        if (page == 0) {
            return sortByPriceDescendingRealization(model, 1);
        }
        else {
            return sortByPriceDescendingRealization(model, page);
        }
    }

    private String sortByPriceDescendingRealization(Model model, int page) {
        try {
            Object[] products = phoneService.sortByPriceDesc(page);

            List<PhoneForMainView> phones = (List<PhoneForMainView>) products[0];
            if (phones.isEmpty()) {
                throw new Exception();
            }

            List<Integer> pages = new ArrayList<>((int) products[1]);

            for (int i = 0; i < (int) products[1]; i++) {
                pages.add(i + 1);
            }

            String pageForWhat = "/sort-by-price-descending?page=";
            FilterSettings filterSettings = createFilterSettings();
            List<String> listSorts = List.of("Sort by price descending", "No sort", "Sort by price ascending");
            List<String> listSortsLinks = List.of("/sort-by-price-descending", "/", "/sort-by-price-ascending");

            model.addAttribute("phones", phones);
            model.addAttribute("pages", pages);
            model.addAttribute("pageForWhat", pageForWhat);
            model.addAttribute("filterSettings", filterSettings);
            model.addAttribute("listSorts", listSorts);
            model.addAttribute("listSortsLinks", listSortsLinks);
            return "products";
        }
        catch (Exception e) {
            LOGGER.warn("Page {} not found", page);
            return "error";
        }
    }

    @GetMapping("/filter")
    public String getFilter(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "filterSettings") FilterSettings filterSettings) {
        if (page == 0) {
            AtomicInteger check = new AtomicInteger(0);

            if (checkFilterCondition(filterSettings, check).get() == 0) {
                return showMainPage(model, 1);
            }
            else {
                return getFilterRealization(model, 1, filterSettings);
            }
        }
        else {
            return getFilterRealization(model, page, filterSettings);
        }
    }

    private String getFilterRealization(Model model, int page, FilterSettings filterSettings) {
        try {
            List<String> params = new ArrayList<>();
            filterParams(filterSettings, params);
            Object[] products = phoneService.filterPhones(page, params.toArray(new String[0]));

            List<PhoneForMainView> phones = (List<PhoneForMainView>) products[0];
            List<Integer> pages = new ArrayList<>((int) products[1]);

            for (int i = 0; i < (int) products[1]; i++) {
                pages.add(i + 1);
            }

            String pageForWhat = "/filter?page=";
            String pageForFilter = "&filterSettings=";
            List<String> listSorts = List.of("No sort", "Sort by price ascending", "Sort by price descending",
                    "Sort by rating ascending", "Sort by rating descending");
            List<String> listSortsLinks = List.of("/", "/sort-by-price-ascending", "/sort-by-price-descending",
                    "/sort-by-rating-ascending", "/sort-by-rating-descending");

            model.addAttribute("phones", phones);
            model.addAttribute("pages", pages);
            model.addAttribute("pageForWhat", pageForWhat);
            model.addAttribute("pageForFilter", pageForFilter);
            model.addAttribute("filterSettings", filterSettings);
            model.addAttribute("listSorts", listSorts);
            model.addAttribute("listSortsLinks", listSortsLinks);
            return "products";
        }
        catch (Exception e) {
            LOGGER.warn("Page {} not found", page);
            return "error";
        }
    }

    private List<String> filterParams(FilterSettings filterSettings, List<String> params) {
        filterSettings.getBrandForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone.brand_id=" + element.getId());
            }
        });

        filterSettings.getChargeTypeForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone.charge_type_id=" + element.getId());
            }
        });

        filterSettings.getCommunicationStandardForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone.communication_standard_id=" + element.getId());
            }
        });

        filterSettings.getOperationSystemForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone.operation_system_id=" + element.getId());
            }
        });

        filterSettings.getProcessorForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone.processor_id=" + element.getId());
            }
        });

        filterSettings.getTypeScreenForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone.type_screen_id=" + element.getId());
            }
        });

        filterSettings.getDiagonalForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone.diagonal=" + element.getDiagonal());
            }
        });

        filterSettings.getDisplayResolutionForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone.display_resolution=" + element.getDisplayResolution());
            }
        });

        filterSettings.getScreenRefreshRateForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone.screen_refresh_rate=" + element.getRefreshRate());
            }
        });

        filterSettings.getNumberOfSimCardForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone.number_of_sim_cards=" + element.getNumberOfSimCards());
            }
        });

        filterSettings.getAmountOfBuiltInMemoryForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone.amount_of_built_in_memory=" + element.getAmountOfBuiltInMemory());
            }
        });

        filterSettings.getAmountOfRamForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone.amount_of_ram=" + element.getAmountOfRam());
            }
        });

        filterSettings.getNumberOfFrontCameraForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone.number_of_front_cameras=" + element.getNumberOfFrontCameras());
            }
        });

        filterSettings.getNumberOfMainCameraForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone.number_of_main_cameras=" + element.getNumberOfMainCameras());
            }
        });

        filterSettings.getDegreeOfMoistureProtectionForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone.degree_of_moisture_protection=" + element.getDegreeOfMoistureProtection());
            }
        });

        filterSettings.getNfcForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone.is_have_nfc=" + element.getIsHaveNfc());
            }
        });

        return params;
    }

    private AtomicInteger checkFilterCondition(FilterSettings filterSettings, AtomicInteger check) {
        filterSettings.getBrandForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                check.addAndGet(1);
            }
        });

        filterSettings.getChargeTypeForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                check.addAndGet(1);
            }
        });

        filterSettings.getCommunicationStandardForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                check.addAndGet(1);
            }
        });

        filterSettings.getOperationSystemForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                check.addAndGet(1);
            }
        });

        filterSettings.getProcessorForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                check.addAndGet(1);
            }
        });

        filterSettings.getTypeScreenForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                check.addAndGet(1);
            }
        });

        filterSettings.getDiagonalForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                check.addAndGet(1);
            }
        });

        filterSettings.getDisplayResolutionForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                check.addAndGet(1);
            }
        });

        filterSettings.getScreenRefreshRateForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                check.addAndGet(1);
            }
        });

        filterSettings.getNumberOfSimCardForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                check.addAndGet(1);
            }
        });

        filterSettings.getAmountOfBuiltInMemoryForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                check.addAndGet(1);
            }
        });

        filterSettings.getAmountOfRamForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                check.addAndGet(1);
            }
        });

        filterSettings.getNumberOfFrontCameraForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                check.addAndGet(1);
            }
        });

        filterSettings.getNumberOfMainCameraForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                check.addAndGet(1);
            }
        });

        filterSettings.getDegreeOfMoistureProtectionForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                check.addAndGet(1);
            }
        });

        filterSettings.getNfcForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                check.addAndGet(1);
            }
        });

        return check;
    }

    private FilterSettings createFilterSettings() {
        List<BrandForMainView> brandForMainViewList = phoneService.findAllAvailableBrand();
        List<ChargeTypeForMainView> chargeTypeForMainViewList = phoneService.findAllAvailableChargeTypes();
        List<CommunicationStandardForMainView> communicationStandardForMainViewList = phoneService.findAllAvailableCommunicationStandards();
        List<OperationSystemForMainView> operationSystemForMainViewList = phoneService.findAllAvailableOperationSystems();
        List<ProcessorForMainView> processorForMainViewList = phoneService.findAllAvailableProcessors();
        List<TypeScreenForMainView> typeScreenForMainViewList = phoneService.findAllAvailableTypeScreens();
        List<DiagonalForMainView> diagonalForMainViewList = phoneService.findAllAvailableDiagonals();
        List<DisplayResolutionForMainView> displayResolutionForMainViewList = phoneService.findAllAvailableDisplayResolutions();
        List<ScreenRefreshRateForMainView> screenRefreshRateForMainViewList = phoneService.findAllAvailableScreenRefreshRates();
        List<NumberOfSimCardForMainView> numberOfSimCardForMainViewList = phoneService.findAllAvailableNumberOfSimCards();
        List<AmountOfBuiltInMemoryForMainView> amountOfBuiltInMemoryForMainViewList = phoneService.findAllAvailableAmountOfBuiltInMemory();
        List<AmountOfRamForMainView> amountOfRamForMainViewList = phoneService.findAllAvailableAmountOfRam();
        List<NumberOfFrontCameraForMainView> numberOfFrontCameraForMainViewList = phoneService.findAllAvailableNumberOfFrontCameras();
        List<NumberOfMainCameraForMainView> numberOfMainCameraForMainViewList = phoneService.findAllAvailableNumberOfMainCameras();
        List<DegreeOfMoistureProtectionForMainView> degreeOfMoistureProtectionForMainViewList = phoneService.findAllAvailableDegreeOfMoistureProtection();
        List<NfcForMainView> nfcForMainViewList = phoneService.getNfcTypes();

        return new FilterSettings(brandForMainViewList, chargeTypeForMainViewList, communicationStandardForMainViewList,
                operationSystemForMainViewList, processorForMainViewList, typeScreenForMainViewList, diagonalForMainViewList,
                displayResolutionForMainViewList, screenRefreshRateForMainViewList, numberOfSimCardForMainViewList,
                amountOfBuiltInMemoryForMainViewList, amountOfRamForMainViewList, numberOfFrontCameraForMainViewList,
                numberOfMainCameraForMainViewList, degreeOfMoistureProtectionForMainViewList, nfcForMainViewList);
    }
}
