package Domain.Repositories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Domain.User;

public class MemoryUserRepository implements UserRepositoryInterface {
    private Map<String, User> _registeredUsers;

    public MemoryUserRepository(List<User> registeredUsers) {
        _registeredUsers = new HashMap<>();
        for (User user : registeredUsers)
            _registeredUsers.put(user.getUserName(), user);
    }

    public boolean doesUserExist(String username) {
        return username != null && _registeredUsers.containsKey(username);
    }

    public User getUserByUsername(String username) {
        return _registeredUsers.get(username);
    }

    public void addUser(User user) {
        _registeredUsers.put(user.getUserName(), user);
    }

    public List<User> getAllUsers() {
        return (List<User>) _registeredUsers.values();
    }

}
