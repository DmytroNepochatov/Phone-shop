package ua.com.alevel.service.shoppingcart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.alevel.repository.shoppingcart.ShoppingCartRepository;

@Service
public class ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingCartService.class);

    @Autowired
    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
    }

    @Transactional
    public void save(String shoppingCartId, double price) {
        shoppingCartRepository.setPriceForShoppingCart(price, shoppingCartId);
        LOGGER.info("Shopping cart {} price successfully updated", shoppingCartId);
    }

    public double findPriceForShoppingCartId(String id) {
        return shoppingCartRepository.findPriceForShoppingCartId(id);
    }
}
