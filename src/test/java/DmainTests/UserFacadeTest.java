package DmainTests;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import Domain.*;

public class UserFacadeTest {
    private UserController _userFacadeUnderTest;
    private List<User> _registeredUsers;
    private List<String> _guestIds;
    private PasswordEncoderUtil _passwordEncoderMock;
    
    private final User _user1 = new User("john_doe", "password123", "john.doe@example.com");

    /**
     * 
     */
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
        UserController userFacade = new UserController(_registeredUsers, _guestIds, _passwordEncoderMock);

        // Act - try to register a new user
        try {
            _userFacadeUnderTest.register("john_doe", "password123", "john.doe@example.com");
        }
        catch (Exception e) {
            fail("Failed to register user");
        }

        // Assert - Verify that the user was registered successfully
        assertEquals(true, userFacade.isUserNameExists("john_doe"));
    }

    @Test
    public void testRegister_whenExistUser_thenUserNameExistsCheckFail() {
        // Arrange - Create a new User object
        _registeredUsers.add(_user1);
        UserController userFacade = new UserController(_registeredUsers, _guestIds, _passwordEncoderMock);

        // Act - Set a new username
        try {
            _userFacadeUnderTest.register("john_doe", "password1234", "john.doe@example.co.il");
        }
        catch (Exception e) {
        }

        // Assert - Verify that the username has been updated
        assertThrowsExactly(Exception.class, () -> userFacade.register("john_doe", "password1234", "john.doe@example.co.il"));
        assertEquals(true, userFacade.isUserNameExists("john_doe"));
        assertEquals(1, userFacade.get_registeredUsers().size());
    }
}