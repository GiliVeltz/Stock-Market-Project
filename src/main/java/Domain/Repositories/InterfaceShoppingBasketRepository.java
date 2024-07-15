package Domain.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import Domain.Entities.ShoppingBasket;

@NoRepositoryBean
public interface InterfaceShoppingBasketRepository extends JpaRepository<ShoppingBasket, Integer>{

}
