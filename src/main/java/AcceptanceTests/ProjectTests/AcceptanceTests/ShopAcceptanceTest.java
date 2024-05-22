package AcceptanceTests.ProjectTests.AcceptanceTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.*;
import org.mockito.*;

import ServiceLayer.UserService;
import ServiceLayer.ShopService;
import Domain.ShopController;
import Domain.User;


public class ShopAcceptanceTest {

    @Mock
    ShopService mockShopService;

    @Mock
    UserService mockUserService;

    @Mock
    User mockUser;

    @Mock
    ShopController mockShopController;

    @Mock
    Shop mockShop;

    @Mock
    TokenService mockTokenService;

    // @Mock
    // NotificationService mockNotificationService;
    String token = "token";
    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        _tokenService.validateToken(token)
        when(_tokenService.validateToken(token)).thenReturn(true);
    }

    @AfterEach
    public void cleanup() {
        // Clean up resources or delete the object
    }
    
    @Test
    public void givenSuccessfulAddProductToShop_whenGivenProduct_thenNoExceptionThrown() 
    {
        //TODO: need to implement 
    }
    
    @Test
    public void givenSuccessfulOpenNewShop_whenGivenShopIdAndUserName_thenNoExceptionThrown() throws Exception 
    {
        //TODO: need to implement 
        String userName = "client123";
        Integer shopId = 5555;

        // need to add to user service
        // when(mockUserService.isLoggedIn(userName)).thenReturn(true);
        when(mockShopController.isShopIdExist(shopId)).thenReturn(false);

        // Verify interactions
        verify(mockShopController, times(1)).openNewShop(shopId, userName);


        assertDoesNotThrow(() -> mockShopService.openNewShop(token, shopId, userName));


    }

    @Test
    public void givenUnSuccessfulOpenNewShop_whenGivenInvalidShopId_thenThrownExceptionInvalidShopId() throws Exception 
    {
        //TODO: need to implement 
        // Arrange
        String userName = "client123";
        Integer shopId = 5555;

        // need to add to user service
        // when(mockUserService.isLoggedIn(userName)).thenReturn(true);

        // Define behavior
        when(mockShopController.isShopIdExist(shopId)).thenReturn(true);
        
        // Act and Assert
        IllegalArgumentException exception =  assertThrows(IllegalArgumentException.class,
                () -> mockShopService.openNewShop(token, shopId, userName));

        // Verify interactions
        verify(mockShopController, times(1)).openNewShop(shopId, userName);
        
        // Assert the result
        assertEquals("Failed to create shopID 5555 by user client123. Error: ", exception.getMessage());
    }


    @Test
    public void givenSuccessfulCloseShop_whenGivenShopIdAndUserName_thenNoExceptionThrown() throws Exception 
    {
        //TODO: need to implement 
        String userName = "client123";
        Integer shopId = 5555;

        when(mockShopController.isShopIdExist(shopId)).thenReturn(true);
        when(mockShop.checkPermission(userName,FOUNDER)).thenReturn(true);

        // Verify interactions
        verify(mockShopController, times(1)).closeShop(shopId, userName);

        assertDoesNotThrow(() -> mockShopService.closeShop(token, shopId, userName));
    }

    @Test
    public void givenUnSuccessfulCloseShop_whenGivenShopIdAndUserNameWithoutPermission_thenNoExceptionThrown() throws Exception 
    {
        //TODO: need to implement 
        String userName = "client123";
        Integer shopId = 5555;

        when(mockShopController.isShopIdExist(shopId)).thenReturn(true);
        when(mockShop.checkPermission(userName,FOUNDER)).thenReturn(false);

        // Verify interactions
        verify(mockShopController, times(1)).closeShop(shopId, userName);
        verify(mockShop, times(1)).checkPermission(shopId, FOUNDER);

        IllegalArgumentException exception =  assertThrows(IllegalArgumentException.class,
                () -> mockShopService.closeShop(token, shopId, userName));

        assertEquals("User client123  doesn't have a role in this shop with id 5555 ", exception.getMessage());

    }

}

}

