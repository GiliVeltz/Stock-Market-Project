package Domain.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import Domain.Entities.ShoppingBasket;

@NoRepositoryBean
public interface InterfaceShoppingBasketRepository extends JpaRepository<ShoppingBasket, Integer>{

    @Query("SELECT c FROM ShoppingBasket c WHERE c.shoppingBasketId = ?1")
    List<ShoppingBasket> getShoppingBasketsByCartId(int cartId);
}
