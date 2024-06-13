package DomainTests;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import Domain.Authenticators.PasswordEncoderUtil;
import Domain.Facades.UserFacade;
import Domain.Order;
import Domain.Shop;
import Domain.ShoppingBasket;
import Domain.User;
import Dtos.UserDto;
import Exceptions.StockMarketException;
import Exceptions.UserException;

public class UserFacadeTests {

    // private fields.
    private UserFacade _userFacadeUnderTest;
    private List<User> _registeredUsers;
    private List<String> _guestIds;

    // mock fields.
    @Mock
    private PasswordEncoderUtil _passwordEncoderMock;

    @Mock
    private User _userMock;

    // users fields.
    private User _user1 = new User("john_doe", "password123", "john.doe@example.com", new Date());

    @BeforeEach
    public void setUp() {
        _passwordEncoderMock = mock(PasswordEncoderUtil.class);
        _registeredUsers = new ArrayList<>();
        _guestIds = new ArrayList<>();
        _userMock = mock(User.class);
    }

    @AfterEach
    public void tearDown() {
        _userFacadeUnderTest.get_registeredUsers().clear();
        _registeredUsers.clear();
        _guestIds.clear();
    }

    @Test
    public void testRegister_whenNewUser_thenUserNameExistsCheckSuccess() {
        // Arrange - Create a new UserFacade object
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);
        when(_passwordEncoderMock.encodePassword(anyString())).thenReturn("password123");

        // Act - try to register a new user
        UserDto userDto = new UserDto("john_doe", "password123", "john.doe@example.com", new Date());
        try {
            _userFacadeUnderTest.register(userDto);
        } catch (Exception e) {
            fail("Failed to register user");
        }

        // Assert - Verify that the user was registered successfully
        assertEquals(true, _userFacadeUnderTest.doesUserExist("john_doe"));
    }

    @Test
    public void testRegister_whenExistUser_thenUserNameExistsCheckFail() {
        // Arrange - Create a new User object
        _registeredUsers.add(_user1);
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);
        when(_passwordEncoderMock.encodePassword(anyString())).thenReturn("password123");

        // Assert - Verify that the username has been updated
        assertThrowsExactly(StockMarketException.class,
                () -> _userFacadeUnderTest
                        .register(new UserDto("john_doe", "password1234", "john.doe@example.co.il", new Date())));
        assertEquals(true, _userFacadeUnderTest.doesUserExist("john_doe"));
        assertEquals(1, _userFacadeUnderTest.get_registeredUsers().size());
    }

    @Test
    public void testRegister_whenEmailIsEmpty_thenError() {
        // Arrange - Create a new UserFacade object
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);
        when(_passwordEncoderMock.encodePassword(anyString())).thenReturn("password123");

        // Act - Try to register a new user with an empty email
        try {
            _userFacadeUnderTest.register(new UserDto("john_doe", "password123", "", new Date()));
            fail("Expected an exception to be thrown");
        } catch (Exception e) {
            // Assert - Verify that an exception is thrown and the user is not registered
            assertEquals(0, _userFacadeUnderTest.get_registeredUsers().size());
        }
    }

    @Test
    public void testRegister_whenEmailIsNotValid_thenError() {
        // Arrange - Create a new UserFacade object
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);
        when(_passwordEncoderMock.encodePassword(anyString())).thenReturn("password123");

        // Act - Try to register a new user with an invalid email
        try {
            _userFacadeUnderTest.register(new UserDto("john_doe", "password123", "john.doe", new Date()));
            fail("Expected an exception to be thrown");
        } catch (Exception e) {
            // Assert - Verify that an exception is thrown and the user is not registered
            assertEquals(0, _userFacadeUnderTest.get_registeredUsers().size());
        }
    }

    @Test
    public void testAddNewGuest_whenGuestAlreadyExist_thenReturnError() {
        // Arrange - Create a new UserFacade object
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);
        String guestId = "guest123";
        _guestIds.add(guestId);

        // Act - Try to add a new guest with an existing ID
        try {
            _userFacadeUnderTest.addNewGuest(guestId);
            fail("Expected an exception to be thrown");
        } catch (Exception e) {
            // Assert - Verify that an exception is thrown and the guest is not added
            assertEquals(1, _guestIds.size());
        }
    }

    @Test
    public void testAddNewGuest_whenGuestNotAlreadeyExist_thenGuestAddedToGuestList() {
        // Arrange - Create a new UserFacade object
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);
        String guestId = "guest123";

        // Act - Try to add a new guest with a non-existing ID
        try {
            _userFacadeUnderTest.addNewGuest(guestId);
        } catch (Exception e) {
            fail("Unexpected exception thrown");
        }

        // Assert - Verify that the guest is added to the guest list
        assertTrue(_guestIds.contains(guestId));
    }

    @Test
    public void testRemoveGuest_whenGuestExist_thenRemovedSuccessfuly() {
        // Arrange - Create a new UserFacade object
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);
        String guestId = "guest123";
        _guestIds.add(guestId);

        // Act - Try to remove an existing guest
        try {
            _userFacadeUnderTest.removeGuest(guestId);
        } catch (Exception e) {
            fail("Unexpected exception thrown");
        }

        // Assert - Verify that the guest is removed from the guest list
        assertFalse(_guestIds.contains(guestId));
    }

    @Test
    public void testRemoveGuest_whenGuestNotExist_thenError() {
        // Arrange - Create a new UserFacade object
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);
        String guestId = "guest123";

        // Act - Try to remove a non-existing guest
        try {
            _userFacadeUnderTest.removeGuest(guestId);
            fail("Expected an exception to be thrown");
        } catch (Exception e) {
            // Assert - Verify that an exception is thrown and the guest is not removed
            assertFalse(_guestIds.contains(guestId));
        }
    }

    @Test
    public void testGetPurchaseHistory_whenUserExists_thenSuccess() throws StockMarketException {
        // Arrange
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);
        String username = "testUser";

        Shop testShop = new Shop(1, "shopName", username, "bankDetails", "shopAddress");
        ShoppingBasket shoppingBasket = new ShoppingBasket(testShop);
        List<ShoppingBasket> basketsList = new ArrayList<>();
        basketsList.add(shoppingBasket);
        Order order = new Order(1, basketsList);

        _userFacadeUnderTest.register(new UserDto(username, "password", "email@example.com", new Date()));
        _userFacadeUnderTest.addOrderToUser(username, order);

        // Act
        List<Order> expectedPurchaseHistory = new ArrayList<>();
        expectedPurchaseHistory.add(order);
        List<Order> actualPurchaseHistory = _userFacadeUnderTest.getPurchaseHistory(username);

        // Assert
        assertEquals(expectedPurchaseHistory, actualPurchaseHistory);
    }

    @Test
    public void testGetPurchaseHistory_whenUserDoesNotExist_thenNull() throws StockMarketException {
        // Arrange
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);
        String nonExistentUsername = "nonExistentUser";

        // Act
        assertThrows(UserException.class, () -> _userFacadeUnderTest.getPurchaseHistory(nonExistentUsername));
    }

    @Test
    public void testgetUserByUsername_whenUserIsNull_thenError() throws StockMarketException {
        // Arrange
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);
        String userIsNull = null;

        // Act
        assertThrows(UserException.class, () -> _userFacadeUnderTest.getUserByUsername(userIsNull));
    }

    @Test
    public void testgetUserByUsername_whenUserExist_thenSuccess() throws StockMarketException {
        // Arrange
        _registeredUsers.add(_user1);
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);
       User ansUser = _userFacadeUnderTest.getUserByUsername("john_doe");

        // Act
        assertEquals(ansUser.getUserName(),"john_doe");
    }

    @Test
    public void testgetUserByUsername_whenUserDoesNotExist_thenError() throws StockMarketException {
        // Arrange
        String userTest = "John_lennon";
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);

        // Act
        assertThrows(UserException.class, () -> _userFacadeUnderTest.getUserByUsername(userTest));
    }

    @Test
    public void testAreCredentialsCorrect_whenUserDoesNotExist_thenError() throws StockMarketException {
        // Arrange
        String userTest = "John_lennon";
        String password = "5555";
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);

        // Act
        assertThrows(UserException.class, () -> _userFacadeUnderTest.AreCredentialsCorrect(userTest,password));
    }

    @Test
    public void testAreCredentialsCorrect_whenUserisNull_thenError() throws StockMarketException {
        // Arrange
        String userTest = null;
        String password = "5555";
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);

        // Act
        assertThrows(UserException.class, () -> _userFacadeUnderTest.AreCredentialsCorrect(userTest,password));
    }

    @Test
    public void testAreCredentialsCorrect_whenCredentislIsCorrect_thenSuccess() throws StockMarketException {
        // Arrange
        String userTest = "john_doe";
        String password = "5555";
        _registeredUsers.add(_user1);

        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);
        when(_passwordEncoderMock.matches(password, _user1.getEncodedPassword())).thenReturn(true);

        // Act
        assertTrue( _userFacadeUnderTest.AreCredentialsCorrect(userTest,password));
    }

    @Test
    public void testAddOrderToUser_whenOrderIsNull_thenError() throws StockMarketException {
        // Arrange
        String userTest = "john_doe";
        _registeredUsers.add(_user1);
        Order order = null;

        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);

        // Act
        assertThrows(IllegalArgumentException.class, () -> _userFacadeUnderTest.addOrderToUser(userTest, order));
    }    

    @Test
    public void testIsAdmin_whenUserIsNot_thenError() throws StockMarketException {
        // Arrange
        String userTest = "notAdmin";

        _registeredUsers.add(_userMock);        
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);
        when(_userMock.isAdmin()).thenReturn(false);
        when(_userMock.getUserName()).thenReturn(userTest);

        // Act
        assertThrows(UserException.class, () -> _userFacadeUnderTest.isAdmin(userTest));
    }  

    @Test
    public void testIsAdmin_whenUserIsAdmin_thenSuccess() throws StockMarketException {
        // Arrange
        String userTest = "Admin";

        when(_userMock.getUserName()).thenReturn(userTest);
        when(_userMock.isAdmin()).thenReturn(true);
        _registeredUsers.add(_userMock);     
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);

        // Act
        assertTrue( _userFacadeUnderTest.isAdmin(userTest));
    }  

    @Test
    public void testAddNewGuest_whenGuestIdExist_thenError() throws StockMarketException {
        // Arrange
        String guestId = "guest";

        _guestIds.add(guestId);     
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);

        // Act
        assertThrows(IllegalArgumentException.class, () -> _userFacadeUnderTest.addNewGuest(guestId));
    }  

    @Test
    public void testAddNewGuest_whenGuestIdNotExist_thenSuccess() throws StockMarketException {
        // Arrange
        String guestId = "guest";
   
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);
        _userFacadeUnderTest.addNewGuest(guestId);

        // Act
        assertTrue( _guestIds.contains(guestId));
    }

    @Test
    public void testChangeEmail_whenEmailIsNull_thenError() throws StockMarketException {
        // Arrange
        String userTest = "john_doe";
        String email = null;
        
        _registeredUsers.add(_user1);
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);

        // Act
        assertThrows(UserException.class, () -> _userFacadeUnderTest.changeEmail(userTest,email));
    }

    @Test
    public void testChangeEmail_whenEmailIsEmpty_thenError() throws StockMarketException {
        // Arrange
        String userTest = "john_doe";
        String email = "";
        
        _registeredUsers.add(_user1);
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);

        // Act
        assertThrows(UserException.class, () -> _userFacadeUnderTest.changeEmail(userTest,email));
    }

    @Test
    public void testChangeEmail_whenEmailIsInValid_thenError() throws StockMarketException {
        // Arrange
        String userTest = "john_doe";
        String email = "555555";
        
        _registeredUsers.add(_user1);
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);

        // Act
        assertThrows(UserException.class, () -> _userFacadeUnderTest.changeEmail(userTest,email));
    }

    @Test
    public void testChangeEmail_whenEmailIsCurrect_thenSuccess() throws StockMarketException {
        // Arrange
        String userTest = "john_doe";
        String email = "MyEmail5@gmail.com";
        
        _registeredUsers.add(_user1);
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);
        _userFacadeUnderTest.changeEmail(userTest,email);

        // Act
        User ansUser = _userFacadeUnderTest.getUserByUsername("john_doe");
        assertEquals(email, ansUser.getEmail());
    }
    
    @Test
    public void testsRegisterUser_whenUserRegisterInParallel_thenError() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        ExecutorService executor = Executors.newFixedThreadPool(2); // create a thread pool with 2 threads
        
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);
        UserDto userDto1 = new UserDto("Dudu_Tassa", "password123", "dudu@example.com", new Date());
        UserDto userDto2 = new UserDto("Dudu_Tassa", "password5555", "dudu@example.com", new Date());
        
        CountDownLatch latch = new CountDownLatch(2);
        AtomicBoolean exceptionCaught = new AtomicBoolean(false);

        // Task for first thread
        Runnable task1 = () -> {
            try {
                _userFacadeUnderTest.register(userDto1);
            } catch (StockMarketException e) {
                exceptionCaught.set(true);
            }finally {
                latch.countDown();
            }
        };

        // Task for second thread
        Runnable task2 = () -> {
            try {
                _userFacadeUnderTest.register(userDto2);
            } catch (StockMarketException e) {
                exceptionCaught.set(true);
            }finally {
                latch.countDown();
            }
        };

        // Execute tasks
        executor.submit(task1);
        executor.submit(task2);

        try {
            latch.await(); // wait for both tasks to complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Test interrupted");
        } finally {
            executor.shutdown(); // shut down executor service
        }
        
        if (!exceptionCaught.get()) {
            fail("Error should raise when user register with the same userName");
        }

    }

}