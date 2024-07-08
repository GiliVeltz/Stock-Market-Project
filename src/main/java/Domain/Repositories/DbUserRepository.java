package Domain.Repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Domain.Entities.User;

@Repository
public interface DbUserRepository extends JpaRepository<User, Long>{
    // User findByName(String userName);
} 
