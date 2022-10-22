package ua.com.alevel.service.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.alevel.mapper.UserMapper;
import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.model.dto.UserOrdersForAdmin;
import ua.com.alevel.model.dto.UserRegistration;
import ua.com.alevel.model.shoppingcart.ShoppingCart;
import ua.com.alevel.model.user.RegisteredUser;
import ua.com.alevel.repository.user.UserRepository;
import java.util.ArrayList;
import java.util.List;

@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean save(UserRegistration userRegistration) {
        if (userRepository.findFirstByEmailAddress(userRegistration.getEmailAddress()).isPresent()) {
            return false;
        }

        userRepository.save(UserMapper.mapUserRegistrationToRegisteredUserCreate(userRegistration, passwordEncoder));
        LOGGER.info("User {} {} {} {} successfully saved", userRegistration.getLastName(), userRegistration.getFirstName(),
                userRegistration.getMiddleName(), userRegistration.getPhoneNumber());
        return true;
    }

    public RegisteredUser findUserByEmailAddress(String emailAddress) {
        return userRepository.findFirstByEmailAddress(emailAddress).get();
    }

    public ShoppingCart findShoppingCartForUserEmail(String emailAddress) {
        return userRepository.findShoppingCartForUserEmail(emailAddress).get();
    }

    public List<UserOrdersForAdmin> getUsersOrdersForAdmin() {
        List<UserOrdersForAdmin> usersOrdersForAdmin = new ArrayList<>();

        for (RegisteredUser registeredUser : userRepository.findAll()) {
            List<ClientCheck> checkList = new ArrayList<>();
            registeredUser.getChecks().forEach(check -> {
                if (!check.isClosed()) {
                    checkList.add(check);
                }
            });
            usersOrdersForAdmin.add(UserMapper.mapRegisteredUsersToUsersOrdersForAdmin(registeredUser, checkList));
        }

        List<UserOrdersForAdmin> result = new ArrayList<>();

        usersOrdersForAdmin.forEach(order -> {
            if (!order.getChecks().isEmpty()) {
                result.add(order);
            }
        });

        return result;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        RegisteredUser registeredUser = userRepository.findFirstByEmailAddress(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(registeredUser.getRole().name()));

        return new org.springframework.security.core.userdetails.User(
                registeredUser.getEmailAddress(),
                registeredUser.getPassword(),
                roles);
    }
}
