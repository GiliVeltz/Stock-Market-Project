package Domain.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import Domain.Entities.User;

@NoRepositoryBean
public interface InterfaceUserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    User findByUserName(String username);
}
