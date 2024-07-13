package Domain.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import Domain.Entities.Order;

@NoRepositoryBean
public interface InterfaceOrderRepository  extends JpaRepository<Order, Long>{

}
