package DmainTests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;



import Domain.ExternalServices.ExternalServiceHandler;
import Domain.Facades.ShoppingCartFacade;
import Domain.Facades.UserFacade;
import Exceptions.ShopException;
import ServiceLayer.Response;
import ServiceLayer.SystemService;
import ServiceLayer.UserService;
import ServiceLayer.TokenService;

public class SystemTest {
    @Mock
    private UserService _userServiceMock;
    @Mock
    private ExternalServiceHandler _externalServiceHandlerMock;
    @Mock
    private TokenService _tokenServiceMock;
    @Mock
    private UserFacade _userFacadeMock;
    @Mock
    private ShoppingCartFacade _shoppingCartFacadeMock;

    private SystemService _systemService;

    @BeforeEach
    public void setUp() {
        _userServiceMock = mock(UserService.class);
        _externalServiceHandlerMock = mock(ExternalServiceHandler.class);
        _tokenServiceMock = mock(TokenService.class);
        _userFacadeMock = mock(UserFacade.class);
        _shoppingCartFacadeMock = mock(ShoppingCartFacade.class);
        _systemService = new SystemService(_userServiceMock, _externalServiceHandlerMock, _tokenServiceMock,
                _userFacadeMock, _shoppingCartFacadeMock);
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testOpenSystem_WhenSystemAlreadyOpen_ReturnsFalse() throws ShopException {
        // Arrange
        String token = "Admin_Token";
        _systemService.setSystemOpen(true);
        when(_tokenServiceMock.extractUsername(token)).thenReturn("Admin");
        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);
        when(_userServiceMock.isSystemAdmin("Admin")).thenReturn(new Response());
        when(_externalServiceHandlerMock.connectToServices()).thenReturn(true);

        // Act
        boolean actual = (_systemService.openSystem(token).getErrorMessage() == null);

        // Assert
        assertFalse(actual);
    }

    @Test
    public void testOpenSystem_WhenUserNotLoggedIn_ReturnsFalse() throws Exception {
        // Arrange
        String token = "Admin_Token";
        when(_tokenServiceMock.extractUsername(token)).thenReturn("Admin");
        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(false);
        when(_userServiceMock.isSystemAdmin("Admin")).thenReturn(new Response());
        when(_externalServiceHandlerMock.connectToServices()).thenReturn(true);

        // Act
        boolean actual = (_systemService.openSystem(token).getErrorMessage() == null);

        // Assert
        assertFalse(actual);
    }

    @Test
    public void testOpenSystem_WhenUserTokenIsNotValid_ReturnsFalse() throws Exception {
        // Arrange
        String token = "Admin_Token";
        when(_tokenServiceMock.extractUsername(token)).thenReturn("Admin");
        when(_tokenServiceMock.validateToken(token)).thenReturn(false);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);
        when(_userServiceMock.isSystemAdmin("Admin")).thenReturn(new Response());
        when(_externalServiceHandlerMock.connectToServices()).thenReturn(true);

        // Act
        boolean actual = (_systemService.openSystem(token).getErrorMessage() == null);

        // Assert
        assertFalse(actual);
    }

    @Test
    public void testOpenSystem_WhenNotConnectedToAllExternalServices_ReturnsFalse() throws Exception {
        // Arrange
        String token = "Admin_Token";
        when(_tokenServiceMock.extractUsername(token)).thenReturn("Admin");
        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);
        when(_userServiceMock.isSystemAdmin("Admin")).thenReturn(new Response());
        when(_externalServiceHandlerMock.connectToServices()).thenReturn(false);

        // Act
        boolean actual = (_systemService.openSystem(token).getErrorMessage() == null);

        // Assert
        assertFalse(actual);
    }

    @Test
    public void testOpenSystem_WhenAllConditionsSatisfied_success() throws Exception {
        // Arrange
        String token = "Admin_Token";
        when(_tokenServiceMock.extractUsername(token)).thenReturn("Admin");
        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);
        when(_userServiceMock.isSystemAdmin("Admin")).thenReturn(new Response());
        when(_externalServiceHandlerMock.connectToServices()).thenReturn(true);

        // Act
        boolean actual = (_systemService.openSystem(token).getErrorMessage() == null);

        // Assert
        assertTrue(actual);
    }

}
