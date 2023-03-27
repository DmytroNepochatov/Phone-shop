package ua.com.alevel.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.model.dto.OrderInfoForMail;
import ua.com.alevel.model.dto.PhoneForAddToCart;
import ua.com.alevel.model.dto.PhoneForShoppingCart;
import ua.com.alevel.model.phone.PhoneInstance;
import ua.com.alevel.model.shoppingcart.ShoppingCart;
import ua.com.alevel.model.user.RegisteredUser;
import ua.com.alevel.service.clientcheck.ClientCheckService;
import ua.com.alevel.service.mailsender.MailSender;
import ua.com.alevel.service.phone.PhoneInstanceService;
import ua.com.alevel.service.user.UserDetailsServiceImpl;
import ua.com.alevel.util.Util;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/shopping-cart")
public class ShoppingCartController {
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final PhoneInstanceService phoneInstanceService;
    private final ClientCheckService clientCheckService;
    private final MailSender mailSender;

    public ShoppingCartController(PhoneInstanceService phoneInstanceService,
                                  UserDetailsServiceImpl userDetailsServiceImpl, ClientCheckService clientCheckService,
                                  MailSender mailSender) {
        this.phoneInstanceService = phoneInstanceService;
        this.clientCheckService = clientCheckService;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.mailSender = mailSender;
    }

    @GetMapping
    public String getShoppingCart(Model model) {
        ShoppingCart shoppingCart = userDetailsServiceImpl.findShoppingCartForUserEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        List<PhoneForShoppingCart> phones = phoneInstanceService.findAllPhoneForShoppingCartId(shoppingCart.getId());
        String deletePhone = "/shopping-cart/delete-from-cart?phoneId=";

        model.addAttribute("totalPrice", phoneInstanceService.findPriceForShoppingCartId(shoppingCart.getId()));
        model.addAttribute("phones", phones);
        model.addAttribute("deletePhone", deletePhone);
        return "shoppingcart";
    }

    @PutMapping("/add-to-cart")
    public String addToCart(PhoneForAddToCart phoneForAddToCart) {
        int checks = Util.checkColorsCount(phoneForAddToCart);

        if (checks == 0) {
            return redirectUrl(phoneForAddToCart, "You must choose the color of the phone");
        }
        if (checks >= 2) {
            return redirectUrl(phoneForAddToCart, "You can only choose one phone color");
        }

        String color = Util.findColor(phoneForAddToCart);

        List<String> ids = phoneInstanceService.findFirstIdPhoneForShoppingCart(phoneForAddToCart.getBrand(), phoneForAddToCart.getName(),
                phoneForAddToCart.getSeries(), phoneForAddToCart.getAmountOfBuiltInMemory(), phoneForAddToCart.getAmountOfRam(),
                color);

        String phoneId = null;
        if (!ids.isEmpty()) {
            phoneId = ids.get(0);
        }

        if (phoneId == null) {
            return redirectUrl(phoneForAddToCart, "Sorry, but this type of phone is out of stock");
        }

        ShoppingCart shoppingCart = userDetailsServiceImpl.findShoppingCartForUserEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        phoneInstanceService.setShoppingCartForPhone(shoppingCart, phoneId);

        return redirectUrl(phoneForAddToCart, "The phone has been successfully added to the shopping cart");
    }

    @PutMapping("/delete-from-cart")
    public String deleteFromShoppingCart(@RequestParam(value = "phoneId") String phoneId) {
        phoneInstanceService.delShoppingCartForPhone(phoneId);
        return "redirect:/shopping-cart";
    }

    @PutMapping("/create-order")
    public String createOrder(Model model) {
        ShoppingCart shoppingCart = userDetailsServiceImpl.findShoppingCartForUserEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        List<PhoneInstance> phoneInstances = phoneInstanceService.findAllPhonesForShoppingCartId(shoppingCart.getId());
        RegisteredUser registeredUser = userDetailsServiceImpl.findUserByEmailAddress(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        Date date = new Date();

        ClientCheck clientCheck = new ClientCheck();
        clientCheck.setRegisteredUser(registeredUser);
        clientCheck.setCreated(date);
        clientCheck.setPhoneInstances(phoneInstances);
        clientCheck.setClosed(false);
        clientCheckService.save(clientCheck);
        ClientCheck clientCheckFromDB = clientCheckService.findClientCheckForUserIdForNewOrder(registeredUser.getId(), date).get();

        phoneInstances.forEach(phoneInstance -> phoneInstanceService.addPhoneToClientCheck(clientCheckFromDB, phoneInstance.getId()));
        phoneInstances.forEach(phoneInstance -> phoneInstanceService.delShoppingCartForPhone(phoneInstance.getId()));

        SimpleDateFormat formatter = new SimpleDateFormat("dd.M.yyyy HH:mm:ss", Locale.ENGLISH);

        mailSender.sendMailPurchaseNotice(registeredUser.getEmailAddress(), "Your order " + clientCheckFromDB.getId() + " has been accepted",
                new OrderInfoForMail(clientCheckFromDB, formatter.format(clientCheckFromDB.getCreated()),
                        phoneInstanceService.findPriceForClientCheckId(clientCheckFromDB.getId()), true, registeredUser));

        model.addAttribute("clientCheck", clientCheckFromDB.getId());
        return "order";
    }

    private String redirectUrl(PhoneForAddToCart phoneForAddToCart, String message) {
        return "redirect:/fullinfo?page=1&brand=" + phoneForAddToCart.getBrand() + "&name=" + phoneForAddToCart.getName() + "&series=" + phoneForAddToCart.getSeries() + "&amountOfBuiltInMemory=" + phoneForAddToCart.getAmountOfBuiltInMemory() + "&amountOfRam=" + phoneForAddToCart.getAmountOfRam() +
                "&successAddToCart=" + message;
    }
}
