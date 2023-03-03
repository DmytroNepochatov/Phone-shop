package ua.com.alevel.repository.shoppingcart;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.shoppingcart.ShoppingCart;
import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, String> {

    @Query("select shoppingCart from ShoppingCart shoppingCart where shoppingCart.registeredUser.emailAddress = ?1")
    Optional<ShoppingCart> findShoppingCartForUserEmail(String emailAddress);
}
