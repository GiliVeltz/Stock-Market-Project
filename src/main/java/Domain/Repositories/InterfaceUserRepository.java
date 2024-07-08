package Domain.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import Domain.Entities.User;

@NoRepositoryBean
public interface InterfaceUserRepository extends JpaRepository<User, Long> {
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u._username = ?1")
    boolean doesUserExist(String _username);

    @Query("SELECT u FROM User u WHERE u._username = ?1")
    User getUserByUsername(String _username);
}
