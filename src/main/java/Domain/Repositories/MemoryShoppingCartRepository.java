package Domain.Repositories;

import java.util.HashMap;
import java.util.Map;

import Domain.ShoppingCart;

import org.springframework.stereotype.Repository;

@Repository
public class MemoryShoppingCartRepository implements InterfaceShoppingCartRepository {
    Map<String, ShoppingCart> _shoppingCarts;
    private int _orderIdCounter;

    public MemoryShoppingCartRepository() {
        _shoppingCarts = new HashMap<>();
        _orderIdCounter = 0;
    }

    @Override
    public void addCartForUser(String username, ShoppingCart cart) {
        _shoppingCarts.put(username, cart);
    }

    @Override
    public ShoppingCart getCartByUsername(String username) {
        return _shoppingCarts.get(username);
    }

    @Override
    public synchronized int getUniqueOrderID() {
        return _orderIdCounter++;
    }
}
