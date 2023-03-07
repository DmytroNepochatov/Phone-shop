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
import ua.com.alevel.util.Util;
import java.util.*;

@Controller
public class ProductsController {
    private final PhoneInstanceService phoneInstanceService;
    private final CommentService commentService;
    private final BrandService brandService;
    private static final String REDIRECT_PAGE_ONE = "redirect:/?page=1";
    private static final String PRODUCTS_PAGE = "products";
    private static final String NO_SORT = "No sort";
    private static final String SORT_BY_PRICE_ASCENDING = "Sort by price ascending";
    private static final String SORT_BY_PRICE_DESCENDING = "Sort by price descending";
    private static final String SLASH_SORT_BY_PRICE_ASCENDING = "/sort-by-price-ascending";
    private static final String SLASH_SORT_BY_PRICE_DESCENDING = "/sort-by-price-descending";
    private static final String SEARCH_KEYWORD = "/search?searchKeyword=";
    private static final String SORT_BY_PRICE_ASCENDING_SEARCH = "/sort-by-price-ascending-search?searchKeyword=";
    private static final String SORT_BY_PRICE_DESCENDING_SEARCH = "/sort-by-price-descending-search?searchKeyword=";
    private static final String PHONES = "phones";
    private static final String FILTER_SETTINGS = "filterSettings";
    private static final String LIST_SORTS = "listSorts";
    private static final String LIST_SORTS_LINKS = "listSortsLinks";
    private static final String SEARCH_PAGE = "searchPage";
    private static final String FILTRATION_PAGE = "filtrationPage";
    private static final String NO_SORT_FILTRATION = "/no-sort-filtration?params=";
    private static final String SORT_BY_PRICE_ASCENDING_FILTRATION = "/sort-by-price-ascending-filtration?params=";
    private static final String SORT_BY_PRICE_DESCENDING_FILTRATION = "/sort-by-price-descending-filtration?params=";

    public ProductsController(PhoneInstanceService phoneInstanceService,
                              CommentService commentService, BrandService brandService) {
        this.phoneInstanceService = phoneInstanceService;
        this.commentService = commentService;
        this.brandService = brandService;
    }

    @GetMapping("/")
    public String showMainPage(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
        return showMainPageRealization(model, page);
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

    @GetMapping("/sort-by-price-ascending-search")
    public String searchPhonesAsc(Model model, @RequestParam(value = "searchKeyword") String searchKeyword) {
        PhonesForMainViewList products = phoneInstanceService.findBySearchAsc(searchKeyword);

        List<String> listSorts = List.of(SORT_BY_PRICE_ASCENDING, NO_SORT, SORT_BY_PRICE_DESCENDING);
        List<String> listSortsLinks = List.of(SORT_BY_PRICE_ASCENDING_SEARCH + searchKeyword,
                SEARCH_KEYWORD + searchKeyword, SORT_BY_PRICE_DESCENDING_SEARCH + searchKeyword);

        return partForSearches(model, products, listSorts, listSortsLinks, searchKeyword);
    }

    @GetMapping("/sort-by-price-descending-search")
    public String searchPhonesDesc(Model model, @RequestParam(value = "searchKeyword") String searchKeyword) {
        PhonesForMainViewList products = phoneInstanceService.findBySearchDesc(searchKeyword);

        List<String> listSorts = List.of(SORT_BY_PRICE_DESCENDING, NO_SORT, SORT_BY_PRICE_ASCENDING);
        List<String> listSortsLinks = List.of(SORT_BY_PRICE_DESCENDING_SEARCH + searchKeyword,
                SEARCH_KEYWORD + searchKeyword, SORT_BY_PRICE_ASCENDING_SEARCH + searchKeyword);

        return partForSearches(model, products, listSorts, listSortsLinks, searchKeyword);
    }

    @GetMapping("/sort-by-price-ascending")
    public String sortByPriceAscending(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
        return sortByPriceAscendingRealization(model, page);
    }

    @GetMapping("/sort-by-price-descending")
    public String sortByPriceDescending(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
        return sortByPriceDescendingRealization(model, page);
    }

    @PostMapping("/filtration")
    public String getFilter(Model model, @ModelAttribute(value = "filterSettings") FilterSettings filterSettings) {
        List<String> params = Util.filterParams(filterSettings, new ArrayList<>());

        if (params.isEmpty()) {
            return REDIRECT_PAGE_ONE;
        }
        else {
            return getFilterRealization(model, filterSettings, params);
        }
    }

    @GetMapping("/no-sort-filtration")
    public String getFilterNoSort(Model model, String params) {
        String[] paramsArr = params.split(",");
        FilterSettings filterSettings = FilterMapper.mapParamsToFilterSettings(phoneInstanceService);
        Util.filterSettingsFilling(filterSettings, paramsArr);

        return getFilter(model, filterSettings);
    }

    @GetMapping("/sort-by-price-ascending-filtration")
    public String getFilterAsc(Model model, String params) {
        String[] paramsArr = params.split(",");
        FilterSettings filterSettings = FilterMapper.mapParamsToFilterSettings(phoneInstanceService);
        Util.filterSettingsFilling(filterSettings, paramsArr);

        PhonesForMainViewList products = phoneInstanceService.filterPhonesAsc(paramsArr);

        List<String> listSorts = List.of(SORT_BY_PRICE_ASCENDING, NO_SORT, SORT_BY_PRICE_DESCENDING);
        List<String> listSortsLinks = List.of(SORT_BY_PRICE_ASCENDING_FILTRATION + params,
                NO_SORT_FILTRATION + params, SORT_BY_PRICE_DESCENDING_FILTRATION + params);

        addToAttributesForFilters(model, products, filterSettings, listSorts, listSortsLinks);
        return PRODUCTS_PAGE;
    }

    @GetMapping("/sort-by-price-descending-filtration")
    public String getFilterDesc(Model model, String params) {
        String[] paramsArr = params.split(",");
        FilterSettings filterSettings = FilterMapper.mapParamsToFilterSettings(phoneInstanceService);
        Util.filterSettingsFilling(filterSettings, paramsArr);

        PhonesForMainViewList products = phoneInstanceService.filterPhonesDesc(paramsArr);

        List<String> listSorts = List.of(SORT_BY_PRICE_DESCENDING, NO_SORT, SORT_BY_PRICE_ASCENDING);
        List<String> listSortsLinks = List.of(SORT_BY_PRICE_DESCENDING_FILTRATION + params,
                NO_SORT_FILTRATION + params, SORT_BY_PRICE_ASCENDING_FILTRATION + params);

        addToAttributesForFilters(model, products, filterSettings, listSorts, listSortsLinks);
        return PRODUCTS_PAGE;
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
        model.addAttribute(PHONES, products.getPhonesForMainView());
        model.addAttribute("pages", pages);
        model.addAttribute("pageForWhat", pageForWhat);
        model.addAttribute(FILTER_SETTINGS, filterSettings);
        model.addAttribute(LIST_SORTS, listSorts);
        model.addAttribute(LIST_SORTS_LINKS, listSortsLinks);
        model.addAttribute(SEARCH_PAGE, false);
        model.addAttribute(FILTRATION_PAGE, false);
    }

    private void addToAttributesForFilters(Model model, PhonesForMainViewList products, FilterSettings filterSettings,
                                           List<String> listSorts, List<String> listSortsLinks) {
        model.addAttribute(PHONES, products.getPhonesForMainView());
        model.addAttribute(FILTER_SETTINGS, filterSettings);
        model.addAttribute(LIST_SORTS, listSorts);
        model.addAttribute(LIST_SORTS_LINKS, listSortsLinks);
        model.addAttribute(SEARCH_PAGE, false);
        model.addAttribute(FILTRATION_PAGE, true);
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
        List<String> listSorts = List.of(NO_SORT, SORT_BY_PRICE_ASCENDING, SORT_BY_PRICE_DESCENDING);
        List<String> listSortsLinks = List.of("/", SLASH_SORT_BY_PRICE_ASCENDING, SLASH_SORT_BY_PRICE_DESCENDING);

        addToAttributes(model, products, pages, pageForWhat, filterSettings, listSorts, listSortsLinks);
        return PRODUCTS_PAGE;
    }

    private String searchPhonesRealization(Model model, String searchKeyword) {
        PhonesForMainViewList products = phoneInstanceService.findBySearch(searchKeyword);

        List<String> listSorts = List.of(NO_SORT, SORT_BY_PRICE_ASCENDING, SORT_BY_PRICE_DESCENDING);
        List<String> listSortsLinks = List.of(SEARCH_KEYWORD + searchKeyword,
                SORT_BY_PRICE_ASCENDING_SEARCH + searchKeyword,
                SORT_BY_PRICE_DESCENDING_SEARCH + searchKeyword);

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

        model.addAttribute(PHONES, products.getPhonesForMainView());
        model.addAttribute(FILTER_SETTINGS, filterSettings);
        model.addAttribute(LIST_SORTS, listSorts);
        model.addAttribute(LIST_SORTS_LINKS, listSortsLinks);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute(SEARCH_PAGE, true);
        model.addAttribute(FILTRATION_PAGE, false);
        return PRODUCTS_PAGE;
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
        List<String> listSorts = List.of(SORT_BY_PRICE_ASCENDING, NO_SORT, SORT_BY_PRICE_DESCENDING);
        List<String> listSortsLinks = List.of(SLASH_SORT_BY_PRICE_ASCENDING, "/", SLASH_SORT_BY_PRICE_DESCENDING);

        addToAttributes(model, products, pages, pageForWhat, filterSettings, listSorts, listSortsLinks);
        return PRODUCTS_PAGE;
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
        List<String> listSorts = List.of(SORT_BY_PRICE_DESCENDING, NO_SORT, SORT_BY_PRICE_ASCENDING);
        List<String> listSortsLinks = List.of(SLASH_SORT_BY_PRICE_DESCENDING, "/", SLASH_SORT_BY_PRICE_ASCENDING);

        addToAttributes(model, products, pages, pageForWhat, filterSettings, listSorts, listSortsLinks);
        return PRODUCTS_PAGE;
    }

    private String getFilterRealization(Model model, FilterSettings filterSettings, List<String> params) {
        PhonesForMainViewList products = phoneInstanceService.filterPhones(params.toArray(new String[0]));

        StringBuilder paramsStr = new StringBuilder();
        params.forEach(param -> paramsStr.append(param).append(","));

        List<String> listSorts = List.of(NO_SORT, SORT_BY_PRICE_ASCENDING, SORT_BY_PRICE_DESCENDING);
        List<String> listSortsLinks = List.of(NO_SORT_FILTRATION + paramsStr,
                SORT_BY_PRICE_ASCENDING_FILTRATION + paramsStr,
                SORT_BY_PRICE_DESCENDING_FILTRATION + paramsStr);

        addToAttributesForFilters(model, products, filterSettings, listSorts, listSortsLinks);
        return PRODUCTS_PAGE;
    }
}