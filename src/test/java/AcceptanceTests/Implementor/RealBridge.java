package AcceptanceTests.Implementor;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;

import Domain.*;
import ServiceLayer.*;

// A real conection to the system.
// The code is tested on the real information on te system.
public class RealBridge implements BridgeInterface{

    private UserService _userService;

    private UserController _userControllerMock;
    private TokenService _tokenServiceMock;
    private ShoppingCartFacade _shoppingCartFacadeMock;
    
    @BeforeEach
    public void setUp() {
        _userControllerMock = mock(UserController.class);
        _tokenServiceMock = mock(TokenService.class);
        _shoppingCartFacadeMock = mock(ShoppingCartFacade.class);
    }

    @Test
    public boolean testRegisterToTheSystem(String username, String password, String email) {
        // Arrange
        String token = "";
        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        _userService = new UserService(_userControllerMock, _tokenServiceMock, _shoppingCartFacadeMock);

        // Act
        Response res = _userService.register(token, username, password, email);

        // Assert
        return res.getErrorMessage() == null;
    }
}
