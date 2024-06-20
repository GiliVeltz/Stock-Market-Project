package Domain.Facades;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;

import Domain.Order;
import Domain.User;
import Domain.Alerts.Alert;
import Domain.Authenticators.EmailValidator;
import Domain.Authenticators.PasswordEncoderUtil;
import Domain.Repositories.MemoryUserRepository;
import Domain.Repositories.UserRepositoryInterface;
import Dtos.UserDto;
import Exceptions.StockMarketException;
import Exceptions.UserException;
import Server.notifications.NotificationHandler;

@RestController
public class UserFacade {
    private static UserFacade _UserFacade;
    private UserRepositoryInterface _userRepository;
    private List<String> _guestIds;
    private EmailValidator _EmailValidator;
    private PasswordEncoderUtil _passwordEncoder;

    public UserFacade(List<User> registeredUsers, List<String> guestIds) {
        _userRepository = new MemoryUserRepository(registeredUsers);
        _guestIds = guestIds;
        _EmailValidator = new EmailValidator();
        _passwordEncoder = new PasswordEncoderUtil();

        // For testing UI
        initUI();
    }

    // Public method to provide access to the _UserFacade
    public static synchronized UserFacade getUserFacade() {
        if (_UserFacade == null) {
            _UserFacade = new UserFacade(new ArrayList<>(), new ArrayList<>());
        }
        return _UserFacade;
    }

    public boolean doesUserExist(String username) {
        return _userRepository.doesUserExist(username);
    }

    public User getUserByUsername(String username) throws StockMarketException {
        if (username == null)
            throw new UserException("Username is null.");
        if (!doesUserExist(username))
            throw new UserException(String.format("Username %s does not exist.", username));
        return _userRepository.getUserByUsername(username);
    }

    public boolean AreCredentialsCorrect(String username, String raw_password) throws StockMarketException {
        User user = getUserByUsername(username);
        if (user != null) {
            return _passwordEncoder.matches(raw_password, user.getPassword());
        }
        return false;
    }

    // this function is used to register a new user to the system.
    public void register(UserDto userDto) throws StockMarketException {
        if (userDto.username == null || userDto.username.isEmpty()) {
            throw new StockMarketException("UserName is empty.");
        }
        if (userDto.email == null || userDto.email.isEmpty()) {
            throw new StockMarketException("Email is empty.");
        }
        if (userDto.password == null || userDto.password.isEmpty() || userDto.password.length() < 5) {
            throw new StockMarketException("Password is empty, or too short.");
        }
        if (!_EmailValidator.isValidEmail(userDto.email)) {
            throw new StockMarketException("Email is not valid.");
        }
        String encodedPass = this._passwordEncoder.encodePassword(userDto.password);
        userDto.password = encodedPass;
        if (!doesUserExist(userDto.username)) {
            _userRepository.addUser(new User(userDto));
        } else {
            throw new StockMarketException("Username already exists.");
        }
    }

    public void addOrderToUser(String username, Order order) throws StockMarketException {
        User user = getUserByUsername(username);
        user.addOrder(order);
    }

    // function that check if a given user is an admin
    public boolean isAdmin(String userName) throws StockMarketException {
        User user = getUserByUsername(userName);
        return user.isAdmin();
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
    public List<Order> getPurchaseHistory(String username) throws StockMarketException {
        User user = getUserByUsername(username);
        if (user != null) {
            return user.getPurchaseHistory();
        } else {
            throw new UserException("User not found.");
        }
    }

    // change email for a user
    public void changeEmail(String username, String email) throws StockMarketException {
        User user = getUserByUsername(username);
        if (email == null || email.isEmpty()) {
            throw new UserException("Email is empty.");
        }
        if (!_EmailValidator.isValidEmail(email)) {
            throw new UserException("Email is not valid.");
        }
        user.setEmail(email);
    }

    // getting the user personal details
    public UserDto getUserDetails(String username) {
        User user = _userRepository.getUserByUsername(username);
        return new UserDto(user.getUserName(), user.getPassword(), user.getEmail(), user.getBirthDate());
    }

    // set the user personal new details
    public UserDto setUserDetails(String username, UserDto userDto) throws StockMarketException {
        if (userDto.username == null || userDto.username.isEmpty()) {
            throw new StockMarketException("new UserName is empty.");
        }
        if (userDto.email == null || userDto.email.isEmpty()) {
            throw new StockMarketException("new Email is empty.");
        }
        if (userDto.password == null || userDto.password.isEmpty() || userDto.password.length() < 5) {
            throw new StockMarketException("new Password is empty, or too short.");
        }
        if (!_EmailValidator.isValidEmail(userDto.email)) {
            throw new StockMarketException("new Email is not valid.");
        }
        String encodedPass = this._passwordEncoder.encodePassword(userDto.password);
        userDto.password = encodedPass;

        User user = getUserByUsername(username);

        if (!doesUserExist(userDto.username)) {
            throw new StockMarketException("Username already exists.");
        } else {
            user.setEmail(userDto.email);
            user.setPassword(userDto.password);
            user.setBirthDate(userDto.birthDate);
        }

        return new UserDto(user.getUserName(), user.getPassword(), user.getEmail(), user.getBirthDate());
    }

    public boolean notifyUser(String targetUser, Alert alert) {
        try {
            NotificationHandler.getInstance().sendMessage(targetUser, alert);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // function to initilaize data for UI testing
    public void initUI() {
        _userRepository.addUser(new User("tal", 
                this._passwordEncoder.encodePassword("taltul"), "tal@gmail.com", new Date()));
    }

}
