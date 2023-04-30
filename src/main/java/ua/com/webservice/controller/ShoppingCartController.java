package ua.com.webservice.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.com.webservice.mapper.OrderMapper;
import ua.com.webservice.model.check.ClientCheck;
import ua.com.webservice.model.dto.CreateOrderParams;
import ua.com.webservice.model.dto.PhoneForAddToCart;
import ua.com.webservice.model.dto.PhoneForShoppingCart;
import ua.com.webservice.model.phone.PhoneInstance;
import ua.com.webservice.model.shoppingcart.ShoppingCart;
import ua.com.webservice.model.user.RegisteredUser;
import ua.com.webservice.service.clientcheck.ClientCheckService;
import ua.com.webservice.service.phone.PhoneInstanceService;
import ua.com.webservice.service.user.UserDetailsServiceImpl;
import ua.com.webservice.util.Util;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/shopping-cart")
public class ShoppingCartController {
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final PhoneInstanceService phoneInstanceService;
    private final ClientCheckService clientCheckService;
    private final ProductsController productsController;
    private final List DELIVERY_TYPE = List.of("Самовивіз з магазину", "Служба доставки", "Кур'єрська доставка");
    private final List PAYMENT_TYPE = List.of("Оплатити карткою на сайті", "Оплата при отриманні");

    public ShoppingCartController(PhoneInstanceService phoneInstanceService,
                                  UserDetailsServiceImpl userDetailsServiceImpl, ClientCheckService clientCheckService,
                                  ProductsController productsController) {
        this.phoneInstanceService = phoneInstanceService;
        this.clientCheckService = clientCheckService;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.productsController = productsController;
    }

    @GetMapping
    public String getShoppingCart(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<String> defaultOrder = clientCheckService.findDefaultCheckIdForUserEmail(email);
        ShoppingCart shoppingCart = userDetailsServiceImpl.findShoppingCartForUserEmail(email);

        if (defaultOrder.isPresent()) {
            phoneInstanceService.goBackToShoppingCart(defaultOrder.get(), shoppingCart);
            clientCheckService.cancelCheck(defaultOrder.get());
        }

        List<PhoneForShoppingCart> phones = phoneInstanceService.findAllPhoneForShoppingCartId(shoppingCart.getId());
        String deletePhone = "/shopping-cart/delete-from-cart?phoneId=";

        model.addAttribute("totalPrice", phoneInstanceService.findPriceForShoppingCartId(shoppingCart.getId()));
        model.addAttribute("phones", phones);
        model.addAttribute("deletePhone", deletePhone);
        return "shoppingcart";
    }

    @PutMapping("/add-to-cart")
    public String addToCart(Model model, PhoneForAddToCart phoneForAddToCart) {
        int checks = Util.checkColorsCount(phoneForAddToCart);

        if (checks == 0) {
            return redirectUrl(model, phoneForAddToCart, "Ви повинні вибрати колір телефону");
        }
        if (checks >= 2) {
            return redirectUrl(model, phoneForAddToCart, "Ви можете обрати лише один колір телефону");
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
            return redirectUrl(model, phoneForAddToCart, "Вибачте, але ця модель телефону закінчилась");
        }

        ShoppingCart shoppingCart = userDetailsServiceImpl.findShoppingCartForUserEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        phoneInstanceService.setShoppingCartForPhone(shoppingCart, phoneId);

        return redirectUrl(model, phoneForAddToCart, "Телефон успішно додано до кошика");
    }

    @PutMapping("/delete-from-cart")
    public String deleteFromShoppingCart(@RequestParam(value = "phoneId") String phoneId) {
        phoneInstanceService.delShoppingCartForPhone(phoneId);
        return "redirect:/shopping-cart";
    }

    @GetMapping("/back-to-cart")
    public String getBackToCart(@RequestParam(value = "id") String id) {
        ShoppingCart shoppingCart = userDetailsServiceImpl.findShoppingCartForUserEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        phoneInstanceService.goBackToShoppingCart(id, shoppingCart);
        clientCheckService.cancelCheck(id);

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
        clientCheckService.save(OrderMapper.fillClientCheckDefaultValues(clientCheck));
        ClientCheck clientCheckFromDB = clientCheckService.findClientCheckForUserIdForNewOrder(registeredUser.getId(), date).get();

        phoneInstances.forEach(phoneInstance -> phoneInstanceService.addPhoneToClientCheck(clientCheckFromDB, phoneInstance.getId()));
        phoneInstances.forEach(phoneInstance -> phoneInstanceService.delShoppingCartForPhone(phoneInstance.getId()));

        SimpleDateFormat formatter = new SimpleDateFormat("dd.M.yyyy HH:mm:ss", Locale.ENGLISH);

        model.addAttribute("created", formatter.format(clientCheckFromDB.getCreated()));
        model.addAttribute("totalPrice", phoneInstanceService.findPriceForClientCheckId(clientCheckFromDB.getId()));
        model.addAttribute("deliveryTypes", DELIVERY_TYPE);
        model.addAttribute("paymentTypes", PAYMENT_TYPE);
        model.addAttribute("msg", "");
        model.addAttribute("createOrderParams", new CreateOrderParams());
        model.addAttribute("clientCheck", clientCheckFromDB);
        model.addAttribute("registeredUser", registeredUser);
        return "setparamsfororder";
    }

    private String redirectUrl(Model model, PhoneForAddToCart phoneForAddToCart, String message) {
        return productsController.getFullInfo(model, 1,
                phoneForAddToCart.getBrand(), phoneForAddToCart.getName(),
                phoneForAddToCart.getSeries(), phoneForAddToCart.getAmountOfBuiltInMemory(),
                phoneForAddToCart.getAmountOfRam(), message);
    }
}
