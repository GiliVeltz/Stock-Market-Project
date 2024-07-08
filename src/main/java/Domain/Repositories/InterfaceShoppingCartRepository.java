package Domain.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

import Domain.Entities.ShoppingCart;
import Domain.Entities.User;

@NoRepositoryBean
public interface InterfaceShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> {

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM ShoppingCart c WHERE c._username = ?1")
    ShoppingCart getCartByUsername(String username);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Order c")
    int getUniqueOrderID();
}
