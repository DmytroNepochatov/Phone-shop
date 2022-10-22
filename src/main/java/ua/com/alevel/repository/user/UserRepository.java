package ua.com.alevel.repository.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.shoppingcart.ShoppingCart;
import ua.com.alevel.model.user.RegisteredUser;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<RegisteredUser, String> {

    Optional<RegisteredUser> findFirstByEmailAddress(String emailAddress);

    @Query("select user.shoppingCart from RegisteredUser user where user.emailAddress = ?1")
    Optional<ShoppingCart> findShoppingCartForUserEmail(String emailAddress);
}
