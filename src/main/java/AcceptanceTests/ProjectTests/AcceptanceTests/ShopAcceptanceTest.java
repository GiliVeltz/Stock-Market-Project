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

    // @Mock
    // NotificationService mockNotificationService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        // mockLibrary = Mockito.spy(new Library(mockDatabaseService, mockReviewService));
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
        verify(mockShopController, times(1)).OpenNewShop(shopId, userName);

        assertDoesNotThrow(() -> mockShopService.OpenNewShop(shopId, userName));
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
                () -> mockShopService.OpenNewShop(shopId, userName));

        // Verify interactions
        verify(mockShopController, times(1)).OpenNewShop(shopId, userName);
        
        // Assert the result
        assertEquals("Failed to create shopID 5555 by user client123. Error: ", exception.getMessage());
    }
}