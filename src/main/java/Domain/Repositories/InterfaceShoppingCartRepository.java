package Domain.Repositories;

import Domain.ShoppingCart;

import org.springframework.stereotype.Repository;

@Repository
public interface InterfaceShoppingCartRepository {
    void addCartForUser(String username, ShoppingCart cart); 

    ShoppingCart getCartByUsername(String username);

    int getUniqueOrderID();
}
