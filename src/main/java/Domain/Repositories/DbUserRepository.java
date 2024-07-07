package Domain.Repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import Domain.Customer;
import Domain.User;

public interface DbUserRepository extends JpaRepository<Customer, Long>{

} 
