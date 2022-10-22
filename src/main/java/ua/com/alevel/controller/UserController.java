package ua.com.alevel.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.model.dto.UserRegistration;
import ua.com.alevel.model.user.RegisteredUser;
import ua.com.alevel.service.clientcheck.ClientCheckService;
import ua.com.alevel.service.user.UserDetailsServiceImpl;
import java.util.List;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/users")
public class UserController {
    private static final int REQUIRED_AGE = 16;
    private static final String REGEX_FOR_PHONE_NUMBER = "[a-zA-Z]";
    private static final int PHONE_NUMBER_SIZE = 13;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final ClientCheckService clientCheckService;

    public UserController(UserDetailsServiceImpl userDetailsServiceImpl, ClientCheckService clientCheckService) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.clientCheckService = clientCheckService;
    }

    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("user", new UserRegistration());
        return "register";
    }

    @PostMapping("/new")
    public String saveUser(Model model, UserRegistration userRegistration) {
        if (userRegistration.getFirstName().isBlank()) {
            return errorModel(model, new UserRegistration(), "First name field is empty");
        }
        if (userRegistration.getMiddleName().isBlank()) {
            return errorModel(model, new UserRegistration(), "Middle name field is empty");
        }
        if (userRegistration.getLastName().isBlank()) {
            return errorModel(model, new UserRegistration(), "Last name field is empty");
        }
        if (userRegistration.getAge() <= 0) {
            return errorModel(model, new UserRegistration(), "Incorrect value in age field");
        }
        if (userRegistration.getAge() < REQUIRED_AGE) {
            return errorModel(model, new UserRegistration(), "You're too young");
        }
        if (userRegistration.getPhoneNumber().charAt(0) != '+' || Pattern.compile(REGEX_FOR_PHONE_NUMBER).matcher(userRegistration.getPhoneNumber()).find()
                || userRegistration.getPhoneNumber().length() != PHONE_NUMBER_SIZE) {
            return errorModel(model, new UserRegistration(), "Incorrect phone number. For example: +380123456789");
        }
        if (userRegistration.getPassword().isBlank() || userRegistration.getSecondPassword().isBlank()) {
            return errorModel(model, new UserRegistration(), "Password field is empty");
        }
        if (!userRegistration.getPassword().equals(userRegistration.getSecondPassword())) {
            return errorModel(model, new UserRegistration(), "Passwords are different");
        }

        if (!userDetailsServiceImpl.save(userRegistration)) {
            return errorModel(model, new UserRegistration(), "This email address is already exist");
        }
        else {
            return "login";
        }
    }

    private String errorModel(Model model, UserRegistration userRegistration, String text) {
        model.addAttribute("errorStat", true);
        model.addAttribute("errorString", text);
        model.addAttribute("user", userRegistration);
        return "register";
    }

    @GetMapping("/profile")
    public String getProfile(Model model) {
        RegisteredUser user = userDetailsServiceImpl.findUserByEmailAddress(SecurityContextHolder.getContext().getAuthentication().getName());
        List<ClientCheck> tempChecks = clientCheckService.findAllNoClosedChecksForUserId(user.getId());
        List<ClientCheck> historyChecks = clientCheckService.findAllClosedChecksForUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("tempChecks", tempChecks);
        model.addAttribute("historyChecks", historyChecks);
        return "profile";
    }
}
