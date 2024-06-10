package DomainTests;

import Domain.Facades.ShopFacade;
import Domain.Facades.ShoppingCartFacade;
import Domain.*;
import Domain.Authenticators.PasswordEncoderUtil;
import Domain.ExternalServices.PaymentService.AdapterPayment;
import Domain.ExternalServices.SupplyService.AdapterSupply;
import Domain.Repositories.ShoppingCartRepositoryInterface;
import Exceptions.StockMarketException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ShoppingCartFacadeTests {

    // private fields in ShoppingCartFacade
    private Map<String, ShoppingCart> _guestsCarts;

    // mocks
    @Mock
    private PasswordEncoderUtil _passwordEncoderMock;
    
    @Mock
    private ShoppingCartRepositoryInterface _cartsRepoMock;

    @Mock
    private ShopFacade _shopFacadeMock;

    @Mock
    private AdapterPayment _AdapterPaymentMock;

    @Mock
    private AdapterSupply _AdapterSupplyMock;

    @Mock
    private ShoppingCart _cartMock;

    private ShoppingCartFacade shoppingCartFacadeUnderTest;

    @BeforeEach
    public void setUp() throws StockMarketException {
        MockitoAnnotations.openMocks(this);
        _guestsCarts = new HashMap<>();
        shoppingCartFacadeUnderTest = new ShoppingCartFacade();
    }

    @AfterEach
    public void tearDown() {
        _guestsCarts.clear();
    }

    @Test
    public void testAddCartForGuest_whenGuestID_thenAddCart() {
        // Arrange
        String guestID = "guestID";

        // Act
        shoppingCartFacadeUnderTest.addCartForGuest(guestID);

        // Assert
        assertTrue(shoppingCartFacadeUnderTest.get_guestsCarts().containsKey(guestID));
    }

    @Test
    public void testAddCartForGuest_whenGuestID_thenAddCartFail() {
        // Arrange
        String guestID = "guestID";

        // Act
        shoppingCartFacadeUnderTest.addCartForGuest(guestID);

        // Assert
        assertFalse(shoppingCartFacadeUnderTest.get_guestsCarts().containsKey("guestIDFail"));
    }

    @Test
    public void testAddCartForUser_whenGuestIDAndUser_thenAddCart() {
        // Arrange
        String guestID = "guestID";
        User user = new User("username", "password", "email@email.com", new Date());

        // Act
        shoppingCartFacadeUnderTest.addCartForGuest(guestID);
        shoppingCartFacadeUnderTest.addCartForUser(guestID, user);

        // Assert
        assertTrue(shoppingCartFacadeUnderTest.get_guestsCarts().containsKey(guestID));
    }

    @Test
    public void testAddCartForUser_whenGuestIDAndUser_thenAddCartFail() {
        // Arrange
        String guestID = "guestID";
        User user = new User("username", "password", "email", new Date());

        // Act
        shoppingCartFacadeUnderTest.addCartForGuest(guestID);
        shoppingCartFacadeUnderTest.addCartForUser(guestID, user);

        // Assert
        assertFalse(shoppingCartFacadeUnderTest.get_guestsCarts().containsKey("guestIDFail"));
    }

    @Test
    public void testAddProductToUserCart_whenSuccessToAddProduct_thenAddProduct() throws StockMarketException {
        // Arrange
        String userName = "username";
        int shopID = 1;
        int productID = 1;
        when(_cartsRepoMock.getCartByUsername(userName)).thenReturn(_cartMock);
        doNothing().when(_cartMock).addProduct(productID, shopID);

        // Act & Assert
        try {
            shoppingCartFacadeUnderTest.addProductToUserCart(userName, productID, shopID);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception thrown when not expected: " + e.getMessage());
        }
    }

    @Test
    public void testRemoveProductFromUserCart_whenSuccessToRemoveProduct_thenRemoveProduct() throws StockMarketException {
        // Arrange
        String userName = "username";
        int shopID = 1;
        int productID = 1;
        when(_cartsRepoMock.getCartByUsername(userName)).thenReturn(_cartMock);
        doNothing().when(_cartMock).removeProduct(productID, shopID);

        // Act & Assert
        try {
            shoppingCartFacadeUnderTest.removeProductFromUserCart(userName, productID, shopID);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception thrown when not expected: " + e.getMessage());
        }
    }
}
