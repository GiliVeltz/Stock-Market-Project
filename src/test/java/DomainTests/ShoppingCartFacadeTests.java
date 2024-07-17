package DomainTests;

import Domain.Facades.ShopFacade;
import Domain.Facades.ShoppingCartFacade;
import Domain.Facades.UserFacade;
import Domain.Authenticators.PasswordEncoderUtil;
import Domain.Entities.Guest;
import Domain.Entities.Order;
import Domain.Entities.Product;
import Domain.Entities.Shop;
import Domain.Entities.ShoppingBasket;
import Domain.Entities.ShoppingCart;
import Domain.Entities.User;
import Domain.Entities.enums.Category;
import Domain.ExternalServices.PaymentService.AdapterPaymentImp;
import Domain.ExternalServices.SupplyService.AdapterSupplyImp;
import Domain.Repositories.*;
import Exceptions.StockMarketException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private DbShoppingCartRepository _cartsRepoMock;

    @Mock
    private ShopFacade _shopFacadeMock;

    @Mock
    private UserFacade _userFacadeMock;

    @Mock
    private AdapterPaymentImp _AdapterPaymentMock;

    @Mock
    private AdapterSupplyImp _AdapterSupplyMock;

    @Mock
    private ShoppingCart _cartMock;

    @Mock
    private ShoppingBasket _basketMock;

    @Mock
    private HashMap<String, ShoppingBasket> _hashMapMock;

    @Mock
    private Shop _shopMock;

    @Mock
    private Order _orderMock;

    @Mock
    DbShoppingCartRepository shoppingCartRepositoryMock;
    
    @Mock
    DbOrderRepository orderRepositoryMock;
    
    @Mock
    DbGuestRepository guestRepositoryMock;
    
    @Mock
    DbUserRepository userRepositoryMock;
    
    @Mock
    DbShoppingBasketRepository basketRepositoryMock;

    @Mock
    DbShopOrderRepository shopOrderRepositoryMock;

    private ShoppingCartFacade shoppingCartFacadeUnderTest;

    @BeforeEach
    public void setUp() throws StockMarketException {
        MockitoAnnotations.openMocks(this);
        _guestsCarts = new HashMap<>();
        shoppingCartFacadeUnderTest = new ShoppingCartFacade(shoppingCartRepositoryMock, orderRepositoryMock, guestRepositoryMock, userRepositoryMock, basketRepositoryMock, shopOrderRepositoryMock, _userFacadeMock, _shopFacadeMock);
        shoppingCartFacadeUnderTest.setShoppingCartFacadeRepositories(new MemoryShoppingCartRepository(), new MemoryOrderRepository(), new MemoryGuestRepository(), new MemoryUserRepository(), new MemoryShoppingBasketRepository(), new MemoryShopOrderRepository());  
    }

    @AfterEach
    public void tearDown() {
        _guestsCarts.clear();
    }

    @Test
    public void testAddCartForGuest_whenGuestID_thenAddCart() {
        // Arrange
        String guestID = "guestID";
        Guest guest = new Guest("guestID");

        shoppingCartFacadeUnderTest = new ShoppingCartFacade(shoppingCartRepositoryMock, orderRepositoryMock, guestRepositoryMock, userRepositoryMock, basketRepositoryMock, shopOrderRepositoryMock, _userFacadeMock, _shopFacadeMock);
        shoppingCartFacadeUnderTest.setShoppingCartFacadeRepositories(new MemoryShoppingCartRepository(), new MemoryOrderRepository(), new MemoryGuestRepository(new ArrayList<>() {
            {
                add(guest);
            }
        }), new MemoryUserRepository(), new MemoryShoppingBasketRepository(), new MemoryShopOrderRepository());

        when(_userFacadeMock.getGuestById(guestID)).thenReturn(guest);
        
        // Act
        try {
            shoppingCartFacadeUnderTest.addCartForGuest(guestID);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception thrown when not expected: " + e.getMessage());
        }

        // Assert
        assertTrue(shoppingCartFacadeUnderTest.getCartByUsernameOrToken(guestID) != null);
    }

    @Test
    public void testAddCartForGuest_whenGuestID_thenAddCartFail() {
        // Arrange
        String guestID = "guestID";
        Guest guest = new Guest("guestID");

        shoppingCartFacadeUnderTest = new ShoppingCartFacade(shoppingCartRepositoryMock, orderRepositoryMock, guestRepositoryMock, userRepositoryMock, basketRepositoryMock, shopOrderRepositoryMock, _userFacadeMock, _shopFacadeMock);
        shoppingCartFacadeUnderTest.setShoppingCartFacadeRepositories(new MemoryShoppingCartRepository(), new MemoryOrderRepository(), new MemoryGuestRepository(new ArrayList<>() {
            {
                add(guest);
            }
        }), new MemoryUserRepository(), new MemoryShoppingBasketRepository(), new MemoryShopOrderRepository());

        when(_userFacadeMock.getGuestById(guestID)).thenReturn(guest);
        
        // Act
        try {
            shoppingCartFacadeUnderTest.addCartForGuest(guestID);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception thrown when not expected: " + e.getMessage());
        }

        // Assert
        assertFalse(shoppingCartFacadeUnderTest.get_guestsCarts().containsKey("guestIDFail"));
    }

    @Test
    public void testAddCartForUser_whenGuestIDAndUser_thenAddCart() {
        // Arrange
        String guestID = "guestID";
        Guest guest = new Guest("guestID");

        shoppingCartFacadeUnderTest = new ShoppingCartFacade(shoppingCartRepositoryMock, orderRepositoryMock, guestRepositoryMock, userRepositoryMock, basketRepositoryMock, shopOrderRepositoryMock, _userFacadeMock, _shopFacadeMock);
        shoppingCartFacadeUnderTest.setShoppingCartFacadeRepositories(new MemoryShoppingCartRepository(), new MemoryOrderRepository(), new MemoryGuestRepository(new ArrayList<>() {
            {
                add(guest);
            }
        }), new MemoryUserRepository(), new MemoryShoppingBasketRepository(), new MemoryShopOrderRepository());
        
        User user = new User("username", "password", "email@email.com", new Date());

        when(_userFacadeMock.getGuestById(guestID)).thenReturn(guest);
        
        // Act
        try {
            shoppingCartFacadeUnderTest.addCartForGuest(guestID);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception thrown when not expected: " + e.getMessage());
        }
        shoppingCartFacadeUnderTest.addCartForUser(guestID, user);

        // Assert
        assertTrue(shoppingCartFacadeUnderTest.getCartByUsernameOrToken(guestID) != null);
    }

    @Test
    public void testAddCartForUser_whenGuestIDAndUser_thenAddCartFail() {
        // Arrange
        String guestID = "guestID";
        Guest guest = new Guest("guestID");

        shoppingCartFacadeUnderTest = new ShoppingCartFacade(shoppingCartRepositoryMock, orderRepositoryMock, guestRepositoryMock, userRepositoryMock, basketRepositoryMock, shopOrderRepositoryMock, _userFacadeMock, _shopFacadeMock);
        shoppingCartFacadeUnderTest.setShoppingCartFacadeRepositories(new MemoryShoppingCartRepository(), new MemoryOrderRepository(), new MemoryGuestRepository(new ArrayList<>() {
            {
                add(guest);
            }
        }), new MemoryUserRepository(), new MemoryShoppingBasketRepository(), new MemoryShopOrderRepository());

        User user = new User("username", "password", "email", new Date());

        when(_userFacadeMock.getGuestById(guestID)).thenReturn(guest);

        // Act
        try {
            shoppingCartFacadeUnderTest.addCartForGuest(guestID);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception thrown when not expected: " + e.getMessage());
        }
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

        shoppingCartFacadeUnderTest = new ShoppingCartFacade(shoppingCartRepositoryMock, orderRepositoryMock, guestRepositoryMock, userRepositoryMock, basketRepositoryMock, shopOrderRepositoryMock, _userFacadeMock, _shopFacadeMock);
        
        when(shoppingCartRepositoryMock.getCartByUsername(userName)).thenReturn(_cartMock);
        doNothing().when(_cartMock).addProduct(productID, shopID, 1);

        // Act & Assert
        try {
            shoppingCartFacadeUnderTest.addProductToUserCart(userName, productID, shopID, 1);
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
        Shop shop = new Shop("shopName", "shopDescription", "shopAddress", "shopOwner", 1);
        Product product = new Product("productName", Category.BOOKS, 10.0, shop, productID);
        when(_cartsRepoMock.getCartByUsername(userName)).thenReturn(_cartMock);
        doNothing().when(_cartMock).removeProduct(product, shopID, 1);

        // Act & Assert
        try {
            shoppingCartFacadeUnderTest.removeProductFromUserCart(userName, productID, shopID, 1);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Exception thrown when not expected: " + e.getMessage());
        }
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testWriteReview_whenUserHasPurchasedProduct_thenWriteReview() throws StockMarketException {
        // Arrange
        String username = "username";
        int productID = 1;
        int shopID = 1;
        String review = "review";
        when(_cartsRepoMock.getCartByUsername(username)).thenReturn(_cartMock);
        when(_cartMock.getPurchases()).thenReturn(_hashMapMock);
        when(_hashMapMock.containsKey(productID)).thenReturn(true);
        when(_hashMapMock.get(productID)).thenReturn(_basketMock);
        when(_basketMock.getShopId()).thenReturn(shopID);
        when(_basketMock.getShop()).thenReturn(_shopMock);
        doNothing().when(_shopMock).addReview(username, productID, review);

        List<Order> purchaseHistory = List.of(_orderMock);
        Map<Integer ,ShoppingBasket> shoppingBasketMap = new HashMap<>();
        shoppingBasketMap.put(productID, _basketMock);
        when(_orderMock.getProductsByShoppingBasket()).thenReturn(shoppingBasketMap);

        shoppingCartFacadeUnderTest = new ShoppingCartFacade(shoppingCartRepositoryMock, orderRepositoryMock, guestRepositoryMock, userRepositoryMock, basketRepositoryMock, shopOrderRepositoryMock, _userFacadeMock, _shopFacadeMock);

        // Act & Assert
        try {
            shoppingCartFacadeUnderTest.writeReview(username, purchaseHistory, productID, shopID, review);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("testWriteReview_whenUserHasPurchasedProduct_thenWriteReview Exception thrown when not expected: " + e.getMessage());
        }
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testWriteReview_whenShopDoesNotExist_thenWriteReviewFail() {
        // Arrange
        String username = "username";
        int productID = 1;
        int shopID = 1;
        String review = "review";
        when(_cartsRepoMock.getCartByUsername(username)).thenReturn(_cartMock);
        when(_cartMock.getPurchases()).thenReturn(_hashMapMock);
        when(_hashMapMock.containsKey(productID)).thenReturn(true);
        when(_hashMapMock.get(productID)).thenReturn(_basketMock);
        when(_basketMock.getShopId()).thenReturn(2);
        when(_basketMock.getShop()).thenReturn(_shopMock);

        List<Order> purchaseHistory = List.of(_orderMock);
        Map<Integer ,ShoppingBasket> shoppingBasketMap = new HashMap<>();
        shoppingBasketMap.put(productID, _basketMock);
        when(_orderMock.getProductsByShoppingBasket()).thenReturn(shoppingBasketMap);

        shoppingCartFacadeUnderTest = new ShoppingCartFacade(shoppingCartRepositoryMock, orderRepositoryMock, guestRepositoryMock, userRepositoryMock, basketRepositoryMock, shopOrderRepositoryMock, _userFacadeMock, _shopFacadeMock);
        shoppingCartFacadeUnderTest.setShoppingCartFacadeRepositories(new MemoryShoppingCartRepository(), new MemoryOrderRepository(), new MemoryGuestRepository(), new MemoryUserRepository(), new MemoryShoppingBasketRepository(), new MemoryShopOrderRepository());

        // Act & Assert
        assertThrows(StockMarketException.class, () -> {
            shoppingCartFacadeUnderTest.writeReview(username, purchaseHistory, productID, shopID, review);
        });
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testWriteReview_whenUserHasNotPurchasedProduct_thenWriteReviewFail() {
        // Arrange
        String username = "username";
        int productID = 1;
        int shopID = 1;
        String review = "review";
        when(_cartsRepoMock.getCartByUsername(username)).thenReturn(_cartMock);
        when(_cartMock.getPurchases()).thenReturn(_hashMapMock);
        when(_hashMapMock.containsKey(productID)).thenReturn(false);

        List<Order> purchaseHistory = List.of(_orderMock);
        Map<Integer ,ShoppingBasket> shoppingBasketMap = new HashMap<>();
        shoppingBasketMap.put(productID, _basketMock);
        when(_orderMock.getProductsByShoppingBasket()).thenReturn(shoppingBasketMap);


        shoppingCartFacadeUnderTest = new ShoppingCartFacade(shoppingCartRepositoryMock, orderRepositoryMock, guestRepositoryMock, userRepositoryMock, basketRepositoryMock, shopOrderRepositoryMock, _userFacadeMock, _shopFacadeMock);
        shoppingCartFacadeUnderTest.setShoppingCartFacadeRepositories(new MemoryShoppingCartRepository(), new MemoryOrderRepository(), new MemoryGuestRepository(), new MemoryUserRepository(), new MemoryShoppingBasketRepository(), new MemoryShopOrderRepository());

        // Act & Assert
        assertThrows(StockMarketException.class, () -> {
            shoppingCartFacadeUnderTest.writeReview(username, purchaseHistory, productID, shopID, review);
        });
    }
}
