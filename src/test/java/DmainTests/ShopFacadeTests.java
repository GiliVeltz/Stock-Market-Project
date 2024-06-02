package DmainTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import Domain.*;
import Domain.Facades.ShopFacade;
import Dtos.ProductDto;
import Exceptions.ShopException;
import ServiceLayer.Response;
import ServiceLayer.ShopService;
import ServiceLayer.TokenService;
import ServiceLayer.UserService;
import ch.qos.logback.core.subst.Token;
import enums.Category;

public class ShopFacadeTests {

    // private fields.
    private List<Shop> _shopsList = new ArrayList<>();

    // mock fields.
    @Mock
    private PasswordEncoderUtil _passwordEncoderMock;

    @Mock
    private ShoppingBasket _shoppingBasketMock;
    @Mock
    private TokenService _tokenServiceMock;
    @Mock
    private UserService _userServiceMock;

    // Shops fields.
    private Shop _shop1;
    private Shop _shop2;
    private Shop _shop3;
    private ProductDto _product1;
    private Product _product2;
    
    private static final Logger logger = Logger.getLogger(ShopFacade.class.getName());


    @BeforeEach
    public void setUp() throws ShopException {
        _passwordEncoderMock = mock(PasswordEncoderUtil.class);
        _shoppingBasketMock = mock(ShoppingBasket.class);
        _tokenServiceMock = mock(TokenService.class);
        _userServiceMock = mock(UserService.class);
        _shop1 = new Shop(1, "founderName1", "bank1", "addresss1");
        _shop2 = new Shop(2, "founderName2", "bank2", "addresss2");
        _shop3 = new Shop(3, "founderName3", "bank3", "addresss3");
        _product1 = new ProductDto("name1", Category.CLOTHING, 1.0);
        _product2 = new Product(3,"name2", Category.CLOTHING, 1.0);
        try{
            _shop3.addProductToShop("founderName3", _product2);
        }
        catch(Exception e)
        {
            logger.log(Level.SEVERE,String.format("Failed to add product2. Error: %s",e.getMessage()),e);
        }
    }

    @AfterEach
    public void tearDown() {
        _shopsList.clear();
    }

    @Test
    public void testOpenNewShop_whenShopNew_whenSuccess() throws Exception {
        // Arrange - Create a new ShopFacade object
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);

        // Act - try to open a new shop with a new ID
        _ShopFacadeUnderTests.openNewShop(_shop2.getFounderName(), _shop2.getBankDetails(),
                _shop2.getShopAddress());

        // Assert - Verify that the shop is added to the list
        assertEquals(1, _ShopFacadeUnderTests.getAllShops().size());
        assertEquals(0, _ShopFacadeUnderTests.getAllShops().get(0).getShopId());
    }

    @Test
    public void testsCloseShop_whenShopIsOpenAndExist_thenCloseSuccess() throws Exception {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);

        // Act - try to close an existing open shop
        _ShopFacadeUnderTests.closeShop(_shop1.getShopId(), _shop1.getFounderName());

        // Assert - Verify that the shop is closed
        assertTrue(_shop1.isShopClosed());
    }

    @Test
    public void testsCloseShop_whenShopNotExist_thenRaiseError() throws Exception {
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
    public void testCloseShop_whenUserHasPermission_thenSuccess() throws Exception {
        // Arrange
        _shopsList.add(_shop1);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);

        // Act - try to close a shop this no permission
        _ShopFacadeUnderTests.closeShop(1, "founderName1");

        // Assert - Verify that the shop is closed
        assertTrue(_shopsList.get(0).isShopClosed());
    }

    @Test
    public void testsAddProductToShop_whenShopExist_thenAddProductSuccess() throws Exception {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);

        // Act - try to add a product to an existing shop
        _ShopFacadeUnderTests.addProductToShop(_shop1.getShopId(), _product1, _shop1.getFounderName());

        // Assert - Verify that the product is added to the shop
        assertEquals(1, _shopsList.size());
        assertEquals(1, _shopsList.get(0).getShopProducts().size());
        assertEquals(_product1._productName, _shop1.getShopProducts().get(0).getProductName());
    }

    @Test
    public void testsAddProductToShop_whenShopNotExist_thenRaiseError() throws Exception {
        // Arrange - Create a new ShopFacade object
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);

        // Act - try to add a product to a non-existing shop
        try {
            _ShopFacadeUnderTests.addProductToShop(3, _product1, "username1");
            fail("Adding a product to a non-existing shop should raise an error");
        } catch (Exception e) {
            // Assert - Verify that the expected exception is thrown
            assertEquals(0, _shopsList.size());
        }
    }

    @Test
    public void testsAddProductToShop_whenUserDoesNotHavePermission_thenFails() {
        // Arrange
        _shopsList.add(_shop1);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);

        // Act - try to add a product to a shop this no permission
        try {
            _ShopFacadeUnderTests.addProductToShop(1, _product1, "Jane");
            fail("Adding a product to a shop without permission should raise an error");
        } catch (Exception e) {
            // Assert - Verify that the expected exception is thrown
            assertEquals(1, _shopsList.size());
            assertEquals(0, _shopsList.get(0).getShopProducts().size());
        }
    }

    // TODO: GILI: check why this test failing, and fix it.
    @Disabled
    @Test
    public void testGetProductInShopByCategory_whenShopIdIsNull_thenSearchInAllShops() throws Exception {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        _shopsList.add(_shop2);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = null;
        Category productCategory = Category.CLOTHING;

        // Act - try to get products by category when shopId is null
        Map<Integer, List<Product>> productsByShop = _ShopFacadeUnderTests.getProductInShopByCategory(shopId,
                productCategory);

        // Assert - Verify that the products are retrieved from all shops
        assertEquals(2, productsByShop.size());
        assertTrue(productsByShop.containsKey(_shop1.getShopId()));
        assertTrue(productsByShop.containsKey(_shop2.getShopId()));
        assertEquals(1, productsByShop.get(_shop1.getShopId()).size());
        assertEquals(1, productsByShop.get(_shop2.getShopId()).size());
    }

    // TODO: GILI: check why this test failing, and fix it.
    @Disabled
    @Test
    public void testGetProductInShopByCategory_whenShopIdIsValid_thenSearchInSpecificShop() throws Exception {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        _shopsList.add(_shop2);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 1;
        Category productCategory = Category.CLOTHING;

        // Act - try to get products by category when shopId is valid
        Map<Integer, List<Product>> productsByShop = _ShopFacadeUnderTests.getProductInShopByCategory(shopId,
                productCategory);

        // Assert - Verify that the products are retrieved from the specific shop
        assertEquals(1, productsByShop.size());
        assertTrue(productsByShop.containsKey(_shop1.getShopId()));
        assertEquals(1, productsByShop.get(_shop1.getShopId()).size());
    }

    @Test
    public void testGetProductInShopByCategory_whenShopIdIsInvalid_thenRaiseError() throws Exception {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        _shopsList.add(_shop2);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 3;
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
    public void testGetProductInShopByCategory_whenCategoryIsNull_thenRaiseError() throws Exception {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        _shopsList.add(_shop2);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 1;
        Category productCategory = null;

        // Act - try to get products by category when category is null
        try {
            _ShopFacadeUnderTests.getProductInShopByCategory(shopId, productCategory);
            fail("Getting products by category with a null category should raise an error");
        } catch (Exception e) {
            // Assert - Verify that the expected exception is thrown
            assertEquals(-1, e.getMessage().indexOf("Category:"));
        }
    }

    // TODO: GILI: check why this test failing, and fix it.
    @Disabled
    @Test
    public void testGetProductInShopByCategory_whenCategoryIsInvalid_thenRaiseError() throws Exception {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        _shopsList.add(_shop2);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 1;
        Category productCategory = Category.GROCERY;

        // Act - try to get products by category when category is invalid
        try {
            _ShopFacadeUnderTests.getProductInShopByCategory(shopId, productCategory);
            fail("Getting products by category with an invalid category should raise an error");
        } catch (Exception e) {
            // Assert - Verify that the expected exception is thrown
            assertEquals(0, e.getMessage().indexOf("Category:"));
        }
    }

    // TODO: GILI: check why this test failing, and fix it.
    @Disabled
    @Test
    public void testGetProductInShopByCategory_whenCategoryIsValid_thenSuccess() throws Exception {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        _shopsList.add(_shop2);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 1;
        Category productCategory = Category.CLOTHING;

        // Act - try to get products by category when category is valid
        Map<Integer, List<Product>> productsByShop = _ShopFacadeUnderTests.getProductInShopByCategory(shopId,
                productCategory);

        // Assert - Verify that the products are retrieved from the specific shop
        assertEquals(1, productsByShop.size());
        assertTrue(productsByShop.containsKey(_shop1.getShopId()));
        assertEquals(1, productsByShop.get(_shop1.getShopId()).size());
    }

    // TODO: GILI: check why this test failing, and fix it.
    @Disabled
    @Test
    public void testGetProductsInShopByKeywords_whenShopIdIsValid_thenSearchInSpecificShop() throws Exception {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        _shopsList.add(_shop2);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 1;
        List<String> keywords = new ArrayList<>();
        keywords.add("name1");

        // Act - try to get products by keywords when shopId is valid
        Map<Integer, List<Product>> productsByShop = _ShopFacadeUnderTests.getProductsInShopByKeywords(shopId,
                keywords);

        // Assert - Verify that the products are retrieved from the specific shop
        assertEquals(1, productsByShop.size());
        assertTrue(productsByShop.containsKey(_shop1.getShopId()));
        assertEquals(1, productsByShop.get(_shop1.getShopId()).size());
    }

    // TODO: GILI: check why this test failing, and fix it.
    @Disabled
    @Test
    public void testGetProductsInShopByKeywords_whenShopIdIsInvalid_thenRaiseError() throws Exception {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        _shopsList.add(_shop2);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 3;
        List<String> keywords = new ArrayList<>();
        keywords.add("name1");

        // Act - try to get products by keywords when shopId is invalid
        try {
            _ShopFacadeUnderTests.getProductsInShopByKeywords(shopId, keywords);
            fail("Getting products by keywords from an invalid shop should raise an error");
        } catch (Exception e) {
            // Assert - Verify that the expected exception is thrown
            assertEquals(0, e.getMessage().indexOf("Shop ID:"));
        }
    }

    // TODO: GILI: check why this test failing, and fix it.
    @Disabled
    @Test
    public void testGetProductsInShopByKeywords_whenKeywordsIsNull_thenRaiseError() throws Exception {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        _shopsList.add(_shop2);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 1;
        List<String> keywords = null;

        // Act - try to get products by keywords when keywords is null
        try {
            _ShopFacadeUnderTests.getProductsInShopByKeywords(shopId, keywords);
            fail("Getting products by keywords with a null keywords list should raise an error");
        } catch (Exception e) {
            // Assert - Verify that the expected exception is thrown
            assertEquals(0, e.getMessage().indexOf("Keywords:"));
        }
    }

    // TODO: GILI: check why this test failing, and fix it.
    @Disabled
    @Test
    public void testGetProductsInShopByKeywords_whenKeywordsIsEmpty_thenRaiseError() throws Exception {
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
            assertEquals(0, e.getMessage().indexOf("Keywords:"));
        }
    }

    // TODO: GILI: check why this test failing, and fix it.
    @Disabled
    @Test
    public void testGetProductsInShopByKeywords_whenKeywordsAreValid_thenSuccess() throws Exception {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        _shopsList.add(_shop2);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 1;
        List<String> keywords = new ArrayList<>();
        keywords.add("name1");

        // Act - try to get products by keywords when keywords are valid
        Map<Integer, List<Product>> productsByShop = _ShopFacadeUnderTests.getProductsInShopByKeywords(shopId,
                keywords);

        // Assert - Verify that the products are retrieved from the specific shop
        assertEquals(1, productsByShop.size());
        assertTrue(productsByShop.containsKey(_shop1.getShopId()));
        assertEquals(1, productsByShop.get(_shop1.getShopId()).size());
    }

    @Test
    public void testAdminGetPurchaseHistory_whenUserNotAdmin_thenFails() throws Exception {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer orderId = 1;
        Integer shopId = 1;
        String userName = "not_admin";
        String token = "Admin_Token";

        ShopService shopService = new ShopService(_ShopFacadeUnderTests, _tokenServiceMock, _userServiceMock);
        when(_tokenServiceMock.extractUsername(token)).thenReturn(userName);
        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);

        // when check if user is admin retuen false
        Response response = new Response();
        response.setErrorMessage("not admin user");
        when(_userServiceMock.isSystemAdmin(userName)).thenReturn(response);

        ShoppingBasket shoppingBasket = new ShoppingBasket(_shop1);
        ShopOrder shopOrder = new ShopOrder(orderId, shopId, shoppingBasket);

        // Act - try to get the purchase history for the shop owner
        _shop1.addOrderToOrderHistory(shopOrder);
        // List<ShopOrder> purchaseHistory =
        // _ShopFacadeUnderTests.getPurchaseHistory(shopId);

        // Assert - Verify that the purchase history is not retrieved
        assertNotNull(shopService.getShopPurchaseHistory(token, shopId).getErrorMessage());

    }

    @Test
    public void testGetPurchaseHistory_whenUserIsOwner_thenSuccess() throws Exception {
        // Arrange - initialize a ShopFacade with a shop that contains a product, and a
        // ShopOrder placed by a user. Configure the token and user services to
        // authenticate and authorize the user as an admin.
        _shopsList.add(_shop1);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer orderId = 1;
        Integer shopId = 1;
        String userName = "founderName1";
        String token = "owner_Token";
        User user = new User("founderName1", "password1", "email1");
        Category category = Category.CLOTHING;
        ShoppingBasket shoppingBasket = new ShoppingBasket(_shop1);
        ShopService shopService = new ShopService(_ShopFacadeUnderTests, _tokenServiceMock, _userServiceMock);
        Product product = new Product(1, "product1", category, 10);
        _shop1.addProductToShop("founderName1", product);
        shoppingBasket.addProductToShoppingBasket(user, product.getProductId());
        ShopOrder shopOrder = new ShopOrder(orderId, shopId, shoppingBasket);
        _shop1.addOrderToOrderHistory(shopOrder);
        when(_tokenServiceMock.extractUsername(token)).thenReturn(userName);
        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);
        Response response = new Response();
        response.setErrorMessage("user not admin");
        when(_userServiceMock.isSystemAdmin(userName)).thenReturn(response);

       

        // Act - try to get the purchase history for the system admin
        Object result = shopService.getShopPurchaseHistory(token, shopId).getReturnValue();

        // Assert - Verify that the purchase history is not retrieved
        assertNotNull(result);
    }

    @Test
    public void testGetPurchaseHistory_whenUserNotAdminAndNotOwner_thenFails() throws Exception {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop1);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer orderId = 1;
        Integer shopId = 1;
        String userName = "not_admin_or_owner";
        String token = "Admin_Token";
        User user = new User(userName, "password1", "email1");
        Category category = Category.CLOTHING;
        ShoppingBasket shoppingBasket = new ShoppingBasket(_shop1);
        ShopService shopService = new ShopService(_ShopFacadeUnderTests, _tokenServiceMock, _userServiceMock);
        Product product = new Product(1, "product1", category, 10);
        _shop1.addProductToShop("founderName1", product);
        shoppingBasket.addProductToShoppingBasket(user, product.getProductId());
        ShopOrder shopOrder = new ShopOrder(orderId, shopId, shoppingBasket);
        _shop1.addOrderToOrderHistory(shopOrder);
        when(_tokenServiceMock.extractUsername(token)).thenReturn(userName);
        when(_tokenServiceMock.validateToken(token)).thenReturn(true);
        when(_tokenServiceMock.isUserAndLoggedIn(token)).thenReturn(true);
        when(_userServiceMock.isSystemAdmin(userName)).thenReturn(new Response());

        // Act - try to get the purchase history for the shop owner
        Object result = shopService.getShopPurchaseHistory(token, shopId).getErrorMessage();

        // Assert - Verify that the purchase history is not retrieved
        assertNotNull(result);
    }

    @Test
    public void testGetPurchaseHistory_whenProductPriceChanges_thenOrderTotalRemainsUnchanged() throws Exception {
        // Arrange
        // Create a new ShopFacade object with a shop that has a product. A user places
        // an order for this product.
        _shopsList.add(_shop1);
        ShopFacade shopFacadeUnderTest = new ShopFacade(_shopsList);
        Integer orderId = 1;
        Integer shopId = 1;
        User user = new User("founderName1", "password1", "email1");
        Category category = Category.CLOTHING;
        ShoppingBasket shoppingBasket = new ShoppingBasket(_shop1);
        Product product = new Product(1, "product1", category, 10);
        _shop1.addProductToShop("founderName1", product);
        shoppingBasket.addProductToShoppingBasket(user, product.getProductId());
        ShopOrder shopOrder = new ShopOrder(orderId, shopId, shoppingBasket);
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
    public void testGetPurchaseHistory_whenProductPriceNotChange_thenSuccess() throws Exception {
        // Arrange
        _shopsList.add(_shop1);
        ShopFacade shopFacadeUnderTest = new ShopFacade(_shopsList);
        Integer orderId = 1;
        Integer shopId = 1;
        User testUser = new User("founderName1", "password1", "email1");
        Category productCategory = Category.CLOTHING;
        ShoppingBasket testShoppingBasket = new ShoppingBasket(_shop1);
        Product testProduct = new Product(1, "product1", productCategory, 10);
        _shop1.addProductToShop("founderName1", testProduct);
        testShoppingBasket.addProductToShoppingBasket(testUser, testProduct.getProductId());

        ShopOrder testShopOrder = new ShopOrder(orderId, shopId, testShoppingBasket);

        // Act
        _shop1.addOrderToOrderHistory(testShopOrder);
        List<ShopOrder> purchaseHistory = shopFacadeUnderTest.getPurchaseHistory(shopId);

        // Assert
        assertEquals(10, purchaseHistory.get(0).getOrderTotalAmount());
    }


    @Test
    public void testsUpdateProductInShop_whenShopExist_thenUpdateProductSuccess() throws Exception {
        // Arrange - Create a new ShopFacade object
        _shopsList.add(_shop3);
        ShopFacade _ShopFacadeUnderTests = new ShopFacade(_shopsList);
        Integer shopId = 3;
        String founder = "founderName3";
        
        assertEquals(0, _product2.getProductQuantity());

        // Act - try to update a product to an existing shop
        _ShopFacadeUnderTests.updateProductQuantity(founder, shopId, _product2.getProductId(), 10);

        // Assert - Verify that the product quantity is updated
        assertEquals(10, _product2.getProductQuantity());
    }

    @Test
    public void testsUpdateProductInShop_whenUserDoesNotHavePermisson_thenRaiseError() throws Exception {
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
    public void testsUpdateProductInShop_whenShopIsClose_thenRaiseError() throws Exception {
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
}
