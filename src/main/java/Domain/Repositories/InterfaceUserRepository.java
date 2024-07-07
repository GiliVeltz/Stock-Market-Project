package Domain.Repositories;

import java.util.List;

import Domain.User;

public interface InterfaceUserRepository {
    boolean doesUserExist(String username);

    User getUserByUsername(String username);

    void addUser(User user);

    List<User> getAllUsers();
}
