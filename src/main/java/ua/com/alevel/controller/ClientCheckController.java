package ua.com.alevel.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.Token;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.com.alevel.mapper.OrderMapper;
import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.model.dto.CreateOrderParams;
import ua.com.alevel.model.dto.OrderInfoForMail;
import ua.com.alevel.model.user.RegisteredUser;
import ua.com.alevel.service.clientcheck.ClientCheckService;
import ua.com.alevel.service.mailsender.MailSender;
import ua.com.alevel.service.phone.PhoneInstanceService;
import ua.com.alevel.service.stripe.StripeService;
import ua.com.alevel.service.user.UserDetailsServiceImpl;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/client-check")
public class ClientCheckController {
    private final ClientCheckService clientCheckService;
    private final PhoneInstanceService phoneInstanceService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final MailSender mailSender;
    private final StripeService stripeService;
    private static final String REGEX_FOR_PHONE_NUMBER = "[a-zA-Z]";
    private static final int PHONE_NUMBER_SIZE = 12;
    private static final int CREDIT_CARD_NUMBER_COUNT = 16;
    private final List DELIVERY_TYPE = List.of("Pickup from the store", "Delivery service", "Courier delivery");
    private final List PAYMENT_TYPE = List.of("Pay by card on the website", "Payment upon receipt");

    public ClientCheckController(ClientCheckService clientCheckService, PhoneInstanceService phoneInstanceService,
                                 UserDetailsServiceImpl userDetailsServiceImpl, MailSender mailSender, StripeService stripeService) {
        this.clientCheckService = clientCheckService;
        this.phoneInstanceService = phoneInstanceService;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.mailSender = mailSender;
        this.stripeService = stripeService;
    }

    @GetMapping
    public String getClientCheckById(Model model, @RequestParam(value = "id") String id) {
        ClientCheck clientCheck = clientCheckService.findById(id).get();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.M.yyyy HH:mm:ss", Locale.ENGLISH);

        if (clientCheck.getClosedDate() != null) {
            model.addAttribute("closedDate", formatter.format(clientCheck.getClosedDate()));
        }

        model.addAttribute("created", formatter.format(clientCheck.getCreated()));
        model.addAttribute("clientCheck", clientCheck);
        model.addAttribute("totalPrice", phoneInstanceService.findPriceForClientCheckId(clientCheck.getId()));
        model.addAttribute("registeredUser", userDetailsServiceImpl.findUserByEmailAddress(SecurityContextHolder.getContext().getAuthentication().getName()).get());
        return "checkinfo";
    }

    @PostMapping("/create-order-last-params")
    public String createOrderLastParams(Model model, CreateOrderParams createOrderParams) {
        if (createOrderParams.getDeliveryType().equals("Select delivery type")) {
            return createOrderError(model, "You must select a delivery type", createOrderParams.getCheckId());
        }
        if (createOrderParams.getPaymentType().equals("Select payment type")) {
            return createOrderError(model, "You must select a payment type", createOrderParams.getCheckId());
        }
        if (createOrderParams.getRecipient().isBlank()) {
            return createOrderError(model, "Last name and first name for recipient field is empty", createOrderParams.getCheckId());
        }
        if (createOrderParams.getRecipientNumberPhone().charAt(0) != '+' || Pattern.compile(REGEX_FOR_PHONE_NUMBER).matcher(createOrderParams.getRecipientNumberPhone()).find()
                || createOrderParams.getRecipientNumberPhone().length() != PHONE_NUMBER_SIZE) {
            return createOrderError(model, "Incorrect phone number for recipient. For example: +10123456789", createOrderParams.getCheckId());
        }

        if (createOrderParams.getDeliveryType().equals(DELIVERY_TYPE.get(1)) ||
                createOrderParams.getDeliveryType().equals(DELIVERY_TYPE.get(2))) {
            if (createOrderParams.getDeliveryAddress().isBlank()) {
                return createOrderError(model, "Delivery address field is empty", createOrderParams.getCheckId());
            }
        }

        if (createOrderParams.getPaymentType().equals(PAYMENT_TYPE.get(0))) {
            if (createOrderParams.getNameOnCard().isBlank()) {
                return createOrderError(model, "Name on card field is empty", createOrderParams.getCheckId());
            }
            if (createOrderParams.getCreditCardNumber().isBlank()) {
                return createOrderError(model, "Credit card number field is empty", createOrderParams.getCheckId());
            }
            if (Pattern.compile(REGEX_FOR_PHONE_NUMBER).matcher(createOrderParams.getCreditCardNumber()).find() ||
                    createOrderParams.getCreditCardNumber().length() != CREDIT_CARD_NUMBER_COUNT) {
                return createOrderError(model, "Incorrect credit card number", createOrderParams.getCheckId());
            }
            if (createOrderParams.getExpiration().isBlank()) {
                return createOrderError(model, "Expiration field is empty", createOrderParams.getCheckId());
            }
            if (Pattern.compile(REGEX_FOR_PHONE_NUMBER).matcher(createOrderParams.getExpiration()).find()) {
                return createOrderError(model, "Incorrect expiration", createOrderParams.getCheckId());
            }
            if (createOrderParams.getCvc().isBlank()) {
                return createOrderError(model, "CVC field is empty", createOrderParams.getCheckId());
            }
            if (Pattern.compile(REGEX_FOR_PHONE_NUMBER).matcher(createOrderParams.getCvc()).find() ||
                    createOrderParams.getCvc().length() != 3) {
                return createOrderError(model, "Incorrect CVC", createOrderParams.getCheckId());
            }

            String[] expirationArr = createOrderParams.getExpiration().split("/");
            int expirationYear = Integer.parseInt(expirationArr[1]) + 2000;

            if (Integer.parseInt(expirationArr[0]) <= 0 || Integer.parseInt(expirationArr[0]) > 12) {
                return createOrderError(model, "Incorrect expiration month", createOrderParams.getCheckId());
            }
            if (expirationYear > LocalDate.now().getYear() + 40) {
                return createOrderError(model, "Incorrect expiration year", createOrderParams.getCheckId());
            }
            if (expirationYear < LocalDate.now().getYear() || (expirationYear == LocalDate.now().getYear()
                    && Integer.parseInt(expirationArr[0]) < LocalDate.now().getMonthValue())) {
                return createOrderError(model, "Your card's expiration year is invalid", createOrderParams.getCheckId());
            }

            try {
                Map<String, Object> card = new HashMap<>();
                card.put("number", createOrderParams.getCreditCardNumber());
                card.put("exp_month", Integer.parseInt(expirationArr[0]));
                card.put("exp_year", expirationYear);
                card.put("cvc", createOrderParams.getCvc());
                card.put("name", createOrderParams.getNameOnCard());
                Map<String, Object> params = new HashMap<>();
                params.put("card", card);

                Token token = Token.create(params);
                stripeService.charge(
                        (int) (phoneInstanceService.findPriceForClientCheckId(createOrderParams.getCheckId()) * 100),
                        "usd",
                        SecurityContextHolder.getContext().getAuthentication().getName(),
                        token.getId()
                );
            }
            catch (StripeException e) {
                return createOrderError(model, e.getMessage(), createOrderParams.getCheckId());
            }
        }

        ClientCheck clientCheck = OrderMapper.createOrderParamsToClientCheck(clientCheckService.findById(createOrderParams.getCheckId()).get(),
                createOrderParams);
        clientCheckService.save(clientCheck);

        SimpleDateFormat formatter = new SimpleDateFormat("dd.M.yyyy HH:mm:ss", Locale.ENGLISH);
        RegisteredUser registeredUser = userDetailsServiceImpl.findUserByEmailAddress(SecurityContextHolder.getContext().getAuthentication().getName()).get();

        if (createOrderParams.isSendEmail()) {
            mailSender.sendMailPurchaseNotice(registeredUser.getEmailAddress(), "Your order " + clientCheck.getId() + " has been accepted",
                    new OrderInfoForMail(clientCheck, formatter.format(clientCheck.getCreated()),
                            phoneInstanceService.findPriceForClientCheckId(clientCheck.getId()), true, registeredUser));
        }

        model.addAttribute("created", formatter.format(clientCheck.getCreated()));
        model.addAttribute("totalPrice", phoneInstanceService.findPriceForClientCheckId(clientCheck.getId()));
        model.addAttribute("clientCheck", clientCheck);
        model.addAttribute("registeredUser", registeredUser);
        return "order";
    }

    private String createOrderError(Model model, String msg, String checkId) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.M.yyyy HH:mm:ss", Locale.ENGLISH);
        ClientCheck clientCheckFromDB = clientCheckService.findById(checkId).get();

        model.addAttribute("created", formatter.format(clientCheckFromDB.getCreated()));
        model.addAttribute("totalPrice", phoneInstanceService.findPriceForClientCheckId(clientCheckFromDB.getId()));
        model.addAttribute("deliveryTypes", DELIVERY_TYPE);
        model.addAttribute("paymentTypes", PAYMENT_TYPE);
        model.addAttribute("msg", msg);
        model.addAttribute("createOrderParams", new CreateOrderParams());
        model.addAttribute("clientCheck", clientCheckFromDB);
        model.addAttribute("registeredUser", userDetailsServiceImpl.findUserByEmailAddress(SecurityContextHolder.getContext().getAuthentication().getName()).get());
        return "setparamsfororder";
    }
}
