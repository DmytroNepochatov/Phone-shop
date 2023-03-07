package ua.com.alevel.repository.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.user.RegisteredUser;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<RegisteredUser, String> {

    Optional<RegisteredUser> findFirstByEmailAddress(String emailAddress);

    @Query("select count(registeredUser) from RegisteredUser registeredUser where registeredUser.role = 'USER'")
    int countAllUsers();

    @Query("select registeredUser from RegisteredUser registeredUser where registeredUser.checks.size = 0 and registeredUser.role = 'USER'")
    List<RegisteredUser> findAllUsersWhichDoesntHaveAnyPurchases();

    @Query("select registeredUser from RegisteredUser registeredUser where registeredUser.checks.size >= 2 and registeredUser.role = 'USER'")
    List<RegisteredUser> findAllUsersWhichHaveMoreThanOnePurchases();
}