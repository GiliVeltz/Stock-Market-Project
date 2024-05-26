package Domain.Facades;

import java.util.List;
import org.springframework.web.bind.annotation.RestController;

import Domain.Order;
import Domain.PasswordEncoderUtil;
import Domain.User;
import Domain.Repositories.MemoryUserRepository;
import Domain.Repositories.UserRepositoryInterface;
import Exceptions.ShopException;

@RestController
public class UserFacade {
    private UserRepositoryInterface _userRepository;
    private List<String> _guestIds;
    private PasswordEncoderUtil _passwordEncoder;

    public UserFacade(List<User> registeredUsers, List<String> guestIds, PasswordEncoderUtil passwordEncoder) {
        _userRepository = new MemoryUserRepository(registeredUsers);
        _guestIds = guestIds;
        _passwordEncoder = passwordEncoder;
    }

    public boolean doesUserExist(String username) {
        return _userRepository.doesUserExist(username);
    }

    public User getUserByUsername(String username) throws ShopException {
        if (username == null)
            throw new ShopException("Username is null.");
        return _userRepository.getUserByUsername(username);
    }

    public boolean AreCredentialsCorrect(String username, String password) throws ShopException {
        User user = getUserByUsername(username);
        if (user != null) {
            return this._passwordEncoder.matches(password, user.getEncodedPassword());
        }
        return false;
    }

    // this function is used to register a new user to the system.
    public void register(String userName, String password, String email) throws Exception {
        String encodedPass = this._passwordEncoder.encodePassword(password);
        if (!doesUserExist(userName)) {
            if (email == null || email.isEmpty()) {
                throw new Exception("Email is empty.");
            }
            if (password == null || password.isEmpty() || password.length() < 5) {
                throw new Exception("Password is empty.");
            }
            _userRepository.addUser(new User(userName, encodedPass, email));
        } else {
            throw new Exception("Username already exists.");
        }
    }

    // function that check if a given user is an admin
    public boolean isAdmin(String userName) throws Exception {
        User user = getUserByUsername(userName);
        if (user != null) {
            return user.isAdmin();
        } else
            throw new Exception("User not found.");
    }

    private boolean isGuestExists(String id) {
        return _guestIds.contains(id);
    }

    public void addNewGuest(String id) {
        if (isGuestExists(id)) {
            throw new IllegalArgumentException("Guest with ID " + id + " already exists.");
        }
        _guestIds.add(id);
    }

    public void removeGuest(String id) {
        if (!isGuestExists(id)) {
            throw new IllegalArgumentException("Guest with ID " + id + " does not exist.");
        }
        _guestIds.remove(String.valueOf(id));
    }

    public List<User> get_registeredUsers() {
        return _userRepository.getAllUsers();
    }

    // function to return the purchase history for the user
    public List<Order> getPurchaseHistory(String username) throws ShopException {
        User user = getUserByUsername(username);
        if (user != null) {
            return user.getPurchaseHistory();
        }
        return null;
    }

}
