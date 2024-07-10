package Domain.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import Domain.Entities.ShoppingCart;

@NoRepositoryBean
public interface InterfaceShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> {

    @Query("SELECT c FROM ShoppingCart c WHERE c.user_or_guest_name = ?1")
    ShoppingCart getCartByUsername(String username);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Order c")
    int getUniqueOrderID();
}
