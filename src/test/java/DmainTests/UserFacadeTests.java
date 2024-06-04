package DmainTests;

import org.junit.jupiter.api.*;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Domain.*;
import Domain.Order;
import Domain.Authenticators.PasswordEncoderUtil;
import Domain.Facades.UserFacade;
import Dtos.UserDto;
import Exceptions.ShopException;

public class UserFacadeTests {

    // private fields.
    private UserFacade _userFacadeUnderTest;
    private List<User> _registeredUsers;
    private List<String> _guestIds;

    // mock fields.
    @Mock
    private PasswordEncoderUtil _passwordEncoderMock;

    // users fields.
    private User _user1 = new User("john_doe", "password123", "john.doe@example.com", new Date());

    @BeforeEach
    public void setUp() {
        _passwordEncoderMock = mock(PasswordEncoderUtil.class);
        _registeredUsers = new ArrayList<>();
        _guestIds = new ArrayList<>();
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

        // Act - Set a new username
        try {
            _userFacadeUnderTest
                    .register(new UserDto("john_doe", "password1234", "john.doe@example.co.il", new Date()));
        } catch (Exception e) {
        }

        // Assert - Verify that the username has been updated
        assertThrowsExactly(Exception.class,
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
    public void testGetPurchaseHistory_whenUserExists_thenSuccess() throws Exception {
        // Arrange
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);
        String username = "testUser";

        Shop testShop = new Shop(1, username, "bankDetails", "shopAddress");
        ShoppingBasket shoppingBasket = new ShoppingBasket(testShop);
        List<ShoppingBasket> basketsList = new ArrayList<>();
        basketsList.add(shoppingBasket);
        Order order = new Order(basketsList);

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
    public void testGetPurchaseHistory_whenUserDoesNotExist_thenNull() throws Exception {
        // Arrange
        _userFacadeUnderTest = new UserFacade(_registeredUsers, _guestIds, _passwordEncoderMock);
        String nonExistentUsername = "nonExistentUser";

        // Act
        assertThrows(ShopException.class, () -> _userFacadeUnderTest.getPurchaseHistory(nonExistentUsername));
    }

}