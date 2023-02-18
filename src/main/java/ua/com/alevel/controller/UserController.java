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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/users")
public class UserController {
    private static final int REQUIRED_AGE = 16;
    private static final String REGEX_FOR_PHONE_NUMBER = "[a-zA-Z]";
    private static final int PHONE_NUMBER_SIZE = 13;
    private static final String DATE_PATTERN = "dd.M.yyyy";
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
    public String saveUser(Model model, UserRegistration userRegistration) throws Exception {
        if (userRegistration.getFirstName().isBlank()) {
            return errorModel(model, new UserRegistration(), "First name field is empty");
        }
        if (userRegistration.getMiddleName().isBlank()) {
            return errorModel(model, new UserRegistration(), "Middle name field is empty");
        }
        if (userRegistration.getLastName().isBlank()) {
            return errorModel(model, new UserRegistration(), "Last name field is empty");
        }
        if (!isValidDate(userRegistration.getDateOfBirth())) {
            return errorModel(model, new UserRegistration(), "Incorrect date of birth");
        }
        if (!isRequiredAge(userRegistration.getDateOfBirth())) {
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

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);

        if (!userDetailsServiceImpl.save(userRegistration, formatter.parse(userRegistration.getDateOfBirth()))) {
            return errorModel(model, new UserRegistration(), "This email address is already exist");
        }
        else {
            return "login";
        }
    }

    private boolean isValidDate(String dateStr) {
        DateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        }
        catch (ParseException e) {
            return false;
        }
        return true;
    }

    private boolean isRequiredAge(String dateStr) {
        String[] strBirth = dateStr.split("\\.");

        LocalDate dateBirth = LocalDate.of(Integer.parseInt(strBirth[2]),
                Integer.parseInt(strBirth[1]), Integer.parseInt(strBirth[0]));
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(dateBirth, currentDate);

        return period.getYears() >= REQUIRED_AGE;
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

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
        String dateOfBirth = formatter.format(user.getDateOfBirth());

        String[] strBirth = dateOfBirth.split("\\.");
        LocalDate dateBirth = LocalDate.of(Integer.parseInt(strBirth[2]),
                Integer.parseInt(strBirth[1]), Integer.parseInt(strBirth[0]));
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(dateBirth, currentDate);
        int age = period.getYears();

        model.addAttribute("dateOfBirth", dateOfBirth);
        model.addAttribute("age", age);
        model.addAttribute("user", user);
        model.addAttribute("tempChecks", tempChecks);
        model.addAttribute("historyChecks", historyChecks);
        return "profile";
    }
}
