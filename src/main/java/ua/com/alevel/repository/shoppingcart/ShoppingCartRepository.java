package ua.com.alevel.repository.shoppingcart;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.shoppingcart.ShoppingCart;
import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, String> {
    @Modifying
    @Query("update ShoppingCart cart set cart.price =?1 where cart.id=?2")
    void setPriceForShoppingCart(double price, String id);

    @Query("select cart.price from ShoppingCart cart where cart.id = ?1")
    double findPriceForShoppingCartId(String id);

    @Query("select shoppingCart from ShoppingCart shoppingCart where shoppingCart.registeredUser.emailAddress = ?1")
    Optional<ShoppingCart> findShoppingCartForUserEmail(String emailAddress);
}
