package ua.com.webservice.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.com.webservice.mapper.UserMapper;
import ua.com.webservice.model.check.ClientCheck;
import ua.com.webservice.model.dto.UserRegistration;
import ua.com.webservice.model.user.RegisteredUser;
import ua.com.webservice.service.clientcheck.ClientCheckService;
import ua.com.webservice.service.user.UserDetailsServiceImpl;
import ua.com.webservice.util.Util;
import java.text.SimpleDateFormat;
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
    private static final String ERROR_STAT = "errorStat";
    private static final String ERROR_STRING = "errorString";
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final ClientCheckService clientCheckService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserDetailsServiceImpl userDetailsServiceImpl, ClientCheckService clientCheckService) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.clientCheckService = clientCheckService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("user", new UserRegistration());
        return "register";
    }

    @PostMapping("/new")
    public String saveUser(Model model, UserRegistration userRegistration) throws Exception {
        if (userRegistration.getFirstName().isBlank()) {
            return errorModel(model, new UserRegistration(), "Поле Ім'я порожнє");
        }
        if (userRegistration.getMiddleName().isBlank()) {
            return errorModel(model, new UserRegistration(), "Поле По-батькові порожнє");
        }
        if (userRegistration.getLastName().isBlank()) {
            return errorModel(model, new UserRegistration(), "Поле Прізвище порожнє");
        }
        if (!Util.isValidDate(userRegistration.getDateOfBirth(), DATE_PATTERN)) {
            return errorModel(model, new UserRegistration(), "Неправильна дата народження");
        }
        if (!Util.isRequiredAge(userRegistration.getDateOfBirth(), REQUIRED_AGE)) {
            return errorModel(model, new UserRegistration(), "Ви надто молоді");
        }
        if (userRegistration.getPhoneNumber().charAt(0) != '+' || Pattern.compile(REGEX_FOR_PHONE_NUMBER).matcher(userRegistration.getPhoneNumber()).find()
                || userRegistration.getPhoneNumber().length() != PHONE_NUMBER_SIZE) {
            return errorModel(model, new UserRegistration(), "Неправильний номер телефону. Приклад: +380123456789");
        }
        if (userRegistration.getPassword().isBlank() || userRegistration.getSecondPassword().isBlank()) {
            return errorModel(model, new UserRegistration(), "Поле Пароль порожнє");
        }
        if (!userRegistration.getPassword().equals(userRegistration.getSecondPassword())) {
            return errorModel(model, new UserRegistration(), "Паролі не співпадають");
        }

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);

        if (!userDetailsServiceImpl.save(userRegistration, formatter.parse(userRegistration.getDateOfBirth()))) {
            return errorModel(model, new UserRegistration(), "Ця електронна адреса вже існує");
        }
        else {
            return "login";
        }
    }

    @GetMapping("/profile")
    public String getProfile(Model model) {
        RegisteredUser user = userDetailsServiceImpl.findUserByEmailAddress(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        List<ClientCheck> tempChecks = clientCheckService.findAllNoClosedChecksForUserId(user.getId());
        List<ClientCheck> historyChecks = clientCheckService.findAllClosedChecksForUserId(user.getId());

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
        String dateOfBirth = formatter.format(user.getDateOfBirth());

        model.addAttribute("dateOfBirth", dateOfBirth);
        model.addAttribute("user", user);
        model.addAttribute("tempChecks", tempChecks);
        model.addAttribute("historyChecks", historyChecks);
        return "profile";
    }

    @GetMapping("/change-info-about-profile")
    public String changeInfoAboutProfile(Model model) {
        return errorModelForChangeInfoAboutProfile(model, false, "");
    }

    @GetMapping("/change-password")
    public String changePassword(Model model) {
        return errorModelForChangePassword(model, false, "");
    }

    @PostMapping("/change-password")
    public String saveChangePassword(Model model, UserRegistration userRegistration) throws Exception {
        if (userRegistration.getPassword().isBlank() || userRegistration.getSecondPassword().isBlank()) {
            return errorModelForChangePassword(model, true, "Поле Пароль порожнє");
        }
        if (!userRegistration.getPassword().equals(userRegistration.getSecondPassword())) {
            return errorModelForChangePassword(model, true, "Паролі не співпадають");
        }

        RegisteredUser userFromDB = userDetailsServiceImpl.findById(userRegistration.getUserId());

        if (passwordEncoder.matches(userRegistration.getPassword(), userFromDB.getPassword())) {
            return errorModelForChangePassword(model, true, "Пароль ідентичний старому паролю");
        }

        RegisteredUser registeredUser = UserMapper.changeUserPassword(userFromDB, userRegistration, passwordEncoder);

        userDetailsServiceImpl.saveChanges(registeredUser);
        return "login";
    }

    @PostMapping("/change-info-about-profile")
    public String saveChangesInfoAboutProfile(Model model, UserRegistration userRegistration) throws Exception {
        if (userRegistration.getFirstName().isBlank()) {
            return errorModelForChangeInfoAboutProfile(model, true, "Поле Ім'я порожнє");
        }
        if (userRegistration.getMiddleName().isBlank()) {
            return errorModelForChangeInfoAboutProfile(model, true, "Поле По-батькові порожнє");
        }
        if (userRegistration.getLastName().isBlank()) {
            return errorModelForChangeInfoAboutProfile(model, true, "Поле Прізвище порожнє");
        }
        if (!Util.isValidDate(userRegistration.getDateOfBirth(), DATE_PATTERN)) {
            return errorModelForChangeInfoAboutProfile(model, true, "Неправильна дата народження");
        }
        if (!Util.isRequiredAge(userRegistration.getDateOfBirth(), REQUIRED_AGE)) {
            return errorModelForChangeInfoAboutProfile(model, true, "Ви надто молоді");
        }
        if (userRegistration.getPhoneNumber().charAt(0) != '+' || Pattern.compile(REGEX_FOR_PHONE_NUMBER).matcher(userRegistration.getPhoneNumber()).find()
                || userRegistration.getPhoneNumber().length() != PHONE_NUMBER_SIZE) {
            return errorModelForChangeInfoAboutProfile(model, true, "Неправильний номер телефону. Приклад: +380123456789");
        }

        RegisteredUser userFromDB = userDetailsServiceImpl.findById(userRegistration.getUserId());
        String oldEmail = userFromDB.getEmailAddress();

        if (!userFromDB.getEmailAddress().equals(userRegistration.getEmailAddress()) &&
                userDetailsServiceImpl.findUserByEmailAddress(userRegistration.getEmailAddress()).isPresent()) {
            return errorModelForChangeInfoAboutProfile(model, true, "Ця електронна адреса вже існує");
        }

        RegisteredUser registeredUser = UserMapper.changeUserInformation(new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH),
                userFromDB, userRegistration);

        userDetailsServiceImpl.saveChanges(registeredUser);

        if (oldEmail.equals(userRegistration.getEmailAddress())) {
            return "redirect:/users/profile";
        }
        else {
            return "login";
        }
    }

    private String errorModel(Model model, UserRegistration userRegistration, String text) {
        model.addAttribute(ERROR_STAT, true);
        model.addAttribute(ERROR_STRING, text);
        model.addAttribute("user", userRegistration);
        return "register";
    }

    private String errorModelForChangeInfoAboutProfile(Model model, boolean flag, String text) {
        RegisteredUser registeredUser = userDetailsServiceImpl.findUserByEmailAddress(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        UserRegistration user = new UserRegistration();
        user.setUserId(registeredUser.getId());
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);

        model.addAttribute("user", user);
        model.addAttribute("registeredUser", registeredUser);
        model.addAttribute("userDate", formatter.format(registeredUser.getDateOfBirth()));
        model.addAttribute(ERROR_STAT, flag);
        model.addAttribute(ERROR_STRING, text);
        return "changeinfoaboutprofile";
    }

    private String errorModelForChangePassword(Model model, boolean flag, String text) {
        RegisteredUser registeredUser = userDetailsServiceImpl.findUserByEmailAddress(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        UserRegistration user = new UserRegistration();
        user.setUserId(registeredUser.getId());

        model.addAttribute("user", user);
        model.addAttribute(ERROR_STAT, flag);
        model.addAttribute(ERROR_STRING, text);
        return "changepassword";
    }
}
