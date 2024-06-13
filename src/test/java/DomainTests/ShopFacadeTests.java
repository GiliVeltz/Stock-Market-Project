package DomainTests;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import Domain.Facades.ShopFacade;
import Domain.Facades.UserFacade;
import Domain.Product;
import Domain.Shop;
import Domain.ShopOrder;
import Domain.ShoppingBasket;
import Domain.User;
import Dtos.ProductDto;
import Dtos.ShopDto;
import Exceptions.StockMarketException;
import ServiceLayer.ShopService;
import ServiceLayer.TokenService;
import enums.Category;

public class ShopFacadeTests {

    // private fields.
    private List<Shop> _shopsList = new ArrayList<>();

    // mock fields.

    @Mock
    private ShoppingBasket _shoppingBasketMock;
    @Mock
    private TokenService _tokenServiceMock;
    @Mock
    private UserFacade _userFacadeMock;

    // Shops fields.
    private Shop _shop1;
    private Shop _shop2;
    private Shop _shop3;
    private ShopDto _shop4;
    private ProductDto _product1dto;
    private ProductDto _product2dto;
    private Product _product2;
    private Product _product3;

    private static final Logger logger = Logger.getLogger(ShopFacade.class.getName());

    @BeforeEach
    public void setUp() throws StockMarketException {
        _shoppingBasketMock = mock(ShoppingBasket.class);
        _tokenServiceMock = mock(TokenService.class);
        _userFacadeMock = mock(UserFacade.class);
        _shop1 = new Shop(1,"shopName1", "founderName1", "bank1", "addresss1");
        _shop2 = new Shop(2, "shopName2", "founderName2", "bank2", "addresss2");
        _shop3 = new Shop(3, "shopName3", "founderName3", "bank3", "addresss3");
        _shop4 = new ShopDto("shopName4", "bank4", "addresss4");
       _product1dto = new ProductDto("name1", Category.CLOTHING, 1.0);
        _product2dto = new ProductDto("name2", Category.CLOTHING, 1.0);
        _product2 = new Product(3,"name2", Category.CLOTHING, 1.0);
        _product3 = new Product(4,"name3", Category.CLOTHING, 80.0);
        try{
            _shop2.addProductToShop("founderName2", _product2);
            _shop3.addProductToShop("founderName3", _product3);
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Failed to add product2. Error: %s", e.getMessage()), e);
        }
    }

    @AfterEach
    public void tearDown() {
        _shopsList.clear();
    }

    @Test
    public void testOpenNewShop_whenShopNew_whenSuccess() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);

        // Act - try to open a new shop with a new ID
        Integer shopId =_ShopFacadeUnderTests.openNewShop("founderName4", _shop4);

        // Assert - Verify that the shop is added to the list
        assertEquals(1, _ShopFacadeUnderTests.getAllShops().size());
        assertEquals(0, _ShopFacadeUnderTests.getAllShops().get(shopId).getShopId());
    }

    @Test
    public void testsCloseShop_whenShopIsOpenAndExist_thenCloseSuccess() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);

        // Act - try to close an existing open shop
        _ShopFacadeUnderTests.closeShop(_shop1.getShopId(), _shop1.getFounderName());

        // Assert - Verify that the shop is closed
        assertTrue(_shop1.isShopClosed());
    }

    @Test
    public void testsCloseShop_whenShopNotExist_thenRaiseError() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);

        // Act - try to close a non-existing shop
        try {
            _ShopFacadeUnderTests.closeShop(3, "founderName3");
            fail("Closing a non-existing shop should raise an error");
        } catch (Exception e) {
            // Assert - Verify that the expected exception is thrown
            assertEquals(0, _shopsList.size());
        }
    }

    @Test
    public void testCloseShop_whenUserDoesNotHavePermission_thenFails() {
        // Arrange
        _shopsList.add(_shop1);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);

        // Act - try to close a shop this no permission
        try {
            _ShopFacadeUnderTests.closeShop(1, "Jane");
            fail("Closing a non-existing shop should raise an error");
        } catch (Exception e) {
            // Assert - Verify that the expected exception is thrown
            assertEquals(1, _shopsList.size());
        }
    }

    @Test
    public void testCloseShop_whenUserHasPermission_thenSuccess() throws StockMarketException {
        // Arrange
        _shopsList.add(_shop1);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);

        // Act - try to close a shop this no permission
        _ShopFacadeUnderTests.closeShop(1, "founderName1");

        // Assert - Verify that the shop is closed
        assertTrue(_shopsList.get(0).isShopClosed());
    }

    @Test
    public void testsAddProductToShop_whenShopExist_thenAddProductSuccess() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);

        // Act - try to add a product to an existing shop
        _ShopFacadeUnderTests.addProductToShop(_shop1.getShopId(), _product1dto, _shop1.getFounderName());

        // Assert - Verify that the product is added to the shop
        assertEquals(1, _shopsList.size());
        assertEquals(1, _shopsList.get(0).getShopProducts().size());
        assertEquals(_product1dto._productName, _shop1.getShopProducts().get(0).getProductName());
    }

    @Test
    public void testsAddProductToShop_whenShopNotExist_thenRaiseError() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);

        // Act - try to add a product to a non-existing shop
        try {
            _ShopFacadeUnderTests.addProductToShop(3, _product1dto, "username1");
            fail("Adding a product to a non-existing shop should raise an error");
        } catch (Exception e) {
            // Assert - Verify that the expected exception is thrown
            assertEquals(0, _shopsList.size());
        }
    }

    @Test
    public void testsAddProductToShop_whenShopProductExist_thenRaiseError() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);

        // Act - try to add a product to a non-existing shop
        assertThrows(StockMarketException.class, () -> {
            _ShopFacadeUnderTests.addProductToShop(2, _product2dto, "founderName2");});
    }

    @Test
    public void testsAddProductToShop_whenShopProductsAddingInParallel_thenSuccess() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        ExecutorService executor = Executors.newFixedThreadPool(2); // create a thread pool with 2 threads
        _shopsList.add(_shop1);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        
        // Task for first thread
        Runnable task1 = () -> {
            try {
                _ShopFacadeUnderTests.addProductToShop(_shop1.getShopId(), _product1dto, _shop1.getFounderName());
            } catch (StockMarketException e) {
                fail(e.getMessage());
            }
        };

        // Task for second thread
        Runnable task2 = () -> {
            try {
                _ShopFacadeUnderTests.addProductToShop(_shop1.getShopId(), _product2dto, _shop1.getFounderName());
            } catch (StockMarketException e) {
                fail(e.getMessage());
            }
        };

        // Execute tasks
        executor.submit(task1);
        executor.submit(task2);

        try {
            // Wait a while for existing tasks to terminate
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Cancel currently executing tasks

                // Wait a while for tasks to respond to being cancelled
                if (!executor.awaitTermination(10, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executor.shutdownNow();

            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
        
        executor.shutdown(); // shut down executor service
        Map<Integer, Product> products = _shop1.getAllProducts();
        assertEquals(2, products.size());
        assertNotEquals(products.get(0).getProductId(), products.get(1).getProductId());
    }

    @Test
    public void testsOpenNewShop_whenShopsAddingInParallel_thenSuccess() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        ExecutorService executor = Executors.newFixedThreadPool(2); // create a thread pool with 2 threads
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        ShopDto _shopDto1 = new ShopDto("shopName1" ,"bank1", "addresss1");
        ShopDto _shopDto2 = new ShopDto("shopName2", "bank2", "addresss2");
        CountDownLatch latch = new CountDownLatch(2);
        AtomicBoolean exceptionCaught = new AtomicBoolean(true);


        // Task for first thread
        Runnable task1 = () -> {
            try {
                _ShopFacadeUnderTests.openNewShop("Hozier",_shopDto1);
            } catch (StockMarketException e) {
                exceptionCaught.set(false);
            }finally {
                latch.countDown();
            }
        };

        // Task for second thread
        Runnable task2 = () -> {
            try {
                _ShopFacadeUnderTests.openNewShop("KALEO",_shopDto2);
            } catch (StockMarketException e) {
                exceptionCaught.set(false);
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
            fail("Error should raise");
        }

        List<Shop> shops = _ShopFacadeUnderTests.getAllShops();
        assertEquals(2, shops.size());
        assertNotEquals(shops.get(0).getShopId(),shops.get(1).getShopId());
    }

    @Test
    public void testsAddProductToShop_whenUserDoesNotHavePermission_thenFails() {
        // Arrange
        _shopsList.add(_shop1);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);

        // Act - try to add a product to a shop this no permission
        try {
            _ShopFacadeUnderTests.addProductToShop(1, _product1dto, "Jane");
            fail("Adding a product to a shop without permission should raise an error");
        } catch (Exception e) {
            // Assert - Verify that the expected exception is thrown
            assertEquals(1, _shopsList.size());
            assertEquals(0, _shopsList.get(0).getShopProducts().size());
        }
    }

    @Test
    public void testGetProductInShopByCategory_whenShopIdIsNull_thenSearchInAllShops() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        _shopsList.add(_shop2);
        _shopsList.add(_shop3);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = null;
        Category productCategory = Category.CLOTHING;

        // Act - try to get products by category when shopId is null
        Map<Integer, List<Product>> productsByShop = _ShopFacadeUnderTests.getProductInShopByCategory(shopId,
                productCategory);

        // Assert - Verify that the products are retrieved from all shops
        assertEquals(2, productsByShop.size()); 
        assertTrue(productsByShop.containsKey(_shop2.getShopId()));
        assertTrue(productsByShop.containsKey(_shop3.getShopId()));
        assertEquals(1, productsByShop.get(_shop2.getShopId()).size());
        assertEquals(1, productsByShop.get(_shop3.getShopId()).size());
    }

    @Test
    public void testGetProductInShopByCategory_whenShopIdIsValid_thenSearchInSpecificShop() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        _shopsList.add(_shop2);
        _shopsList.add(_shop3);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 2;
        Category productCategory = Category.CLOTHING;

        // Act - try to get products by category when shopId is valid
        Map<Integer, List<Product>> productsByShop = _ShopFacadeUnderTests.getProductInShopByCategory(shopId,
                productCategory);

        // Assert - Verify that the products are retrieved from the specific shop
        assertEquals(1, productsByShop.size());
        assertTrue(productsByShop.containsKey(_shop2.getShopId()));
        assertEquals(1, productsByShop.get(_shop2.getShopId()).size());
    }

    @Test
    public void testGetProductInShopByCategory_whenShopIdIsInvalid_thenRaiseError() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        _shopsList.add(_shop2);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 5;
        Category productCategory = Category.CLOTHING;

        // Act - try to get products by category when shopId is invalid
        try {
            _ShopFacadeUnderTests.getProductInShopByCategory(shopId, productCategory);
            fail("Getting products by category from an invalid shop should raise an error");
        } catch (Exception e) {
            // Assert - Verify that the expected exception is thrown
            assertEquals(0, e.getMessage().indexOf("Shop ID:"));
        }
    }

    @Test
    public void testGetProductInShopByCategory_whenCategoryIsNull_thenRaiseError() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        _shopsList.add(_shop2);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 1;
        Category productCategory = Category.DEFAULT_VAL;

        // Act - try to get products by category when category is null
        try {
            _ShopFacadeUnderTests.getProductInShopByCategory(shopId, productCategory);
            fail("Getting products by category with a null category should raise an error");
        } catch (Exception e) {
            // Assert - Verify that the expected exception is thrown
            assertEquals(-1, e.getMessage().indexOf("Category:"));
        }
    }


    @Test
    public void testGetProductInShopByCategory_whenCategoryIsValidButNoProducts_thenSuccess() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 1;
        Category productCategory = Category.CLOTHING;

        // Act - try to get products by category when category is valid
        Map<Integer, List<Product>> productsByShop = _ShopFacadeUnderTests.getProductInShopByCategory(shopId,
                productCategory);

        // Assert - Verify that the products are retrieved from the specific shop
        assertEquals(0, productsByShop.size());
    }


    @Test
    public void testGetProductsInShopByKeywords_whenShopIdIsValid_thenSearchInSpecificShop() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        _shopsList.add(_shop2);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 2;
        List<String> keywords = new ArrayList<>();
        keywords.add("name2");

        // Act - try to get products by keywords when shopId is valid
        Map<Integer, List<Product>> productsByShop = _ShopFacadeUnderTests.getProductsInShopByKeywords(shopId,
                keywords);

        // Assert - Verify that the products are retrieved from the specific shop
        assertEquals(1, productsByShop.size());
        assertTrue(productsByShop.containsKey(_shop2.getShopId()));
        assertEquals(1, productsByShop.get(_shop2.getShopId()).size());
    }


    @Test
    public void testGetProductsInShopByKeywords_whenShopIdIsInvalid_thenRaiseError() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        _shopsList.add(_shop2);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 5;
        List<String> keywords = new ArrayList<>();
        keywords.add("name2");

        // Act - try to get products by keywords when shopId is invalid
        try {
            _ShopFacadeUnderTests.getProductsInShopByKeywords(shopId, keywords);
            fail("Getting products by keywords from an invalid shop should raise an error");
        } catch (Exception e) {
            // Assert - Verify that the expected exception is thrown
            assertEquals(0, e.getMessage().indexOf("Shop ID:"));
        }
    }

    @Test
    public void testGetProductsInShopByKeywords_whenKeywordsIsNull_thenRaiseError() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        _shopsList.add(_shop2);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 2;
        List<String> keywords = null;

        // Act - try to get products by keywords when keywords is null
        try {
            _ShopFacadeUnderTests.getProductsInShopByKeywords(shopId, keywords);
            fail("Getting products by keywords with a null keywords list should raise an error");
        } catch (Exception e) {
            // Assert - Verify that the expected exception is thrown
            assertEquals("Product keywords is null or empty.", e.getMessage());
        }
    }

    @Test
    public void testGetProductsInShopByKeywords_whenKeywordsIsEmpty_thenRaiseError() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        _shopsList.add(_shop2);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 1;
        List<String> keywords = new ArrayList<>();

        // Act - try to get products by keywords when keywords is empty
        try {
            _ShopFacadeUnderTests.getProductsInShopByKeywords(shopId, keywords);
            fail("Getting products by keywords with an empty keywords list should raise an error");
        } catch (Exception e) {
            // Assert - Verify that the expected exception is thrown
            assertEquals("Product keywords is null or empty.", e.getMessage());
        }
    }

    @Test
    public void testGetProductsInShopByKeywords_whenShopIdIsNullAndKeywordsAreValid_thenSearchInAllShopsSuccess() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        _shopsList.add(_shop2);
        _shopsList.add(_shop3);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = null;
        List<String> keywords = new ArrayList<>();
        keywords.add("clothing");

        // Act - try to get products by keywords when keywords are valid
        Map<Integer, List<Product>> productsByShop = _ShopFacadeUnderTests.getProductsInShopByKeywords(shopId,
                keywords);

        // Assert - Verify that the products are retrieved from the specific shop
        assertEquals(2, productsByShop.size());
        assertTrue(productsByShop.containsKey(_shop2.getShopId()));
        assertTrue(productsByShop.containsKey(_shop3.getShopId()));
        assertEquals(1, productsByShop.get(_shop2.getShopId()).size());
        assertEquals(1, productsByShop.get(_shop3.getShopId()).size());
    }

    @Test
    public void testAdminGetPurchaseHistory_whenUserNotAdmin_thenFails() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 1;
        String userName = "not_admin";
        String token = "Admin_Token";

        ShopService shopService = new ShopService(_ShopFacadeUnderTests, _tokenServiceMock, _userFacadeMock);
        when(_tokenServiceMock.extractUsername(token)).thenReturn(userName);
        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);

        // when check if user is admin retuen false
        when(_userFacadeMock.isAdmin(userName)).thenReturn(false);

        ShoppingBasket shoppingBasket = new ShoppingBasket(_shop1);
        ShopOrder shopOrder = new ShopOrder(1, shopId, shoppingBasket);

        // Act - try to get the purchase history for the shop owner
        _shop1.addOrderToOrderHistory(shopOrder);
        // List<ShopOrder> purchaseHistory =
        // _ShopFacadeUnderTests.getPurchaseHistory(shopId);

        // Assert - Verify that the purchase history is not retrieved
        assertNotNull(shopService.getShopPurchaseHistory(token, shopId).getErrorMessage());

    }

    @Test
    public void testGetPurchaseHistory_whenUserIsOwner_thenSuccess() throws StockMarketException {
        // Arrange - initialize a ShopFacade with a shop that contains a product, and a
        // ShopOrder placed by a user. Configure the token and user services to
        // authenticate and authorize the user as an admin.
        _shopsList.add(_shop1);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 1;
        String userName = "founderName1";
        String token = "owner_Token";
        User user = new User("founderName1", "password1", "email@example.com", new Date());
        Category category = Category.CLOTHING;
        ShoppingBasket shoppingBasket = new ShoppingBasket(_shop1);
        ShopService shopService = new ShopService(_ShopFacadeUnderTests, _tokenServiceMock, _userFacadeMock);
        Product product = new Product(1, "product1", category, 10);
        _shop1.addProductToShop("founderName1", product);
        shoppingBasket.addProductToShoppingBasket(user, product.getProductId());
        ShopOrder shopOrder = new ShopOrder(1, shopId, shoppingBasket);
        _shop1.addOrderToOrderHistory(shopOrder);
        when(_tokenServiceMock.extractUsername(token)).thenReturn(userName);
        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);
        when(_userFacadeMock.isAdmin(userName)).thenReturn(false);

        // Act - try to get the purchase history for the system admin
        Object result = shopService.getShopPurchaseHistory(token, shopId).getReturnValue();

        // Assert - Verify that the purchase history is not retrieved
        assertNotNull(result);
    }

    @Test
    public void testGetPurchaseHistory_whenUserNotAdminAndNotOwner_thenFails() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 1;
        String userName = "not_admin_or_owner";
        String token = "Admin_Token";
        User user = new User(userName, "password1", "email@example.com", new Date());
        Category category = Category.CLOTHING;
        ShoppingBasket shoppingBasket = new ShoppingBasket(_shop1);
        ShopService shopService = new ShopService(_ShopFacadeUnderTests, _tokenServiceMock, _userFacadeMock);
        Product product = new Product(1, "product1", category, 10);
        _shop1.addProductToShop("founderName1", product);
        shoppingBasket.addProductToShoppingBasket(user, product.getProductId());
        ShopOrder shopOrder = new ShopOrder(1, shopId, shoppingBasket);
        _shop1.addOrderToOrderHistory(shopOrder);
        when(_tokenServiceMock.extractUsername(token)).thenReturn(userName);
        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);
        when(_userFacadeMock.isAdmin(userName)).thenReturn(true);

        // Act - try to get the purchase history for the shop owner
        Object result = shopService.getShopPurchaseHistory(token, shopId).getErrorMessage();

        // Assert - Verify that the purchase history is not retrieved
        assertNotNull(result);
    }

    @Test
    public void testGetPurchaseHistory_whenProductPriceChanges_thenOrderTotalRemainsUnchanged() throws StockMarketException {
        // Arrange
        // Create a new ShopFacade object with a shop that has a product. A user places
        // an order for this product.
        _shopsList.add(_shop1);
        ShopFacade shopFacadeUnderTest = new ShopFacade(_shopsList);
        Integer shopId = 1;
        User user = new User("founderName1", "password1", "email@example.com", new Date());
        Category category = Category.CLOTHING;
        ShoppingBasket shoppingBasket = new ShoppingBasket(_shop1);
        Product product = new Product(1, "product1", category, 10);
        _shop1.addProductToShop("founderName1", product);
        shoppingBasket.addProductToShoppingBasket(user, product.getProductId());
        ShopOrder shopOrder = new ShopOrder(1, shopId, shoppingBasket);
        _shop1.addOrderToOrderHistory(shopOrder);

        // Act
        // Change the price of the product after the order has been placed
        _shop1.setProductPrice(product.getProductId(), 100.0);
        List<ShopOrder> purchaseHistory = shopFacadeUnderTest.getPurchaseHistory(shopId);

        // Assert
        // Verify that the total amount of the order in the purchase history remains
        // unchanged despite the price change
        assertEquals(10, purchaseHistory.get(0).getOrderTotalAmount());
    }

    @Test
    public void testGetPurchaseHistory_whenProductPriceNotChange_thenSuccess() throws StockMarketException {
        // Arrange
        _shopsList.add(_shop1);
        ShopFacade shopFacadeUnderTest = new ShopFacade(_shopsList);
        Integer shopId = 1;
        User testUser = new User("founderName1", "password1", "email1", new Date());
        Category productCategory = Category.CLOTHING;
        ShoppingBasket testShoppingBasket = new ShoppingBasket(_shop1);
        Product testProduct = new Product(1, "product1", productCategory, 10);
        _shop1.addProductToShop("founderName1", testProduct);
        testShoppingBasket.addProductToShoppingBasket(testUser, testProduct.getProductId());

        ShopOrder testShopOrder = new ShopOrder(1, shopId, testShoppingBasket);

        // Act
        _shop1.addOrderToOrderHistory(testShopOrder);
        List<ShopOrder> purchaseHistory = shopFacadeUnderTest.getPurchaseHistory(shopId);

        // Assert
        assertEquals(10, purchaseHistory.get(0).getOrderTotalAmount());
    }

    @Test
    public void testsUpdateProductInShop_whenShopExist_thenUpdateProductSuccess() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop3);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 3;
        String founder = "founderName3";

        assertEquals(0, _product2.getProductQuantity());

        // Act - try to update a product to an existing shop
        _ShopFacadeUnderTests.updateProductQuantity(founder, shopId, _product3.getProductId(), 10);

        // Assert - Verify that the product quantity is updated
        assertEquals(10, _product3.getProductQuantity());
    }

    @Test
    public void testsUpdateProductInShop_whenUserDoesNotHavePermisson_thenRaiseError() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 3;
        String userName = "user1";

        assertEquals(0, _product2.getProductQuantity());

        // Act - try to update a product to an existing shop
        try {
            _ShopFacadeUnderTests.updateProductQuantity(userName, shopId, _product2.getProductId(), 10);
            fail("Update product by user without permission should raise an error");
        } catch (Exception e) {
            // Assert - Verify that the product quantity is updated
            assertEquals(0, _product2.getProductQuantity());
        }
    }

    @Test
    public void testsUpdateProductInShop_whenShopIsClose_thenRaiseError() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 3;
        String userName = "founderName3";
        _shop1.closeShop();

        assertEquals(0, _product2.getProductQuantity());

        // Act - try to update a product to an existing shop
        try {
            _ShopFacadeUnderTests.updateProductQuantity(userName, shopId, _product2.getProductId(), 10);
            fail("Update product when shop is closed should raise an error");
        } catch (Exception e) {
            // Assert - Verify that the product quantity is updated
            assertEquals(0, _product2.getProductQuantity());
        }
    }


    @Test
    public void testAddShopRating_whenShopNotExist_thenRaiseError() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        _shopsList.add(_shop2);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 5;
        Integer rating = 4;

        // Act - try to get products by keywords when shopId is invalid
        try {
            _ShopFacadeUnderTests.addShopRating(shopId, rating);
            fail("Rating a shop that doesnt exist should raise an error");
        } catch (Exception e) {
            // Assert - Verify that the expected exception is thrown
            assertEquals(0, e.getMessage().indexOf("Shop ID:"));
        }
    }
    

    @Test
    public void testAddShopRating_whenAllValid_thenSuccess() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        _shopsList.add(_shop2);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 2;
        Integer rating = 4;

        // Act - try to get products by keywords when shopId is valid
       _ShopFacadeUnderTests.addShopRating(shopId, rating);

        // Assert - Verify that the products are retrieved from the specific shop
        assertEquals(4, _shop2.getShopRating());
    }

    
    @Test
    public void testAddProductRating_whenAllValid_thenSuccess() throws StockMarketException {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        _shopsList.add(_shop2);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 2;
        Integer productId = 3;
        Integer rating = 4;

        // Act - try to get products by keywords when shopId is valid
       _ShopFacadeUnderTests.addProductRating(shopId, productId, rating);

        // Assert - Verify that the products are retrieved from the specific shop
        assertEquals(4, _shop2.getProductRating(productId));


    }

}
