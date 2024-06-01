package Domain.Facades;

import java.util.Date;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

import Domain.Order;
import Domain.User;
import Domain.Authenticators.EmailValidator;
import Domain.Authenticators.PasswordEncoderUtil;
import Domain.Repositories.MemoryUserRepository;
import Domain.Repositories.UserRepositoryInterface;
import Dtos.UserDto;
import Exceptions.ShopException;

@RestController
public class UserFacade {
    private static UserFacade _UserFacade;
    private UserRepositoryInterface _userRepository;
    private List<String> _guestIds;
    private PasswordEncoderUtil _passwordEncoder;
    private EmailValidator _EmailValidator;

    public UserFacade(List<User> registeredUsers, List<String> guestIds, PasswordEncoderUtil passwordEncoder) {
        _userRepository = new MemoryUserRepository(registeredUsers);
        _guestIds = guestIds;
        _passwordEncoder = passwordEncoder;
        _EmailValidator = new EmailValidator();
    }

    // Public method to provide access to the _UserFacade
    public static synchronized UserFacade getUserFacade(List<User> registeredUsers, List<String> guestIds, PasswordEncoderUtil passwordEncoder) {
        if (_UserFacade == null) {
            _UserFacade = new UserFacade(registeredUsers, guestIds, passwordEncoder);
        }
        return _UserFacade;
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
    public void register(UserDto userDto) throws Exception {
        // TODO: remove the encoding - should be done in the front end
        String encodedPass = this._passwordEncoder.encodePassword(userDto.password);
        if (userDto.username == null || userDto.username.isEmpty()) {
            throw new Exception("UserName is empty.");
        }
        if (userDto.email == null || userDto.email.isEmpty()) {
            throw new Exception("Email is empty.");
        }
        if (userDto.password == null || userDto.password.isEmpty() || userDto.password.length() < 5) {
            throw new Exception("Password is empty, or too short.");
        }

        if (!doesUserExist(userDto.username)) {
            _userRepository.addUser(new User(userDto.username, encodedPass, userDto.email));
        } else {
            throw new Exception("Username already exists.");
        }
    }
   
    public void addOrderToUser(String username, Order order) throws ShopException {
        User user = getUserByUsername(username);
        if (user != null) {
            user.addOrder(order);
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

    // change email for a user
    public void changeEmail(String username, String email) throws Exception {
        User user = getUserByUsername(username);
        if (user == null) {
            throw new Exception("Trying to change password for user - User not found.");
        }
        if(email == null || email.isEmpty()) {
            throw new Exception("Email is empty.");
        }
        if (!_EmailValidator.isValidEmail(email)) {
            throw new Exception("Email is not valid.");
        }
        user.setEmail(email);
    }
}
