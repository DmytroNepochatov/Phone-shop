package ua.com.alevel.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;
import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.model.dto.UserOrdersForAdmin;
import ua.com.alevel.model.dto.UserRegistration;
import ua.com.alevel.model.user.RegisteredUser;
import ua.com.alevel.model.user.Role;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public final class UserMapper {
    private UserMapper() {
    }

    public static RegisteredUser mapUserRegistrationToRegisteredUserCreate(UserRegistration userRegistration, PasswordEncoder passwordEncoder, Date dateOfBirth) {
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setLastName(userRegistration.getLastName());
        registeredUser.setFirstName(userRegistration.getFirstName());
        registeredUser.setMiddleName(userRegistration.getMiddleName());
        registeredUser.setDateOfBirth(dateOfBirth);
        registeredUser.setPhoneNumber(userRegistration.getPhoneNumber());
        registeredUser.setEmailAddress(userRegistration.getEmailAddress());
        registeredUser.setPassword(passwordEncoder.encode(userRegistration.getPassword()));
        registeredUser.setComments(new ArrayList<>());
        registeredUser.setChecks(new ArrayList<>());
        registeredUser.setRole(Role.USER);

        return registeredUser;
    }

    public static UserOrdersForAdmin mapRegisteredUsersToUsersOrdersForAdmin(RegisteredUser registeredUser, ClientCheck checkList) {
        UserOrdersForAdmin userOrder = new UserOrdersForAdmin();
        userOrder.setLastName(registeredUser.getLastName());
        userOrder.setFirstName(registeredUser.getFirstName());
        userOrder.setPhoneNumber(registeredUser.getPhoneNumber());
        userOrder.setCheck(checkList);

        return userOrder;
    }

    public static RegisteredUser changeUserInformation(SimpleDateFormat formatter, RegisteredUser registeredUser, UserRegistration userRegistration) throws Exception {
        registeredUser.setFirstName(userRegistration.getFirstName());
        registeredUser.setMiddleName(userRegistration.getMiddleName());
        registeredUser.setLastName(userRegistration.getLastName());
        registeredUser.setEmailAddress(userRegistration.getEmailAddress());
        registeredUser.setDateOfBirth(formatter.parse(userRegistration.getDateOfBirth()));
        registeredUser.setPhoneNumber(userRegistration.getPhoneNumber());

        return registeredUser;
    }

    public static RegisteredUser changeUserPassword(RegisteredUser registeredUser, UserRegistration userRegistration, PasswordEncoder passwordEncoder) {
        registeredUser.setPassword(passwordEncoder.encode(userRegistration.getPassword()));

        return registeredUser;
    }
}
