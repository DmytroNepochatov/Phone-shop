package ua.com.alevel.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;
import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.model.dto.UserOrdersForAdmin;
import ua.com.alevel.model.dto.UserRegistration;
import ua.com.alevel.model.user.RegisteredUser;
import ua.com.alevel.model.user.Role;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public static UserOrdersForAdmin mapRegisteredUsersToUsersOrdersForAdmin(RegisteredUser registeredUser, List<ClientCheck> checkList) {
        return new UserOrdersForAdmin(registeredUser.getLastName(), registeredUser.getFirstName(),
                registeredUser.getMiddleName(), registeredUser.getPhoneNumber(), checkList);
    }
}
