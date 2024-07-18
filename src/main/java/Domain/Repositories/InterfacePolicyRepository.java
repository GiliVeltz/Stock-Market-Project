package Domain.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import Domain.Entities.Discounts.Discount;
import Domain.Entities.Policies.Policy;

public interface InterfacePolicyRepository extends JpaRepository<Policy, Integer>{

}
