package ua.com.alevel.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.model.dto.PhoneColors;
import ua.com.alevel.model.dto.PhoneForAddToCart;
import ua.com.alevel.model.dto.PhoneForShoppingCart;
import ua.com.alevel.model.phone.Phone;
import ua.com.alevel.model.shoppingcart.ShoppingCart;
import ua.com.alevel.model.user.RegisteredUser;
import ua.com.alevel.service.clientcheck.ClientCheckService;
import ua.com.alevel.service.phone.PhoneService;
import ua.com.alevel.service.shoppingcart.ShoppingCartService;
import ua.com.alevel.service.user.UserDetailsServiceImpl;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/shopping-cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final PhoneService phoneService;
    private final ClientCheckService clientCheckService;

    public ShoppingCartController(ShoppingCartService shoppingCartService, PhoneService phoneService,
                                  UserDetailsServiceImpl userDetailsServiceImpl, ClientCheckService clientCheckService) {
        this.shoppingCartService = shoppingCartService;
        this.phoneService = phoneService;
        this.clientCheckService = clientCheckService;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @GetMapping
    public String getShoppingCart(Model model) {
        ShoppingCart shoppingCart = userDetailsServiceImpl.findShoppingCartForUserEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        List<PhoneForShoppingCart> phones = phoneService.findAllPhoneForShoppingCartId(shoppingCart.getId());
        String deletePhone = "/shopping-cart/delete-from-cart?phoneId=";

        model.addAttribute("totalPrice", shoppingCart.getPrice());
        model.addAttribute("phones", phones);
        model.addAttribute("deletePhone", deletePhone);
        return "shoppingcart";
    }

    @PutMapping("/add-to-cart")
    public String addToCart(PhoneForAddToCart phoneForAddToCart) {
        int checks = checkColorsCount(phoneForAddToCart);

        if (checks == 0) {
            return "redirect:/fullinfo?page=1&brand=" + phoneForAddToCart.getBrand() + "&name=" + phoneForAddToCart.getName() + "&series=" + phoneForAddToCart.getSeries() + "&amountOfBuiltInMemory=" + phoneForAddToCart.getAmountOfBuiltInMemory() + "&amountOfRam=" + phoneForAddToCart.getAmountOfRam() +
                    "&successAddToCart=You must choose the color of the phone";
        }
        if (checks >= 2) {
            return "redirect:/fullinfo?page=1&brand=" + phoneForAddToCart.getBrand() + "&name=" + phoneForAddToCart.getName() + "&series=" + phoneForAddToCart.getSeries() + "&amountOfBuiltInMemory=" + phoneForAddToCart.getAmountOfBuiltInMemory() + "&amountOfRam=" + phoneForAddToCart.getAmountOfRam() +
                    "&successAddToCart=You can only choose one phone color";
        }

        String color = findColor(phoneForAddToCart);

        String phoneId = phoneService.findFirstIdPhoneForShoppingCart(phoneForAddToCart.getBrand(), phoneForAddToCart.getName(),
                phoneForAddToCart.getSeries(), phoneForAddToCart.getAmountOfBuiltInMemory(), phoneForAddToCart.getAmountOfRam(),
                color).get(0);

        if (phoneId == null) {
            return "redirect:/fullinfo?page=1&brand=" + phoneForAddToCart.getBrand() + "&name=" + phoneForAddToCart.getName() + "&series=" + phoneForAddToCart.getSeries() + "&amountOfBuiltInMemory=" + phoneForAddToCart.getAmountOfBuiltInMemory() + "&amountOfRam=" + phoneForAddToCart.getAmountOfRam() +
                    "&successAddToCart=Sorry, but this type of phone is out of stock";
        }

        ShoppingCart shoppingCart = userDetailsServiceImpl.findShoppingCartForUserEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        phoneService.setShoppingCartForPhone(shoppingCart, phoneId);
        double priceForPhone = phoneService.findPriceForPhoneId(phoneId);
        double oldPriceShoppingCart = shoppingCartService.findPriceForShoppingCartId(shoppingCart.getId());
        shoppingCartService.save(shoppingCart.getId(), priceForPhone + oldPriceShoppingCart);

        return "redirect:/fullinfo?page=1&brand=" + phoneForAddToCart.getBrand() + "&name=" + phoneForAddToCart.getName() + "&series=" + phoneForAddToCart.getSeries() + "&amountOfBuiltInMemory=" + phoneForAddToCart.getAmountOfBuiltInMemory() + "&amountOfRam=" + phoneForAddToCart.getAmountOfRam() +
                "&successAddToCart=The phone has been successfully added to the shopping cart";
    }

    @PutMapping("/delete-from-cart")
    public String deleteFromShoppingCart(@RequestParam(value = "phoneId") String phoneId) {
        ShoppingCart shoppingCart = userDetailsServiceImpl.findShoppingCartForUserEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        delete(phoneId, shoppingCart);
        return "redirect:/shopping-cart";
    }

    @PutMapping("/create-order")
    public String createOrder(Model model) {
        ShoppingCart shoppingCart = userDetailsServiceImpl.findShoppingCartForUserEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        List<Phone> phones = phoneService.findAllPhonesForShoppingCartId(shoppingCart.getId());
        RegisteredUser registeredUser = userDetailsServiceImpl.findUserByEmailAddress(SecurityContextHolder.getContext().getAuthentication().getName());
        Date date = new Date();

        ClientCheck clientCheck = new ClientCheck();
        clientCheck.setRegisteredUser(registeredUser);
        clientCheck.setCreated(date);
        clientCheck.setTotalPrice(shoppingCart.getPrice());
        clientCheck.setPhones(phones);
        clientCheck.setClosed(false);
        clientCheckService.save(clientCheck);
        ClientCheck clientCheckFromDB = clientCheckService.findClientCheckForUserIdForNewOrder(registeredUser.getId(), date).get();

        phones.forEach(phone -> phoneService.addPhoneToClientCheck(clientCheckFromDB, phone.getId()));
        phones.forEach(phone -> delete(phone.getId(), shoppingCart));

        model.addAttribute("clientCheck", clientCheckFromDB.getId());
        return "order";
    }

    private void delete(String phoneId, ShoppingCart shoppingCart) {
        double priceForPhone = phoneService.findPriceForPhoneId(phoneId);
        double oldPriceShoppingCart = shoppingCartService.findPriceForShoppingCartId(shoppingCart.getId());
        shoppingCartService.save(shoppingCart.getId(), oldPriceShoppingCart - priceForPhone);
        phoneService.delShoppingCartForPhone(phoneId);
    }

    private int checkColorsCount(PhoneForAddToCart phoneForAddToCart) {
        int checks = 0;

        for (PhoneColors phoneColor : phoneForAddToCart.getPhoneColors()) {
            if (phoneColor.isEnabled()) {
                checks++;
            }
        }

        return checks;
    }

    private String findColor(PhoneForAddToCart phoneForAddToCart) {
        for (PhoneColors phoneColor : phoneForAddToCart.getPhoneColors()) {
            if (phoneColor.isEnabled()) {
                return phoneColor.getColor();
            }
        }

        return null;
    }
}
