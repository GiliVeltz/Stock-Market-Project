package Domain.Repositories;

import Domain.ShoppingCart;

public interface InterfaceShoppingCartRepository {
    void addCartForUser(String username, ShoppingCart cart); 

    ShoppingCart getCartByUsername(String username);

    int getUniqueOrderID();
}
