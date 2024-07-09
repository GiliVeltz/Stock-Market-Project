package Domain.Repositories;

import org.springframework.stereotype.Repository;

import Domain.Entities.ShoppingCart;

@Repository
public interface InterfaceShoppingCartRepository {
    void addCartForUser(String username, ShoppingCart cart); 

    ShoppingCart getCartByUsername(String username);

    int getUniqueOrderID();
}
