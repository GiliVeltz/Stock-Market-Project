package Domain.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import Domain.Entities.User;

@NoRepositoryBean
public interface InterfaceUserRepository extends JpaRepository<User, Long> {
    boolean existsByusername(String username);

    User findByusername(String username);

    
}
