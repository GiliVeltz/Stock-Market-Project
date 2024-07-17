package Domain.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import Domain.Entities.Discounts.Discount;

@NoRepositoryBean
public interface InterfaceDiscountRepository extends JpaRepository<Discount, Integer>{

}
