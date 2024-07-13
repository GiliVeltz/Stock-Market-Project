package Domain.Facades;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Domain.Authenticators.EmailValidator;
import Domain.Authenticators.PasswordEncoderUtil;
import Domain.Entities.Guest;
import Domain.Entities.Order;
import Domain.Entities.ShoppingCart;
import Domain.Entities.User;
import Domain.Entities.Alerts.Alert;
import Domain.Entities.Alerts.IntegrityRuleBreakAlert;
import Domain.Repositories.DbGuestRepository;
import Domain.Repositories.DbOrderRepository;
import Domain.Repositories.DbShoppingCartRepository;
import Domain.Repositories.DbUserRepository;
import Domain.Repositories.InterfaceGuestRepository;
import Domain.Repositories.InterfaceOrderRepository;
import Domain.Repositories.InterfaceShoppingCartRepository;
import Domain.Repositories.InterfaceUserRepository;
import Dtos.OrderDto;
import Dtos.UserDto;
import Exceptions.StockMarketException;
import Exceptions.UserException;
import Server.notifications.NotificationHandler;

@SuppressWarnings("unused")
@Service
public class UserFacade {
    private InterfaceUserRepository _userRepository;
    private InterfaceGuestRepository _guestRepository;
    private InterfaceShoppingCartRepository _shoppingCartRepository;
    private InterfaceOrderRepository _orderRepository;
    private EmailValidator _EmailValidator;
    private PasswordEncoderUtil _passwordEncoder;
    private NotificationHandler _notificationHandler;

    private static final Logger logger = Logger.getLogger(UserFacade.class.getName());

    @Autowired
    public UserFacade( List<User> registeredUsers, List<String> guestIds, PasswordEncoderUtil passwordEncoder, EmailValidator EmailValidator, 
    DbUserRepository repository, DbGuestRepository guestRepo, DbShoppingCartRepository shoppingCartRepository, DbOrderRepository orderRepository, NotificationHandler notificationHandler) {
        _guestRepository = guestRepo;
        _EmailValidator = EmailValidator;
        _passwordEncoder = passwordEncoder;
        _userRepository = repository;
        _notificationHandler = notificationHandler;
        _shoppingCartRepository = shoppingCartRepository;
        _orderRepository = orderRepository;
        // // //For testing UI
        // initUI();
    }

    public UserFacade() {
    }

    // set the repositor ies to be used test time
    public void setUserFacadeRepositories(InterfaceUserRepository userRepository, InterfaceGuestRepository guestRepository, InterfaceOrderRepository orderRepository, InterfaceShoppingCartRepository shoppingCartRepository) {
        _userRepository = userRepository;
        _guestRepository = guestRepository;
        _orderRepository = orderRepository;
        _shoppingCartRepository = shoppingCartRepository;
    }

    // logIn function
    public void logIn(String userName, String password) throws StockMarketException{
        logger.info("User " + userName + " tries to log in to the system.");
        if (!AreCredentialsCorrect(userName, password)){
            throw new StockMarketException("User Name Is Not Registered Or Password Is Incorrect.");
        }
        
        User user = getUserByUsername(userName);
        if (user.isLoggedIn()){
                throw new StockMarketException("User is already logged in.");
        }
        user.logIn();
        _userRepository.save(user);
        logger.info("User " + userName + " logged in to the system.");
    }

    // logOut function
    public void logOut(String userName) throws StockMarketException{
        logger.info("User " + userName + " tries to log out from the system.");
        User user = getUserByUsername(userName);
        if (!user.isLoggedIn()){
                throw new StockMarketException("User is not logged in.");
        }
        user.logOut();
        _userRepository.save(user);
        logger.info("User " + userName + " logged out from the system.");
    }

    // function to check if a user exists in the system
    public boolean doesUserExist(String username) {
        return _userRepository.existsByusername(username);
    }

    // function to get a user by username
    public User getUserByUsername(String username) throws StockMarketException {
        if (username == null)
            throw new UserException("Username is null.");
        if (!doesUserExist(username))
            throw new UserException(String.format("Username %s does not exist.", username));
        return _userRepository.findByusername(username);
    }

    // function to check if the credentials are correct
    @Transactional
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
            User user = new User();
            user.setUserName(userDto.username);
            user.setPassword(userDto.password);
            user.setEmail(userDto.email);
            user.setBirthDate(userDto.birthDate);
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setOrderRepository(_orderRepository);
            shoppingCart.setUser(user);
            user.setShoppingCart(shoppingCart);
            _userRepository.save(user);
        } else {
            throw new StockMarketException("Username already exists.");
        }
    }

    // function to add an order to a user
    @Transactional
    public void addOrderToUser(String username, Order order) throws StockMarketException {
        User user = getUserByUsername(username);
        user.addOrder(order);
        _userRepository.save(user);
    }

    // function that check if a given user is an admin
    @Transactional
    public boolean isAdmin(String userName) throws StockMarketException {
        User user = getUserByUsername(userName);
        return user.isAdmin();
    }

    // function to check if a given user is a guest
    @Transactional
    private boolean isGuestExists(String id) {
        return _guestRepository.existsByGuestId(id);
    }

    // function to add a new guest to the system
    @Transactional
    public void addNewGuest(String id) {
        if (isGuestExists(id)) {
            throw new IllegalArgumentException("Guest with ID " + id + " already exists.");
        }
        _guestRepository.save(new Guest(id));
    }

    // function to remove a guest from the system
    @Transactional
    public void removeGuest(String id) {
        if (!isGuestExists(id)) {
            throw new IllegalArgumentException("Guest with ID " + id + " does not exist.");
        }
        _guestRepository.deleteByGuestId(String.valueOf(id));
    }

    // function to get all the registered users
    @Transactional
    public List<User> get_registeredUsers() {
        return _userRepository.findAll();
    }

    // function to return the purchase history for the user
    @Transactional
    public List<Order> getPurchaseHistory(String username) throws StockMarketException {
        User user = getUserByUsername(username);
        if (user != null) {
            return user.getPurchaseHistory();
        } else {
            throw new UserException("User not found.");
        }
    }

    // change email for a user
    @Transactional
    public void changeEmail(String username, String email) throws StockMarketException {
        User user = getUserByUsername(username);
        if (email == null || email.isEmpty()) {
            throw new UserException("Email is empty.");
        }
        if (!_EmailValidator.isValidEmail(email)) {
            throw new UserException("Email is not valid.");
        }
        user.setEmail(email);
        _userRepository.save(user);
    }

    // getting the user personal details
    @Transactional
    public UserDto getUserDetails(String username) {
        User user = _userRepository.findByusername(username);
        return new UserDto(user.getUserName(), user.getPassword(), user.getEmail(), user.getBirthDate());
    }

    // set the user personal new details
    @Transactional
    public UserDto setUserDetails(String username, UserDto userDto) throws StockMarketException {
        if (userDto.username == null || userDto.username.isEmpty()) {
            throw new StockMarketException("new UserName is empty.");
        }
        if (userDto.email == null || userDto.email.isEmpty()) {
            throw new StockMarketException("new Email is empty.");
        }
        if (!_EmailValidator.isValidEmail(userDto.email)) {
            throw new StockMarketException("new Email is not valid.");
        }
        User user = getUserByUsername(username);

        if (user == null) {
            throw new StockMarketException("Username already exists.");
        } else {
            user.setEmail(userDto.email);
            user.setBirthDate(userDto.birthDate);
            _userRepository.save(user);
        }
        return new UserDto(user.getUserName(), user.getPassword(), user.getEmail(), user.getBirthDate());
    }

    // function to notify a user when an alert is triggered
    @Transactional
    public boolean notifyUser(String targetUser, Alert alert) {
        try {
            _notificationHandler.sendMessage(targetUser, alert);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<OrderDto> viewOrderHistory(String username) throws StockMarketException {
        User user = getUserByUsername(username);
        List<Order> orders = user.getPurchaseHistory();
        List<OrderDto> orderDtos = new ArrayList<>();
        for (Order order : orders) {
            orderDtos.add(new OrderDto(order));
        }
        return orderDtos;
    }

    public void reportToAdmin(String user, String message) {
        Alert alert = new IntegrityRuleBreakAlert(user, message);
        String targetUser = alert.getTargetUser();
       notifyUser(targetUser, alert);
    }

    // get guest oblect by guest id from the repository
    public Guest getGuestById(String id) {
        return _guestRepository.findByGuestId(id);
    }

    // // // function to initilaize data for UI testing
    // public void initUI() {
    //     _userRepository.addUser(new User("tal", 
    //             this._passwordEncoder.encodePassword("taltul"), "tal@gmail.com", new Date()));
    //     _userRepository.addUser(new User("vladik", 
    //             this._passwordEncoder.encodePassword("123456"), "vladik@gmail.com", new Date()));
    //     _userRepository.addUser(new User("v", 
    //             this._passwordEncoder.encodePassword("123456"), "v@gmail.com", new Date()));
    //     _userRepository.addUser(new User("test", 
    //             this._passwordEncoder.encodePassword("123456"), "v@gmail.com", new Date()));
    //     _userRepository.addUser(new User("metar", 
    //             this._passwordEncoder.encodePassword("123456"), "v@gmail.com", new Date()));
    // }

}