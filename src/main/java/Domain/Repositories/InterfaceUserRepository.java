package Domain.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import Domain.Entities.User;

@NoRepositoryBean
public interface InterfaceUserRepository extends JpaRepository<User, Integer> {
    boolean existsByusername(String username);

    User findByusername(String username);

    // Method to retrieve all messages of a user by username
    @Query("SELECT u.messages FROM User u WHERE u.username = :username")
    List<String> findMessagesByUsername(@Param("username") String username);



    
}
