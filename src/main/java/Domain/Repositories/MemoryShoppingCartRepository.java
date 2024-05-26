package Domain.Repositories;

import java.util.HashMap;
import java.util.Map;

import Domain.ShoppingCart;

public class MemoryShoppingCartRepository implements ShoppingCartRepositoryInterface {
    Map<String, ShoppingCart> _shoppingCarts;

    public MemoryShoppingCartRepository() {
        _shoppingCarts = new HashMap<>();
    }

    @Override
    public void addCartForUser(String username, ShoppingCart cart) {
        _shoppingCarts.put(username, cart);
    }

    @Override
    public ShoppingCart getCartByUsername(String username) {
        return _shoppingCarts.get(username);
    }
}
