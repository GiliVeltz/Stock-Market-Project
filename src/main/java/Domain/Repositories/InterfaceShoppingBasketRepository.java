package Domain.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import Domain.Entities.ShoppingBasket;

@NoRepositoryBean
public interface InterfaceShoppingBasketRepository extends JpaRepository<ShoppingBasket, Integer>{

    @Query("SELECT c FROM ShoppingBasket c WHERE c.shoppingBasketId = ?1")
    List<ShoppingBasket> getShoppingBasketsByCartId(int cartId);

    @Query("SELECT p.productId FROM ShoppingBasket sb JOIN sb.productsList p WHERE sb.shoppingBasketId = :basketId")
    List<Integer> getProductIdsList(int basketId);
}
