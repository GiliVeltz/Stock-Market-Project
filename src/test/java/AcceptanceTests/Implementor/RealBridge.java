package AcceptanceTests.Implementor;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import Domain.*;
import ServiceLayer.*;

// A real conection to the system.
// The code is tested on the real information on te system.
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RealBridge implements BridgeInterface, ParameterResolver{


    private UserService _userServiceUnderTest;
    @Mock
    private UserController _userControllerMock;
    @Mock
    private TokenService _tokenServiceMock;
    @Mock
    private ShoppingCartFacade _shoppingCartFacadeMock;
    
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == RealBridge.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return new RealBridge();
    }

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
        _tokenServiceMock = mock(TokenService.class); // Initialize the mock object
        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        _userServiceUnderTest = new UserService(_userControllerMock, _tokenServiceMock, _shoppingCartFacadeMock);

        // Act
        Response res = _userServiceUnderTest.register(token, username, password, email);

        // Assert
        return res.getErrorMessage() == null;
    }
}
