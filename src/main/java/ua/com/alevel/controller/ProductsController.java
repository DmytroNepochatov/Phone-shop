package ua.com.alevel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.alevel.mapper.FilterMapper;
import ua.com.alevel.mapper.PhoneMapper;
import ua.com.alevel.model.dto.*;
import ua.com.alevel.model.dto.filterparams.*;
import ua.com.alevel.service.brand.BrandService;
import ua.com.alevel.service.comment.CommentService;
import ua.com.alevel.service.phone.PhoneInstanceService;
import java.util.*;
import static org.apache.commons.lang.NumberUtils.isNumber;

@Controller
public class ProductsController {
    private final PhoneInstanceService phoneInstanceService;
    private final CommentService commentService;
    private final BrandService brandService;
    private static final String REDIRECT_PAGE_ONE = "redirect:/?page=1";
    private static final String PRODUCTS_PAGE = "products";

    public ProductsController(PhoneInstanceService phoneInstanceService, CommentService commentService, BrandService brandService) {
        this.phoneInstanceService = phoneInstanceService;
        this.commentService = commentService;
        this.brandService = brandService;
    }

    @GetMapping("/")
    public String showMainPage(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
        return showMainPageRealization(model, page);
    }

    private String showMainPageRealization(Model model, int page) {
        PhonesForMainViewList products = phoneInstanceService.findAllForMainView(page);
        checkList(products);
        List<Integer> pages = new ArrayList<>(products.getPages());

        for (int i = 0; i < products.getPages(); i++) {
            pages.add(i + 1);
        }

        String pageForWhat = "/?page=";
        FilterSettings filterSettings = FilterMapper.mapParamsToFilterSettings(phoneInstanceService);
        List<String> listSorts = List.of("No sort", "Sort by price ascending", "Sort by price descending");
        List<String> listSortsLinks = List.of("/", "/sort-by-price-ascending", "/sort-by-price-descending");

        addToAttributes(model, products, pages, pageForWhat, filterSettings, listSorts, listSortsLinks);
        return PRODUCTS_PAGE;
    }

    @GetMapping("/search")
    public String searchPhones(Model model, @RequestParam(value = "searchKeyword") String searchKeyword) {
        if (searchKeyword.isBlank()) {
            return REDIRECT_PAGE_ONE;
        }
        else {
            return searchPhonesRealization(model, searchKeyword);
        }
    }

    private String searchPhonesRealization(Model model, String searchKeyword) {
        PhonesForMainViewList products = phoneInstanceService.findBySearch(searchKeyword);

        List<String> listSorts = List.of("No sort", "Sort by price ascending", "Sort by price descending");
        List<String> listSortsLinks = List.of("/search?searchKeyword=" + searchKeyword,
                "/sort-by-price-ascending-search?searchKeyword=" + searchKeyword,
                "/sort-by-price-descending-search?searchKeyword=" + searchKeyword);

        return partForSearches(model, products, listSorts, listSortsLinks, searchKeyword);
    }

    @GetMapping("/sort-by-price-ascending-search")
    public String searchPhonesAsc(Model model, @RequestParam(value = "searchKeyword") String searchKeyword) {
        PhonesForMainViewList products = phoneInstanceService.findBySearchAsc(searchKeyword);

        List<String> listSorts = List.of("Sort by price ascending", "No sort", "Sort by price descending");
        List<String> listSortsLinks = List.of("/sort-by-price-ascending-search?searchKeyword=" + searchKeyword,
                "/search?searchKeyword=" + searchKeyword, "/sort-by-price-descending-search?searchKeyword=" + searchKeyword);

        return partForSearches(model, products, listSorts, listSortsLinks, searchKeyword);
    }

    @GetMapping("/sort-by-price-descending-search")
    public String searchPhonesDesc(Model model, @RequestParam(value = "searchKeyword") String searchKeyword) {
        PhonesForMainViewList products = phoneInstanceService.findBySearchDesc(searchKeyword);

        List<String> listSorts = List.of("Sort by price descending", "No sort", "Sort by price ascending");
        List<String> listSortsLinks = List.of("/sort-by-price-descending-search?searchKeyword=" + searchKeyword,
                "/search?searchKeyword=" + searchKeyword, "/sort-by-price-ascending-search?searchKeyword=" + searchKeyword);

        return partForSearches(model, products, listSorts, listSortsLinks, searchKeyword);
    }

    private String partForSearches(Model model, PhonesForMainViewList products, List<String> listSorts,
                                   List<String> listSortsLinks, String searchKeyword) {
        FilterSettings filterSettings = FilterMapper.mapParamsToFilterSettings(phoneInstanceService);
        List<String> brands = new ArrayList<>();
        products.getPhonesForMainView().forEach(phone -> brands.add(phone.getBrand()));
        Set<String> uniqueBrands = new LinkedHashSet<>(brands);

        for (String uniqueBrand : uniqueBrands) {
            for (BrandForMainView brandForMainView : filterSettings.getBrandForMainViewList()) {
                if (brandForMainView.getName().equals(uniqueBrand)) {
                    brandForMainView.setEnabled(true);
                }
            }
        }

        model.addAttribute("phones", products.getPhonesForMainView());
        model.addAttribute("filterSettings", filterSettings);
        model.addAttribute("listSorts", listSorts);
        model.addAttribute("listSortsLinks", listSortsLinks);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("searchPage", true);
        model.addAttribute("filtrationPage", false);
        return PRODUCTS_PAGE;
    }

    @GetMapping("/sort-by-price-ascending")
    public String sortByPriceAscending(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
        return sortByPriceAscendingRealization(model, page);
    }

    private String sortByPriceAscendingRealization(Model model, int page) {
        PhonesForMainViewList products = phoneInstanceService.sortByPriceAsc(page);
        checkList(products);
        List<Integer> pages = new ArrayList<>(products.getPages());

        for (int i = 0; i < products.getPages(); i++) {
            pages.add(i + 1);
        }

        String pageForWhat = "/sort-by-price-ascending?page=";
        FilterSettings filterSettings = FilterMapper.mapParamsToFilterSettings(phoneInstanceService);
        List<String> listSorts = List.of("Sort by price ascending", "No sort", "Sort by price descending");
        List<String> listSortsLinks = List.of("/sort-by-price-ascending", "/", "/sort-by-price-descending");

        addToAttributes(model, products, pages, pageForWhat, filterSettings, listSorts, listSortsLinks);
        return PRODUCTS_PAGE;
    }

    @GetMapping("/sort-by-price-descending")
    public String sortByPriceDescending(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
        return sortByPriceDescendingRealization(model, page);
    }

    private String sortByPriceDescendingRealization(Model model, int page) {
        PhonesForMainViewList products = phoneInstanceService.sortByPriceDesc(page);
        checkList(products);
        List<Integer> pages = new ArrayList<>(products.getPages());

        for (int i = 0; i < products.getPages(); i++) {
            pages.add(i + 1);
        }

        String pageForWhat = "/sort-by-price-descending?page=";
        FilterSettings filterSettings = FilterMapper.mapParamsToFilterSettings(phoneInstanceService);
        List<String> listSorts = List.of("Sort by price descending", "No sort", "Sort by price ascending");
        List<String> listSortsLinks = List.of("/sort-by-price-descending", "/", "/sort-by-price-ascending");

        addToAttributes(model, products, pages, pageForWhat, filterSettings, listSorts, listSortsLinks);
        return PRODUCTS_PAGE;
    }

    @PostMapping("/filtration")
    public String getFilter(Model model, @ModelAttribute(value = "filterSettings") FilterSettings filterSettings) {
        List<String> params = filterParams(filterSettings, new ArrayList<>());

        if (params.isEmpty()) {
            return REDIRECT_PAGE_ONE;
        }
        else {
            return getFilterRealization(model, filterSettings, params);
        }
    }

    private String getFilterRealization(Model model, FilterSettings filterSettings, List<String> params) {
        PhonesForMainViewList products = phoneInstanceService.filterPhones(params.toArray(new String[0]));

        StringBuilder paramsStr = new StringBuilder();
        params.forEach(param -> paramsStr.append(param).append(","));

        List<String> listSorts = List.of("No sort", "Sort by price ascending", "Sort by price descending");
        List<String> listSortsLinks = List.of("/no-sort-filtration?params=" + paramsStr,
                "/sort-by-price-ascending-filtration?params=" + paramsStr,
                "/sort-by-price-descending-filtration?params=" + paramsStr);

        addToAttributesForFilters(model, products, filterSettings, listSorts, listSortsLinks);
        return PRODUCTS_PAGE;
    }

    @GetMapping("/no-sort-filtration")
    public String getFilterNoSort(Model model, String params) {
        String[] paramsArr = params.split(",");
        FilterSettings filterSettings = FilterMapper.mapParamsToFilterSettings(phoneInstanceService);
        filterSettingsFilling(filterSettings, paramsArr);

        return getFilter(model, filterSettings);
    }

    @GetMapping("/sort-by-price-ascending-filtration")
    public String getFilterAsc(Model model, String params) {
        String[] paramsArr = params.split(",");
        FilterSettings filterSettings = FilterMapper.mapParamsToFilterSettings(phoneInstanceService);
        filterSettingsFilling(filterSettings, paramsArr);

        PhonesForMainViewList products = phoneInstanceService.filterPhonesAsc(paramsArr);

        List<String> listSorts = List.of("Sort by price ascending", "No sort", "Sort by price descending");
        List<String> listSortsLinks = List.of("/sort-by-price-ascending-filtration?params=" + params,
                "/no-sort-filtration?params=" + params, "/sort-by-price-descending-filtration?params=" + params);

        addToAttributesForFilters(model, products, filterSettings, listSorts, listSortsLinks);
        return PRODUCTS_PAGE;
    }

    @GetMapping("/sort-by-price-descending-filtration")
    public String getFilterDesc(Model model, String params) {
        String[] paramsArr = params.split(",");
        FilterSettings filterSettings = FilterMapper.mapParamsToFilterSettings(phoneInstanceService);
        filterSettingsFilling(filterSettings, paramsArr);

        PhonesForMainViewList products = phoneInstanceService.filterPhonesDesc(paramsArr);

        List<String> listSorts = List.of("Sort by price descending", "No sort", "Sort by price ascending");
        List<String> listSortsLinks = List.of("/sort-by-price-descending-filtration?params=" + params,
                "/no-sort-filtration?params=" + params, "/sort-by-price-ascending-filtration?params=" + params);

        addToAttributesForFilters(model, products, filterSettings, listSorts, listSortsLinks);
        return PRODUCTS_PAGE;
    }

    private void filterSettingsFilling(FilterSettings filterSettings, String[] params) {
        for (int i = 0; i < params.length; i++) {
            String[] fields = params[i].split("=");
            filterSettings.getBrandForMainViewList().forEach(element -> {
                if (element.getId().equals(fields[1])) {
                    element.setEnabled(true);
                }
            });

            filterSettings.getChargeTypeForMainViewList().forEach(element -> {
                if (element.getId().equals(fields[1])) {
                    element.setEnabled(true);
                }
            });

            filterSettings.getCommunicationStandardForMainViewList().forEach(element -> {
                if (element.getId().equals(fields[1])) {
                    element.setEnabled(true);
                }
            });

            filterSettings.getOperationSystemForMainViewList().forEach(element -> {
                if (element.getId().equals(fields[1])) {
                    element.setEnabled(true);
                }
            });

            filterSettings.getProcessorForMainViewList().forEach(element -> {
                if (element.getId().equals(fields[1])) {
                    element.setEnabled(true);
                }
            });

            filterSettings.getTypeScreenForMainViewList().forEach(element -> {
                if (element.getId().equals(fields[1])) {
                    element.setEnabled(true);
                }
            });

            filterSettings.getDisplayResolutionForMainViewList().forEach(element -> {
                if (element.getDisplayResolution().equals(fields[1])) {
                    element.setEnabled(true);
                }
            });

            filterSettings.getDegreeOfMoistureProtectionForMainViewList().forEach(element -> {
                if (element.getDegreeOfMoistureProtection().equals(fields[1])) {
                    element.setEnabled(true);
                }
            });

            filterSettings.getNfcForMainViewList().forEach(element -> {
                if (element.getIsHaveNfc().equals(fields[1])) {
                    element.setEnabled(true);
                }
            });

            if (isNumber(fields[1])) {

                filterSettings.getDiagonalForMainViewList().forEach(element -> {
                    if (element.getDiagonal() == Float.parseFloat(fields[1])) {
                        element.setEnabled(true);
                    }
                });


                filterSettings.getScreenRefreshRateForMainViewList().forEach(element -> {
                    if (element.getRefreshRate() == Integer.parseInt(fields[1])) {
                        element.setEnabled(true);
                    }
                });

                filterSettings.getNumberOfSimCardForMainViewList().forEach(element -> {
                    if (element.getNumberOfSimCards() == Integer.parseInt(fields[1])) {
                        element.setEnabled(true);
                    }
                });

                filterSettings.getAmountOfBuiltInMemoryForMainViewList().forEach(element -> {
                    if (element.getAmountOfBuiltInMemory() == Integer.parseInt(fields[1])) {
                        element.setEnabled(true);
                    }
                });

                filterSettings.getAmountOfRamForMainViewList().forEach(element -> {
                    if (element.getAmountOfRam() == Integer.parseInt(fields[1])) {
                        element.setEnabled(true);
                    }
                });

                filterSettings.getNumberOfFrontCameraForMainViewList().forEach(element -> {
                    if (element.getNumberOfFrontCameras() == Integer.parseInt(fields[1])) {
                        element.setEnabled(true);
                    }
                });

                filterSettings.getNumberOfMainCameraForMainViewList().forEach(element -> {
                    if (element.getNumberOfMainCameras() == Integer.parseInt(fields[1])) {
                        element.setEnabled(true);
                    }
                });
            }
        }
    }

    private List<String> filterParams(FilterSettings filterSettings, List<String> params) {
        filterSettings.getBrandForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.brand_id=" + element.getId());
            }
        });

        filterSettings.getChargeTypeForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.charge_type_id=" + element.getId());
            }
        });

        filterSettings.getCommunicationStandardForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.communication_standard_id=" + element.getId());
            }
        });

        filterSettings.getOperationSystemForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.operation_system_id=" + element.getId());
            }
        });

        filterSettings.getProcessorForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.processor_id=" + element.getId());
            }
        });

        filterSettings.getTypeScreenForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.type_screen_id=" + element.getId());
            }
        });

        filterSettings.getDiagonalForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.diagonal=" + element.getDiagonal());
            }
        });

        filterSettings.getDisplayResolutionForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.display_resolution=" + element.getDisplayResolution());
            }
        });

        filterSettings.getScreenRefreshRateForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.screen_refresh_rate=" + element.getRefreshRate());
            }
        });

        filterSettings.getNumberOfSimCardForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.number_of_sim_cards=" + element.getNumberOfSimCards());
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
                params.add("phone_description.number_of_front_cameras=" + element.getNumberOfFrontCameras());
            }
        });

        filterSettings.getNumberOfMainCameraForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.number_of_main_cameras=" + element.getNumberOfMainCameras());
            }
        });

        filterSettings.getDegreeOfMoistureProtectionForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.degree_of_moisture_protection=" + element.getDegreeOfMoistureProtection());
            }
        });

        filterSettings.getNfcForMainViewList().forEach(element -> {
            if (element.isEnabled()) {
                params.add("phone_description.is_have_nfc=" + element.getIsHaveNfc());
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

        FullInfoAboutPhone fullInfo = phoneInstanceService.getFullInfoAboutPhone(phoneForMainView);
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

        float rating = (float) Math.ceil(((float) fullInfo.getPhoneInstance().getPhone().getRating().getTotalPoints() / fullInfo.getPhoneInstance().getPhone().getRating().getNumberOfPoints()) * Math.pow(10, 1)) / (float) Math.pow(10, 1);
        List<String> photos = new ArrayList<>();
        phoneForAddToCart.getPhoneColors().forEach(phone -> {
            photos.add(phone.getPhoneFrontAndBack());
            photos.add(phone.getLeftSideAndRightSide());
            photos.add(phone.getUpSideAndDownSide());
        });

        model.addAttribute("photos", photos);
        model.addAttribute("rating", rating);
        model.addAttribute("fullInfo", fullInfo.getPhoneInstance());
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

    private void addToAttributes(Model model, PhonesForMainViewList products, List<Integer> pages,
                                 String pageForWhat, FilterSettings filterSettings, List<String> listSorts,
                                 List<String> listSortsLinks) {
        model.addAttribute("phones", products.getPhonesForMainView());
        model.addAttribute("pages", pages);
        model.addAttribute("pageForWhat", pageForWhat);
        model.addAttribute("filterSettings", filterSettings);
        model.addAttribute("listSorts", listSorts);
        model.addAttribute("listSortsLinks", listSortsLinks);
        model.addAttribute("searchPage", false);
        model.addAttribute("filtrationPage", false);
    }

    private void addToAttributesForFilters(Model model, PhonesForMainViewList products, FilterSettings filterSettings,
                                           List<String> listSorts, List<String> listSortsLinks) {
        model.addAttribute("phones", products.getPhonesForMainView());
        model.addAttribute("filterSettings", filterSettings);
        model.addAttribute("listSorts", listSorts);
        model.addAttribute("listSortsLinks", listSortsLinks);
        model.addAttribute("searchPage", false);
        model.addAttribute("filtrationPage", true);
    }
}