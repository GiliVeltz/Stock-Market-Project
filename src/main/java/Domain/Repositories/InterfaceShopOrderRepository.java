package Domain.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import Domain.Entities.ShopOrder;

@NoRepositoryBean
public interface InterfaceShopOrderRepository extends JpaRepository<ShopOrder, Integer>{

}
