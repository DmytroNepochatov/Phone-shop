package ua.com.alevel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.alevel.mapper.FilterMapper;
import ua.com.alevel.mapper.PhoneMapper;
import ua.com.alevel.model.accessory.Brand;
import ua.com.alevel.model.dto.*;
import ua.com.alevel.model.dto.filterparams.*;
import ua.com.alevel.service.brand.BrandService;
import ua.com.alevel.service.comment.CommentService;
import ua.com.alevel.service.phone.PhoneService;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Controller
public class ProductsController {
    private final PhoneService phoneService;
    private final CommentService commentService;
    private final BrandService brandService;

    public ProductsController(PhoneService phoneService, CommentService commentService, BrandService brandService) {
        this.phoneService = phoneService;
        this.commentService = commentService;
        this.brandService = brandService;
    }

    @GetMapping("/")
    public String showMainPage(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
        return showMainPageRealization(model, page);
    }

    private String showMainPageRealization(Model model, int page) {
        PhonesForMainViewList products = phoneService.findAllForMainView(page);

        checkList(products);

        List<Integer> pages = new ArrayList<>(products.getPages());

        for (int i = 0; i < products.getPages(); i++) {
            pages.add(i + 1);
        }

        String pageForWhat = "/?page=";
        FilterSettings filterSettings = FilterMapper.mapParamsToFilterSettings(phoneService);
        List<String> listSorts = List.of("No sort", "Sort by price ascending", "Sort by price descending");
        List<String> listSortsLinks = List.of("/", "/sort-by-price-ascending", "/sort-by-price-descending");

        model.addAttribute("phones", products.getPhonesForMainView());
        model.addAttribute("pages", pages);
        model.addAttribute("pageForWhat", pageForWhat);
        model.addAttribute("filterSettings", filterSettings);
        model.addAttribute("listSorts", listSorts);
        model.addAttribute("listSortsLinks", listSortsLinks);
        return "products";
    }

    @GetMapping("/search")
    public String searchPhones(Model model, @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "searchKeyword") String searchKeyword) {
        if (searchKeyword.isBlank()) {
            return "redirect:/?page=1";
        }
        else {
            return searchPhonesRealization(model, page, searchKeyword);
        }
    }

    private String searchPhonesRealization(Model model, int page, String searchKeyword) {
        String regex = "[0-9]";
        String regex2 = "[a-zA-Z]";

        if (Pattern.compile(regex).matcher(searchKeyword).find() && Pattern.compile(regex2).matcher(searchKeyword).find()) {
            String name = searchKeyword.replaceAll(regex2, "").trim();
            String brandAndSeries = searchKeyword.replaceAll(regex, "").trim();

            String[] brandAndSeriesArray = brandAndSeries.split(" ", 2);
            if (brandAndSeriesArray.length == 1) {
                return "redirect:/?page=1";
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
                return "redirect:/?page=1";
            }

            String[] params = new String[]{idBrand, brandAndSeriesArray[1].replaceAll("  ", " "), name};
            PhonesForMainViewList products = phoneService.findBySearch(page, params);

            List<Integer> pages = new ArrayList<>(products.getPages());

            for (int i = 0; i < products.getPages(); i++) {
                pages.add(i + 1);
            }

            String pageForWhat = "/search?page=";
            FilterSettings filterSettings = FilterMapper.mapParamsToFilterSettings(phoneService);
            List<String> listSorts = List.of("No sort", "Sort by price ascending", "Sort by price descending");
            List<String> listSortsLinks = List.of("/", "/sort-by-price-ascending", "/sort-by-price-descending");

            model.addAttribute("phones", products.getPhonesForMainView());
            model.addAttribute("pages", pages);
            model.addAttribute("pageForWhat", pageForWhat);
            model.addAttribute("filterSettings", filterSettings);
            model.addAttribute("listSorts", listSorts);
            model.addAttribute("listSortsLinks", listSortsLinks);
            model.addAttribute("searchKeyword", searchKeyword);
            return "products";
        }
        else {
            return "redirect:/?page=1";
        }
    }

    @GetMapping("/sort-by-price-ascending")
    public String sortByPriceAscending(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
        return sortByPriceAscendingRealization(model, page);
    }

    private String sortByPriceAscendingRealization(Model model, int page) {
        PhonesForMainViewList products = phoneService.sortByPriceAsc(page);

        checkList(products);

        List<Integer> pages = new ArrayList<>(products.getPages());

        for (int i = 0; i < products.getPages(); i++) {
            pages.add(i + 1);
        }

        String pageForWhat = "/sort-by-price-ascending?page=";
        FilterSettings filterSettings = FilterMapper.mapParamsToFilterSettings(phoneService);
        List<String> listSorts = List.of("Sort by price ascending", "No sort", "Sort by price descending");
        List<String> listSortsLinks = List.of("/sort-by-price-ascending", "/", "/sort-by-price-descending");

        model.addAttribute("phones", products.getPhonesForMainView());
        model.addAttribute("pages", pages);
        model.addAttribute("pageForWhat", pageForWhat);
        model.addAttribute("filterSettings", filterSettings);
        model.addAttribute("listSorts", listSorts);
        model.addAttribute("listSortsLinks", listSortsLinks);
        return "products";
    }

    @GetMapping("/sort-by-price-descending")
    public String sortByPriceDescending(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
        return sortByPriceDescendingRealization(model, page);
    }

    private String sortByPriceDescendingRealization(Model model, int page) {
        PhonesForMainViewList products = phoneService.sortByPriceDesc(page);

        checkList(products);

        List<Integer> pages = new ArrayList<>(products.getPages());

        for (int i = 0; i < products.getPages(); i++) {
            pages.add(i + 1);
        }

        String pageForWhat = "/sort-by-price-descending?page=";
        FilterSettings filterSettings = FilterMapper.mapParamsToFilterSettings(phoneService);
        List<String> listSorts = List.of("Sort by price descending", "No sort", "Sort by price ascending");
        List<String> listSortsLinks = List.of("/sort-by-price-descending", "/", "/sort-by-price-ascending");

        model.addAttribute("phones", products.getPhonesForMainView());
        model.addAttribute("pages", pages);
        model.addAttribute("pageForWhat", pageForWhat);
        model.addAttribute("filterSettings", filterSettings);
        model.addAttribute("listSorts", listSorts);
        model.addAttribute("listSortsLinks", listSortsLinks);
        return "products";
    }

    @PostMapping("/filtration")
    public String getFilter(Model model, @RequestParam(value = "page", defaultValue = "1") int page,
                            @ModelAttribute(value = "filterSettings") FilterSettings filterSettings) {
        List<String> params = filterParams(filterSettings, new ArrayList<>());

        if (params.isEmpty()) {
            return "redirect:/?page=1";
        }
        else {
            return getFilterRealization(model, page, filterSettings, params);
        }
    }

    private String getFilterRealization(Model model, int page, FilterSettings filterSettings, List<String> params) {
        PhonesForMainViewList products = phoneService.filterPhones(page, params.toArray(new String[0]));

        List<Integer> pages = new ArrayList<>(products.getPages());

        for (int i = 0; i < products.getPages(); i++) {
            pages.add(i + 1);
        }

        String pageForWhat = "/filtration?page=";
        String pageForFilter = "&filterSettings=";
        List<String> listSorts = List.of("No sort", "Sort by price ascending", "Sort by price descending");
        List<String> listSortsLinks = List.of("/", "/sort-by-price-ascending", "/sort-by-price-descending");

        model.addAttribute("phones", products.getPhonesForMainView());
        model.addAttribute("pages", pages);
        model.addAttribute("pageForWhat", pageForWhat);
        model.addAttribute("pageForFilter", pageForFilter);
        model.addAttribute("filterSettings", filterSettings);
        model.addAttribute("listSorts", listSorts);
        model.addAttribute("listSortsLinks", listSortsLinks);
        return "products";
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

    @GetMapping("/fullinfo")
    public String getFullInfo(Model model, @RequestParam(value = "page") int page,
                              @RequestParam(value = "brand") String brand,
                              @RequestParam(value = "name") String name,
                              @RequestParam(value = "series") String series,
                              @RequestParam(value = "amountOfBuiltInMemory") int amountOfBuiltInMemory,
                              @RequestParam(value = "amountOfRam") int amountOfRam,
                              @RequestParam(value = "successAddToCart") String successAddToCart) {

        PhoneForMainView phoneForMainView = PhoneMapper.mapParamsToPhoneForMainView(brandService.findBrandByName(brand).get(),
                name, series, amountOfBuiltInMemory, amountOfRam);

        FullInfoAboutPhone fullInfo = phoneService.getFullInfoAboutPhone(phoneForMainView);
        PhoneForAddToCart phoneForAddToCart = new PhoneForAddToCart(brand, name, series, amountOfBuiltInMemory,
                amountOfRam, fullInfo.getPhoneColors());
        CommentForPhoneList comments = commentService.findCommentsForPhone(phoneForMainView, page);

        List<Integer> pages = new ArrayList<>(comments.getCommentsCount());

        for (int i = 0; i < comments.getCommentsCount(); i++) {
            pages.add(i + 1);
        }

        String pageForWhat = "/fullinfo?page=";
        String pageForWhatPhone = "&brand=" + brand + "&name=" + name + "&series="
                + series + "&amountOfBuiltInMemory=" + amountOfBuiltInMemory + "&amountOfRam=" + amountOfRam;

        float rating = (float) Math.ceil(((float) fullInfo.getPhone().getRating().getTotalPoints() / fullInfo.getPhone().getRating().getNumberOfPoints()) * Math.pow(10, 1)) / (float) Math.pow(10, 1);
        List<String> photos = new ArrayList<>();
        phoneForAddToCart.getPhoneColors().forEach(phone -> {
            photos.add(phone.getPhoneFrontAndBack());
            photos.add(phone.getLeftSideAndRightSide());
            photos.add(phone.getUpSideAndDownSide());
        });

        model.addAttribute("photos", photos);
        model.addAttribute("rating", rating);
        model.addAttribute("fullInfo", fullInfo.getPhone());
        model.addAttribute("phoneForAddToCart", phoneForAddToCart);
        model.addAttribute("pages", pages);
        model.addAttribute("pageForWhat", pageForWhat);
        model.addAttribute("pageForWhatPhone", pageForWhatPhone);
        model.addAttribute("comments", comments.getCommentForPhoneList());
        model.addAttribute("successAddToCart", successAddToCart);
        return "fullinfo";
    }

    private void checkList(PhonesForMainViewList products) {
        if (products.getPhonesForMainView().isEmpty()) {
            throw new RuntimeException();
        }
    }
}