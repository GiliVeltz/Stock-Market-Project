package Domain.Repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import Domain.User;

@Repository
public class DbUserRepository implements InterfaceUserRepository {
    
    private final JpaRepository<User, String> jpaShoppingCartRepository;

    @Autowired
    public DbUserRepository(JpaRepository<User, String> jpaShoppingCartRepository) {
        this.jpaShoppingCartRepository = jpaShoppingCartRepository;
    }

    public boolean doesUserExist(String username) {
        return jpaShoppingCartRepository.existsById(username);
    }

    public User getUserByUsername(String username) {
        return jpaShoppingCartRepository.getReferenceById(username);
    }

    public void addUser(User user) {
        jpaShoppingCartRepository.save(user);
    }

    public List<User> getAllUsers() {
        return jpaShoppingCartRepository.findAll();
    }
}
