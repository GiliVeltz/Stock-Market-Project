package DomainTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import Domain.Entities.Product;
import Domain.Entities.Shop;
import Domain.Entities.ShoppingBasket;
import Domain.Entities.ShoppingCart;
import Domain.Entities.User;
import Domain.Entities.enums.Category;
import Domain.ExternalServices.PaymentService.AdapterPaymentImp;
import Domain.ExternalServices.SupplyService.AdapterSupplyImp;
import Domain.Facades.ShopFacade;
import Domain.Repositories.InterfaceOrderRepository;
import Domain.Repositories.InterfaceShopOrderRepository;
import Domain.Repositories.InterfaceShoppingBasketRepository;
import Domain.Repositories.MemoryOrderRepository;
import Dtos.PaymentInfoDto;
import Dtos.PurchaseCartDetailsDto;
import Dtos.SupplyInfoDto;
import Dtos.Rules.MinBasketPriceRuleDto;
import Dtos.Rules.ShoppingBasketRuleDto;
import Exceptions.PaymentFailedException;
import Exceptions.ProdcutPolicyException;
import Exceptions.ProductDoesNotExistsException;
import Exceptions.ShippingFailedException;
import Exceptions.ShopPolicyException;
import Exceptions.StockMarketException;
import Server.notifications.NotificationHandler;

public class ShoppingCartTests {

    private ShoppingCart shoppingCartUnderTest;

    @Mock
    private Shop shopMock;

    @Mock
    private User userMock;

    @Mock
    private AdapterPaymentImp adapterPaymentMock;

    @Mock
    private AdapterSupplyImp adapterSupplyMock;

    @Mock
    private ShopFacade shopFacadeMock;

    @Mock
    private NotificationHandler notificationHandlerMock;

    @Mock
    private InterfaceOrderRepository orderRepositoryMock;

    @Mock
    private InterfaceShoppingBasketRepository shoppingBasketRepositoryMock;

    @Mock
    private InterfaceShopOrderRepository shopOrderRepositoryMock;

    private PaymentInfoDto paymentInfoDto;
    private SupplyInfoDto  supplyInfoDto;

    @BeforeEach
    public void setUp() {
        shopMock = Mockito.mock(Shop.class);
        userMock = Mockito.mock(User.class);
        adapterPaymentMock = Mockito.mock(AdapterPaymentImp.class);
        adapterSupplyMock = Mockito.mock(AdapterSupplyImp.class);
        shopFacadeMock = Mockito.mock(ShopFacade.class);
        notificationHandlerMock = Mockito.mock(NotificationHandler.class);
        orderRepositoryMock = Mockito.mock(InterfaceOrderRepository.class);
        shoppingBasketRepositoryMock = Mockito.mock(InterfaceShoppingBasketRepository.class);
        shopOrderRepositoryMock = Mockito.mock(InterfaceShopOrderRepository.class);
        shoppingCartUnderTest = null;
        paymentInfoDto = new PaymentInfoDto("abc", "abc", "abc", "abc", "abc", "982", "abc");
        supplyInfoDto = new SupplyInfoDto("abc", "abc", "abc", "abc", "abc");
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testAddProduct_whenProductDoesNotExists_shouldThrowProductDoesNotExistsException()
            throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        when(shopMock.getProductById(1)).thenReturn(null);
        Mockito.doNothing().when(shopMock).ValidateProdcutPolicy(Mockito.any(User.class), Mockito.any(Product.class));
        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shopMock);

        // Act
        try {
            shoppingCartUnderTest.addProduct(1, 1, 1);
            fail("Expected ProductDoesNotExistsException");
        } catch (ProductDoesNotExistsException e) {
            // Assert
            assertTrue(e.getMessage().contains("Product does not exists"));
        } catch (ProdcutPolicyException e) {
            e.printStackTrace();
            fail("Unexpected ProdcutPolicyException");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testAddProduct_whenProductExists_shouldAddProduct() throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        shoppingCartUnderTest.setShoppingBasketsRepository(shoppingBasketRepositoryMock);
        Product product = new Product("product", Category.CLOTHING, 10, shopMock, 1);
        when(shopMock.getProductById(1)).thenReturn(product);
        Mockito.doNothing().when(shopMock).ValidateProdcutPolicy(Mockito.any(User.class), Mockito.any(Product.class));
        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shopMock);
        when(userMock.getUserName()).thenReturn("userMock");

        shoppingCartUnderTest.SetUser(userMock);

        // Act
        try {
            shoppingCartUnderTest.addProduct(1, 1, 1);
        } catch (ProductDoesNotExistsException e) {
            e.printStackTrace();
            fail("Unexpected ProductDoesNotExistsException");
        } catch (ProdcutPolicyException e) {
            e.printStackTrace();
            fail("Unexpected ProdcutPolicyException");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e.getMessage());
        }

        // Assert
        assertTrue(shoppingCartUnderTest.getCartSize() == 1);
    }

    @Test
    public void testCancelPurchaseEditStock_whenBasketsToBuyIsEmpty_shouldNotCancelPurchase() {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);

        // Act
        try {
            shoppingCartUnderTest.cancelPurchaseEditStock(new ArrayList<Integer>());
        } catch (ProductDoesNotExistsException e) {
            e.printStackTrace();
            fail("Unexpected ProductDoesNotExistsException");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e.getMessage());
        }

        // Assert
        assertTrue(shoppingCartUnderTest.getCartSize() == 0);
    }

    @Test
    public void testCancelPurchaseEditStock_whenBasketsToBuyIsNotEmpty_shouldCancelPurchase()
            throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        ShoppingBasket shoppingBasketMock = Mockito.mock(ShoppingBasket.class);
        List<Integer> basketsToBuy = new ArrayList<Integer>();
        basketsToBuy.add(0);
        Mockito.doNothing().when(shoppingBasketMock).cancelPurchase();

        shoppingCartUnderTest.addShoppingBasket(shoppingBasketMock);

        // Act
        try {
            shoppingCartUnderTest.cancelPurchaseEditStock(basketsToBuy);
        } catch (ProductDoesNotExistsException e) {
            e.printStackTrace();
            fail("Unexpected ProductDoesNotExistsException");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e.getMessage());
        }

        // Assert
        assertTrue(shoppingCartUnderTest.getCartSize() == 1);
    }

    @Test
    public void testPurchaseCartEditStock_whenBasketsToBuyIsEmpty_shouldNotPurchaseCart() {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);

        // Act
        try {
            shoppingCartUnderTest.purchaseCartEditStock(new ArrayList<Integer>());
        } catch (ProductDoesNotExistsException e) {
            e.printStackTrace();
            fail("Unexpected ProductDoesNotExistsException");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e.getMessage());
        }

        // Assert
        assertTrue(shoppingCartUnderTest.getCartSize() == 0);
    }

    @Disabled
    // TODO: TAL: check this test
    @Test
    public void testPurchaseCartEditStock_whenBasketsToBuyIsNotEmpty_shouldPurchaseCart() throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        ShoppingBasket shoppingBasketMock = Mockito.mock(ShoppingBasket.class);
        List<Integer> basketsToBuy = new ArrayList<Integer>();
        basketsToBuy.add(0);
        // String username = userMock.getUsername();
        when(shoppingBasketMock.purchaseBasket("Guest")).thenReturn(true);
        
        shoppingCartUnderTest.addShoppingBasket(shoppingBasketMock);

        // Act
        try {
            shoppingCartUnderTest.purchaseCartEditStock(basketsToBuy);
        } catch (ProductDoesNotExistsException e) {
            e.printStackTrace();
            fail("Unexpected ProductDoesNotExistsException");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e.getMessage());
        }

        // Assert
        assertTrue(shoppingCartUnderTest.getCartSize() == 1);
    }

    @Test
    public void testPurchaseCartEditStock_whenProductOutOfStock_shouldThrowProductOutOfStockExepction()
            throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        ShoppingBasket shoppingBasketMock = Mockito.mock(ShoppingBasket.class);
        List<Integer> basketsToBuy = new ArrayList<Integer>();
        basketsToBuy.add(0);
        when(shoppingBasketMock.purchaseBasket("Guest")).thenReturn(false);
        
        shoppingCartUnderTest.addShoppingBasket(shoppingBasketMock);

        // Act & Assert
        assertThrows(StockMarketException.class, () -> {
            shoppingCartUnderTest.purchaseCartEditStock(basketsToBuy);
        });
        assertTrue(shoppingCartUnderTest.getCartSize() == 1);
    }

    @Test
    public void testPurchaseCartEditStock_whenProductOutOfStock_shouldCancelPurchase() throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        ShoppingBasket shoppingBasketMock = Mockito.mock(ShoppingBasket.class);
        List<Integer> basketsToBuy = new ArrayList<Integer>();
        basketsToBuy.add(0);
        // when(shoppingBasketMock.getUser)
        // when(shoppingBasketMock.getUser)
        when(shoppingBasketMock.purchaseBasket("Guest")).thenReturn(false);
        
        shoppingCartUnderTest.addShoppingBasket(shoppingBasketMock);

        // Act & Assert
        assertThrows(StockMarketException.class, () -> {
            shoppingCartUnderTest.purchaseCartEditStock(basketsToBuy);
        });
        assertTrue(shoppingCartUnderTest.getCartSize() == 1);
    }

    @Test
    public void testPurchaseCartEditStock_whenShopPolicyException_shouldCancelPurchase() throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        ShoppingBasket shoppingBasketMock = Mockito.mock(ShoppingBasket.class);
        List<Integer> basketsToBuy = new ArrayList<Integer>();
        basketsToBuy.add(0);
        when(shoppingBasketMock.purchaseBasket("Guest")).thenThrow(new ShopPolicyException("ShopPolicyException"));
        
        shoppingCartUnderTest.addShoppingBasket(shoppingBasketMock);

        // Act & Assert
        assertThrows(StockMarketException.class, () -> {
            shoppingCartUnderTest.purchaseCartEditStock(basketsToBuy);
        });
        assertTrue(shoppingCartUnderTest.getCartSize() == 1);
    }

    @Test
    public void testRemoveProduct_whenProductDoesNotExists_shouldThrowProductDoesNotExistsException()
            throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        when(shopMock.getProductById(1)).thenThrow(new ProductDoesNotExistsException("Product does not exists"));
        Mockito.doNothing().when(shopMock).ValidateProdcutPolicy(Mockito.any(User.class), Mockito.any(Product.class));
        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shopMock);

        // Act
        try {
            shoppingCartUnderTest.removeProduct(null, 1, 1);
            fail("Expected StockMarketException");
        } catch (StockMarketException e) {
            // Assert
            assertTrue(e.getMessage()
                    .contains("Trying to remove product from shopping cart, but no shopping basket found for shop: 1"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testRemoveProduct_whenProductExists_shouldRemoveProduct() throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        shoppingCartUnderTest.setShoppingBasketsRepository(shoppingBasketRepositoryMock);
        Product product = new Product("product", Category.CLOTHING, 10, shopMock, 1);
        when(shopMock.getProductById(1)).thenReturn(product);
        when(shopMock.getShopId()).thenReturn(1);
        Mockito.doNothing().when(shopMock).ValidateProdcutPolicy(Mockito.any(User.class), Mockito.any(Product.class));
        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shopMock);
        when(userMock.getUserName()).thenReturn("userMock");

        shoppingCartUnderTest.SetUser(userMock);
        shoppingCartUnderTest.addProduct(1, 1, 1);

        // Act
        try {
            shoppingCartUnderTest.removeProduct(product, 1, 1);
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Unexpected StockMarketException");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e.getMessage());
        }

        // Assert
        assertTrue(shoppingCartUnderTest.getCartSize() == 0);
    }

    @Test
    public void testGetPurchases() {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        ShoppingBasket shoppingBasketMock = Mockito.mock(ShoppingBasket.class);
        when(shoppingBasketMock.getShop()).thenReturn(shopMock);
        when(shopMock.getShopName()).thenReturn("shopMock");

        shoppingCartUnderTest.addShoppingBasket(shoppingBasketMock);

        // Act
        Map<String, ShoppingBasket> purchases = shoppingCartUnderTest.getPurchases();

        // Assert
        assertTrue(purchases.size() == 1);
    }

    @Test
    public void testContainsKey_whenShoppingBasketWithShopIDExists_shouldReturnTrue() {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        ShoppingBasket shoppingBasketMock = Mockito.mock(ShoppingBasket.class);
        when(shoppingBasketMock.getShop()).thenReturn(shopMock);
        when(shopMock.getShopId()).thenReturn(1);

        shoppingCartUnderTest.addShoppingBasket(shoppingBasketMock);

        // Act
        boolean result = shoppingCartUnderTest.containsKey(1);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testContainsKey_whenShoppingBasketWithShopIDDoesNotExists_shouldReturnFalse() {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        ShoppingBasket shoppingBasketMock = Mockito.mock(ShoppingBasket.class);
        when(shoppingBasketMock.getShop()).thenReturn(shopMock);
        when(shopMock.getShopId()).thenReturn(1);

        shoppingCartUnderTest.addShoppingBasket(shoppingBasketMock);

        // Act
        boolean result = shoppingCartUnderTest.containsKey(2);

        // Assert
        assertTrue(!result);
    }

    @Test
    public void testPurchaseCartEditStock_whenBasketsHaveProductsAndTheyAreInStock_shouldReduceStock()
            throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        Shop shop = new Shop("shopName1", "ownerUsername", "bank1", "address1", 1);
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product("product2", Category.ELECTRONICS, 100.0, shop, 2);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop("shopName2", "ownerUsername2", "bank2", "address2", 2);
        Product product3 = new Product("product3", Category.ELECTRONICS, 100.0, shop2, 3);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product("product4", Category.ELECTRONICS, 100.0, shop2, 4);
        product4.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product4);

        shoppingCartUnderTest.setShoppingBasketsRepository(shoppingBasketRepositoryMock);

        shop.setNotificationHandler(notificationHandlerMock);
        shop2.setNotificationHandler(notificationHandlerMock);

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(2, 1, 1);
        shoppingCartUnderTest.addProduct(3, 2, 1);
        shoppingCartUnderTest.addProduct(4, 2, 1);

        ArrayList<Integer> basketsToBuy = new ArrayList<>(Arrays.asList(0, 1));

        // Act
        shoppingCartUnderTest.purchaseCartEditStock(basketsToBuy);

        // Assert
        assertEquals(product.getProductQuantity(), 8);
        assertEquals(product2.getProductQuantity(), 9);
        assertEquals(product3.getProductQuantity(), 9);
        assertEquals(product4.getProductQuantity(), 9);
    }

    @Test
    public void testPurchaseCartEditStock_whenBasketsHaveProductsAndSomeAreNotInStock_shouldNotReduceStock()
            throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        Shop shop = new Shop("shopName1", "ownerUsername", "bank1", "address1", 1);
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product("product2", Category.ELECTRONICS, 100.0, shop, 2);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop("shopName2", "ownerUsername2", "bank2", "address2", 2);
        Product product3 = new Product("product3", Category.ELECTRONICS, 100.0, shop2, 3);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product("product4", Category.ELECTRONICS, 100.0, shop2, 4);
        product4.updateProductQuantity(0);
        shop2.addProductToShop("ownerUsername2", product4);

        shoppingCartUnderTest.setShoppingBasketsRepository(shoppingBasketRepositoryMock);
        
        shop.setNotificationHandler(notificationHandlerMock);
        shop2.setNotificationHandler(notificationHandlerMock);

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(2, 1, 1);
        shoppingCartUnderTest.addProduct(3, 2, 1);
        shoppingCartUnderTest.addProduct(4, 2, 1);

        ArrayList<Integer> basketsToBuy = new ArrayList<>(Arrays.asList(0, 1));

        // Act & Assert
        assertThrows(StockMarketException.class, () -> {
            shoppingCartUnderTest.purchaseCartEditStock(basketsToBuy);
        });
        assertEquals(product.getProductQuantity(), 10);
        assertEquals(product2.getProductQuantity(), 10);
        assertEquals(product3.getProductQuantity(), 10);
        assertEquals(product4.getProductQuantity(), 0);
    }

    @Test
    public void testCancelPurchaseEditStock_whenBasketsNeedToBeCanceled_shouldRestockAllProductsInCart()
            throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        Shop shop = new Shop("shopName1", "ownerUsername", "bank1", "address1", 1);
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product("product2", Category.ELECTRONICS, 100.0, shop, 2);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop("shopName2", "ownerUsername2", "bank2", "address2", 2);
        Product product3 = new Product("product3", Category.ELECTRONICS, 100.0, shop2, 3);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product("product4", Category.ELECTRONICS, 100.0, shop2, 4);
        product4.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product4);
        shoppingCartUnderTest.setShoppingBasketsRepository(shoppingBasketRepositoryMock);

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(2, 1, 1);
        shoppingCartUnderTest.addProduct(3, 2, 1);
        shoppingCartUnderTest.addProduct(4, 2, 1);

        ArrayList<Integer> basketsToBuy = new ArrayList<>(Arrays.asList(0, 1));

        // Act
        shoppingCartUnderTest.cancelPurchaseEditStock(basketsToBuy);

        // Assert
        assertEquals(product.getProductQuantity(), 12);
        assertEquals(product2.getProductQuantity(), 11);
        assertEquals(product3.getProductQuantity(), 11);
        assertEquals(product4.getProductQuantity(), 11);
    }

    @Test
    public void testPurchaseCart_whenEveryThingIsOkAndItsGuest_shouldReduceStockMakeShopOrdersPayDeliver()
            throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        Shop shop = new Shop("shopName1", "ownerUsername", "bank1", "address1", 1);
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product("product2", Category.ELECTRONICS, 100.0, shop, 2);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop("shopName2", "ownerUsername2", "bank2", "address2", 2);
        Product product3 = new Product("product3", Category.ELECTRONICS, 100.0, shop2, 3);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product("product4", Category.ELECTRONICS, 100.0, shop2, 4);
        product4.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product4);

        List<ShoppingBasketRuleDto> shoppingBasketRules = new ArrayList<>();
        shoppingBasketRules.add(new MinBasketPriceRuleDto(100));
        shop.changeShopPolicy("ownerUsername", shoppingBasketRules);
        shop2.changeShopPolicy("ownerUsername2", shoppingBasketRules);

        shop.setNotificationHandler(notificationHandlerMock);
        shop2.setNotificationHandler(notificationHandlerMock);

        shoppingCartUnderTest.setShoppingBasketsRepository(shoppingBasketRepositoryMock);
        shoppingCartUnderTest.setShopOrderRepository(shopOrderRepositoryMock);

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        when(adapterPaymentMock.handshake()).thenReturn(true);
        when(adapterSupplyMock.handshake()).thenReturn(true);
        when(adapterPaymentMock.payment(paymentInfoDto, 500)).thenReturn(1000);
        when(adapterSupplyMock.supply(supplyInfoDto)).thenReturn(1000);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(2, 1, 1);
        shoppingCartUnderTest.addProduct(3, 2, 1);
        shoppingCartUnderTest.addProduct(4, 2, 1);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(paymentInfoDto, supplyInfoDto, 
            new ArrayList<>(Arrays.asList(0, 1)));
     
        // Act
        shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto);

        // Assert
        assertEquals(product.getProductQuantity(), 8);
        assertEquals(product2.getProductQuantity(), 9);
        assertEquals(product3.getProductQuantity(), 9);
        assertEquals(product4.getProductQuantity(), 9);
        assertEquals(shop.getPurchaseHistory().size(), 1);
        assertEquals(shop2.getPurchaseHistory().size(), 1);
    }

    @Test
    public void testPurchaseCart_whenEveryThingIsOkAndItsUser_shouldReduceStockMakeOrdersPayDeliver()
            throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        Date date = new Date();
        date.setTime(0);
        User user = new User("user", "passwordUser", "email", date);
        Shop shop = new Shop("shopName1", "ownerUsername", "bank1", "address1", 1);
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product("product2", Category.ELECTRONICS, 100.0, shop, 2);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop("shopName2", "ownerUsername2", "bank2", "address2", 2);
        Product product3 = new Product("product3", Category.ELECTRONICS, 100.0, shop2, 3);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product("product4", Category.ELECTRONICS, 100.0, shop2, 4);
        product4.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product4);

        List<ShoppingBasketRuleDto> shoppingBasketRules = new ArrayList<>();
        shoppingBasketRules.add(new MinBasketPriceRuleDto(100));
        shop.changeShopPolicy("ownerUsername", shoppingBasketRules);
        shop2.changeShopPolicy("ownerUsername2", shoppingBasketRules);

        shop.setNotificationHandler(notificationHandlerMock);
        shop2.setNotificationHandler(notificationHandlerMock);

        shoppingCartUnderTest.setShoppingBasketsRepository(shoppingBasketRepositoryMock);

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        when(adapterPaymentMock.handshake()).thenReturn(true);
        when(adapterSupplyMock.handshake()).thenReturn(true);
        when(adapterPaymentMock.payment(paymentInfoDto, 500)).thenReturn(1000);
        when(adapterSupplyMock.supply(supplyInfoDto)).thenReturn(1000);
        shoppingCartUnderTest.SetUser(user);
        shoppingCartUnderTest.setShoppingBasketsRepository(shoppingBasketRepositoryMock);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(2, 1, 1);
        shoppingCartUnderTest.addProduct(3, 2, 1);
        shoppingCartUnderTest.addProduct(4, 2, 1);

        shoppingCartUnderTest.setOrderRepository(new MemoryOrderRepository());
        shoppingCartUnderTest.setShopOrderRepository(shopOrderRepositoryMock);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(paymentInfoDto, supplyInfoDto, 
            new ArrayList<>(Arrays.asList(0, 1)));

        // Act
        shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto);

        // Assert
        assertEquals(product.getProductQuantity(), 8);
        assertEquals(product2.getProductQuantity(), 9);
        assertEquals(product3.getProductQuantity(), 9);
        assertEquals(product4.getProductQuantity(), 9);
        assertEquals(shop.getPurchaseHistory().size(), 1);
        assertEquals(shop2.getPurchaseHistory().size(), 1);
        assertEquals(user.getPurchaseHistory().size(), 1);
    }

    @Test
    public void testPurchaseCart_whenDontAcceptPolicyAndItsGuest_shouldNotReduceStockNotMakeShopOrdersNotDeliverNotPay()
            throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        Shop shop = new Shop("shopName1", "ownerUsername", "bank1", "address1", 1);
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product("product2", Category.ELECTRONICS, 100.0, shop, 2);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop("shopName2", "ownerUsername2", "bank2", "address2", 2);
        Product product3 = new Product("product3", Category.ELECTRONICS, 100.0, shop2, 3);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product("product4", Category.ELECTRONICS, 100.0, shop2, 4);
        product4.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product4);

        List<ShoppingBasketRuleDto> shoppingBasketRules = new ArrayList<>();
        shoppingBasketRules.add(new MinBasketPriceRuleDto(10000));
        shop.changeShopPolicy("ownerUsername", shoppingBasketRules);
        shop2.changeShopPolicy("ownerUsername2", shoppingBasketRules);

        shop.setNotificationHandler(notificationHandlerMock);
        shop2.setNotificationHandler(notificationHandlerMock);

        shoppingCartUnderTest.setShoppingBasketsRepository(shoppingBasketRepositoryMock);
        shoppingCartUnderTest.setShopOrderRepository(shopOrderRepositoryMock);

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        when(adapterPaymentMock.handshake()).thenReturn(true);
        when(adapterSupplyMock.handshake()).thenReturn(true);
        when(adapterPaymentMock.payment(paymentInfoDto, 500)).thenReturn(1000);
        when(adapterSupplyMock.supply(supplyInfoDto)).thenReturn(1000);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(2, 1, 1);
        shoppingCartUnderTest.addProduct(3, 2, 1);
        shoppingCartUnderTest.addProduct(4, 2, 1);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(paymentInfoDto, supplyInfoDto, 
            new ArrayList<>(Arrays.asList(0, 1)));
     
        // Act
        assertThrows(StockMarketException.class, () -> {
            shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto);
        });

        // Assert
        assertEquals(product.getProductQuantity(), 10);
        assertEquals(product2.getProductQuantity(), 10);
        assertEquals(product3.getProductQuantity(), 10);
        assertEquals(product4.getProductQuantity(), 10);
        assertEquals(shop.getPurchaseHistory().size(), 0);
        assertEquals(shop2.getPurchaseHistory().size(), 0);
    }

    @Test
    public void testPurchaseCart_whenDontAcceptPolicyAndItsUser_shouldNotReduceStockNotMakeShopOrdersNotDeliverNotPay()
            throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        Date date = new Date();
        date.setTime(0);
        User user = new User("user", "passwordUser", "email", date);
        Shop shop = new Shop("shopName1", "ownerUsername", "bank1", "address1", 1);
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product("product2", Category.ELECTRONICS, 100.0, shop, 2);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop("shopName2", "ownerUsername2", "bank2", "address2", 2);
        Product product3 = new Product("product3", Category.ELECTRONICS, 100.0, shop2, 3);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product("product4", Category.ELECTRONICS, 100.0, shop2, 4);
        product4.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product4);

        List<ShoppingBasketRuleDto> shoppingBasketRules = new ArrayList<>();
        shoppingBasketRules.add(new MinBasketPriceRuleDto(10000));
        shop.changeShopPolicy("ownerUsername", shoppingBasketRules);
        shop2.changeShopPolicy("ownerUsername2", shoppingBasketRules);

        shop.setNotificationHandler(notificationHandlerMock);
        shop2.setNotificationHandler(notificationHandlerMock);

        shoppingCartUnderTest.setShoppingBasketsRepository(shoppingBasketRepositoryMock);

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        when(adapterPaymentMock.handshake()).thenReturn(true);
        when(adapterSupplyMock.handshake()).thenReturn(true);
        when(adapterPaymentMock.payment(paymentInfoDto, 500)).thenReturn(1000);
        when(adapterSupplyMock.supply(supplyInfoDto)).thenReturn(1000);
        shoppingCartUnderTest.SetUser(user);
        shoppingCartUnderTest.setShoppingBasketsRepository(shoppingBasketRepositoryMock);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(2, 1, 1);
        shoppingCartUnderTest.addProduct(3, 2, 1);
        shoppingCartUnderTest.addProduct(4, 2, 1);

        shoppingCartUnderTest.setOrderRepository(new MemoryOrderRepository());
        shoppingCartUnderTest.setShopOrderRepository(shopOrderRepositoryMock);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(paymentInfoDto, supplyInfoDto, 
            new ArrayList<>(Arrays.asList(0, 1)));

        // Act
        assertThrows(StockMarketException.class, () -> {
            shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto);
        });

        // Assert
        assertEquals(product.getProductQuantity(), 10);
        assertEquals(product2.getProductQuantity(), 10);
        assertEquals(product3.getProductQuantity(), 10);
        assertEquals(product4.getProductQuantity(), 10);
        assertEquals(shop.getPurchaseHistory().size(), 0);
        assertEquals(shop2.getPurchaseHistory().size(), 0);
        assertEquals(user.getPurchaseHistory().size(), 0);
    }

    @Test
    public void testPurchaseCart_whenSomeProductsNotInStockItsGuest_shouldNotReduceStockNotMakeShopOrdersNotDeliverNotPay()
            throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        Shop shop = new Shop("shopName1", "ownerUsername", "bank1", "address1", 1);
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product("product2", Category.ELECTRONICS, 100.0, shop, 2);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop("shopName2", "ownerUsername2", "bank2", "address2", 2);
        Product product3 = new Product("product3", Category.ELECTRONICS, 100.0, shop2, 3);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product("product4", Category.ELECTRONICS, 100.0, shop2, 4);
        product4.updateProductQuantity(0);
        shop2.addProductToShop("ownerUsername2", product4);

        shop.setNotificationHandler(notificationHandlerMock);
        shop2.setNotificationHandler(notificationHandlerMock);
        
        shoppingCartUnderTest.setShoppingBasketsRepository(shoppingBasketRepositoryMock);
        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(paymentInfoDto, supplyInfoDto, 
            new ArrayList<>(Arrays.asList(0, 1)));

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(2, 1, 1);
        shoppingCartUnderTest.addProduct(3, 2, 1);
        shoppingCartUnderTest.addProduct(4, 2, 1);

        // Act & Assert
        assertThrows(StockMarketException.class, () -> {
            shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto);
        });
        assertEquals(product.getProductQuantity(), 10);
        assertEquals(product2.getProductQuantity(), 10);
        assertEquals(product3.getProductQuantity(), 10);
        assertEquals(product4.getProductQuantity(), 0);
        assertEquals(shop.getPurchaseHistory().size(), 0);
        assertEquals(shop2.getPurchaseHistory().size(), 0);
    }

    @Test
    public void testPurchaseCart_whenSomeProductsNotInStockItsUser_shouldNotReduceStockNotMakeOrdersNotDeliverNotPay()
            throws StockMarketException {
        // Arrange
        Date date = new Date();
        date.setTime(0);
        User user = new User("user", "passwordUser", "email", date);
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        Shop shop = new Shop("shopName1", "ownerUsername", "bank1", "address1", 1);
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product("product2", Category.ELECTRONICS, 100.0, shop, 2);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop("shopName2", "ownerUsername2", "bank2", "address2", 2);
        Product product3 = new Product("product3", Category.ELECTRONICS, 100.0, shop2, 3);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product("product4", Category.ELECTRONICS, 100.0, shop2, 4);
        product4.updateProductQuantity(0);
        shop2.addProductToShop("ownerUsername2", product4);

        shop.setNotificationHandler(notificationHandlerMock);
        shop2.setNotificationHandler(notificationHandlerMock);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(paymentInfoDto, supplyInfoDto, 
            new ArrayList<>(Arrays.asList(0, 1)));
        
        shoppingCartUnderTest.setShoppingBasketsRepository(shoppingBasketRepositoryMock);
        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        shoppingCartUnderTest.SetUser(user);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(2, 1, 1);
        shoppingCartUnderTest.addProduct(3, 2, 1);
        shoppingCartUnderTest.addProduct(4, 2, 1);

        // Act & Assert
        assertThrows(StockMarketException.class, () -> {
            shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto);
        });
        assertEquals(product.getProductQuantity(), 10);
        assertEquals(product2.getProductQuantity(), 10);
        assertEquals(product3.getProductQuantity(), 10);
        assertEquals(product4.getProductQuantity(), 0);
        assertEquals(shop.getPurchaseHistory().size(), 0);
        assertEquals(shop2.getPurchaseHistory().size(), 0);
        assertEquals(user.getPurchaseHistory().size(), 0);
    }

    @Test
    public void testPurchaseCart_whenCheckPaymentFailedItsGuest_shouldNotReduceStockNotMakeShopOrdersNotDeliverNotPay()
            throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        Shop shop = new Shop("shopName1", "ownerUsername", "bank1", "address1", 1);
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product("product2", Category.ELECTRONICS, 100.0, shop, 2);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop("shopName2", "ownerUsername2", "bank2", "address2", 2);
        Product product3 = new Product("product3", Category.ELECTRONICS, 100.0, shop2, 3);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product("product4", Category.ELECTRONICS, 100.0, shop2, 4);
        product4.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product4);

        shoppingCartUnderTest.setShoppingBasketsRepository(shoppingBasketRepositoryMock);

        shop.setNotificationHandler(notificationHandlerMock);
        shop2.setNotificationHandler(notificationHandlerMock);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(paymentInfoDto, supplyInfoDto, 
            new ArrayList<>(Arrays.asList(0, 1)));

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        when(adapterPaymentMock.payment(paymentInfoDto, 500)).thenReturn(-1);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(2, 1, 1);
        shoppingCartUnderTest.addProduct(3, 2, 1);
        shoppingCartUnderTest.addProduct(4, 2, 1);

        // Act & Assert
        assertThrows(PaymentFailedException.class, () -> {
            shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto);
        });
        assertEquals(product.getProductQuantity(), 10);
        assertEquals(product2.getProductQuantity(), 10);
        assertEquals(product3.getProductQuantity(), 10);
        assertEquals(product4.getProductQuantity(), 10);
        assertEquals(shop.getPurchaseHistory().size(), 0);
        assertEquals(shop2.getPurchaseHistory().size(), 0);
    }

    @Test
    public void testPurchaseCart_whenCheckPaymentFailedItsUser_shouldNotReduceStockNotMakeOrdersNotDeliverNotPay()
            throws StockMarketException {
        // Arrange
        Date date = new Date();
        date.setTime(0);
        User user = new User("user", "passwordUser", "email", date);
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        Shop shop = new Shop("shopName1", "ownerUsername", "bank1", "address1", 1);
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product("product2", Category.ELECTRONICS, 100.0, shop, 2);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop("shopName2", "ownerUsername2", "bank2", "address2", 2);
        Product product3 = new Product("product3", Category.ELECTRONICS, 100.0, shop2, 3);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product("product4", Category.ELECTRONICS, 100.0, shop2, 4);
        product4.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product4);

        shoppingCartUnderTest.setShoppingBasketsRepository(shoppingBasketRepositoryMock);

        shop.setNotificationHandler(notificationHandlerMock);
        shop2.setNotificationHandler(notificationHandlerMock);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(paymentInfoDto, supplyInfoDto, 
            new ArrayList<>(Arrays.asList(0, 1)));

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        when(adapterPaymentMock.payment(paymentInfoDto, 500)).thenReturn(-1);
        shoppingCartUnderTest.SetUser(user);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(2, 1, 1);
        shoppingCartUnderTest.addProduct(3, 2, 1);
        shoppingCartUnderTest.addProduct(4, 2, 1);

        // Act & Assert
        assertThrows(PaymentFailedException.class, () -> {
            shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto);
        });
        assertEquals(product.getProductQuantity(), 10);
        assertEquals(product2.getProductQuantity(), 10);
        assertEquals(product3.getProductQuantity(), 10);
        assertEquals(product4.getProductQuantity(), 10);
        assertEquals(shop.getPurchaseHistory().size(), 0);
        assertEquals(shop2.getPurchaseHistory().size(), 0);
        assertEquals(user.getPurchaseHistory().size(), 0);
    }

    @Test
    public void testPurchaseCart_whenCheckDeliverFailedItsGuest_shouldNotReduceStockNotMakeShopOrdersNotDeliverRefound()
            throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        Shop shop = new Shop("shopName1", "ownerUsername", "bank1", "address1", 1);
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product("product2", Category.ELECTRONICS, 100.0, shop, 2);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop("shopName2", "ownerUsername2", "bank2", "address2", 2);
        Product product3 = new Product("product3", Category.ELECTRONICS, 100.0, shop2, 3);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product("product4", Category.ELECTRONICS, 100.0, shop2, 4);
        product4.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product4);

        shoppingCartUnderTest.setShoppingBasketsRepository(shoppingBasketRepositoryMock);

        shop.setNotificationHandler(notificationHandlerMock);
        shop2.setNotificationHandler(notificationHandlerMock);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(paymentInfoDto, supplyInfoDto, 
            new ArrayList<>(Arrays.asList(0, 1)));

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        when(adapterPaymentMock.handshake()).thenReturn(true);
        when(adapterSupplyMock.handshake()).thenReturn(true);
        when(adapterPaymentMock.payment(paymentInfoDto, 500)).thenReturn(1000);
        when(adapterSupplyMock.supply(supplyInfoDto)).thenReturn(-1);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(2, 1, 1);
        shoppingCartUnderTest.addProduct(3, 2, 1);
        shoppingCartUnderTest.addProduct(4, 2, 1);

        // Act & Assert
        assertThrows(ShippingFailedException.class, () -> {
            shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto);
        });
        assertEquals(product.getProductQuantity(), 10);
        assertEquals(product2.getProductQuantity(), 10);
        assertEquals(product3.getProductQuantity(), 10);
        assertEquals(product4.getProductQuantity(), 10);
        assertEquals(shop.getPurchaseHistory().size(), 0);
        assertEquals(shop2.getPurchaseHistory().size(), 0);
    }

    @Test
    public void testPurchaseCart_whenCheckDeliverFailedItsUser_shouldNotReduceStockNotMakeOrdersNotDeliverRefound()
            throws StockMarketException {
        // Arrange
        Date date = new Date();
        date.setTime(0);
        User user = new User("user", "passwordUser", "email", date);
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        Shop shop = new Shop("shopName1", "ownerUsername", "bank1", "address1", 1);
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product("product2", Category.ELECTRONICS, 100.0, shop, 2);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop("shopName2", "ownerUsername2", "bank2", "address2", 2);
        Product product3 = new Product("product3", Category.ELECTRONICS, 100.0, shop2, 3);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product("product4", Category.ELECTRONICS, 100.0, shop2, 4);
        product4.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product4);

        shoppingCartUnderTest.setShoppingBasketsRepository(shoppingBasketRepositoryMock);

        shop.setNotificationHandler(notificationHandlerMock);
        shop2.setNotificationHandler(notificationHandlerMock);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(paymentInfoDto, supplyInfoDto, 
            new ArrayList<>(Arrays.asList(0, 1)));

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        when(adapterPaymentMock.handshake()).thenReturn(true);
        when(adapterSupplyMock.handshake()).thenReturn(true);
        when(adapterPaymentMock.payment(paymentInfoDto, 500)).thenReturn(1000);
        when(adapterSupplyMock.supply(supplyInfoDto)).thenReturn(-1);
        shoppingCartUnderTest.SetUser(user);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(2, 1, 1);
        shoppingCartUnderTest.addProduct(3, 2, 1);
        shoppingCartUnderTest.addProduct(4, 2, 1);

        // Act & Assert
        assertThrows(ShippingFailedException.class, () -> {
            shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto);
        });
        assertEquals(product.getProductQuantity(), 10);
        assertEquals(product2.getProductQuantity(), 10);
        assertEquals(product3.getProductQuantity(), 10);
        assertEquals(product4.getProductQuantity(), 10);
        assertEquals(shop.getPurchaseHistory().size(), 0);
        assertEquals(shop2.getPurchaseHistory().size(), 0);
        assertEquals(user.getPurchaseHistory().size(), 0);
    }

    @Test
    public void testPurchaseCart_whenPaymentFailedItsGuest_shouldNotReduceStockNotMakeShopOrdersNotDeliverNotCharge()
            throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        Shop shop = new Shop("shopName1", "ownerUsername", "bank1", "address1", 1);
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);

        shop.setNotificationHandler(notificationHandlerMock);
        shoppingCartUnderTest.setShoppingBasketsRepository(shoppingBasketRepositoryMock);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(paymentInfoDto, supplyInfoDto, 
            new ArrayList<>(Arrays.asList(0)));
        Map<Double, String> paymentDetails = new HashMap<>();
        paymentDetails.put(200.0, "bank1");
        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(adapterPaymentMock.payment(paymentInfoDto, 200)).thenReturn(-1);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(1, 1, 1);

        // Act & Assert
        assertThrows(PaymentFailedException.class, () -> {
            shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto);
        });
        assertEquals(product.getProductQuantity(), 10);
        assertEquals(shop.getPurchaseHistory().size(), 0);
    }

    @Test
    public void testPurchaseCart_whenPaymentFailedItsUser_shouldNotReduceStockNotMakeShopOrdersNotDeliverNotCharge()
            throws StockMarketException {
        // Arrange
        Date date = new Date();
        date.setTime(0);
        User user = new User("user", "passwordUser", "email", date);
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        Shop shop = new Shop("shopName1", "ownerUsername", "bank1", "address1", 1);
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);

        shop.setNotificationHandler(notificationHandlerMock);
        shoppingCartUnderTest.setShoppingBasketsRepository(shoppingBasketRepositoryMock);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(paymentInfoDto, supplyInfoDto, 
                new ArrayList<>(Arrays.asList(0)));
        Map<Double, String> paymentDetails = new HashMap<>();
        paymentDetails.put(200.0, "bank1");
        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(adapterPaymentMock.payment(paymentInfoDto, 200)).thenReturn(-1);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.SetUser(user);

        // Act & Assert
        assertThrows(PaymentFailedException.class, () -> {
            shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto);
        });
        assertEquals(product.getProductQuantity(), 10);
        assertEquals(shop.getPurchaseHistory().size(), 0);
        assertEquals(user.getPurchaseHistory().size(), 0);
    }

    @Test
    public void testPurchaseCart_whenDeliveryFailedItsGuest_shouldNotReduceStockNotMakeShopOrdersNotDeliverRefound()
            throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        Shop shop = new Shop("shopName1", "ownerUsername", "bank1", "address1", 1);
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);

        shoppingCartUnderTest.setShoppingBasketsRepository(shoppingBasketRepositoryMock);
        shop.setNotificationHandler(notificationHandlerMock);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(paymentInfoDto, supplyInfoDto, 
                new ArrayList<>(Arrays.asList(0)));
        Map<Double, String> paymentDetails = new HashMap<>();
        paymentDetails.put(200.0, "bank1");
        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(adapterPaymentMock.handshake()).thenReturn(true);
        when(adapterSupplyMock.handshake()).thenReturn(true);
        when(adapterPaymentMock.payment(paymentInfoDto, 200)).thenReturn(1000);
        when(adapterSupplyMock.supply(supplyInfoDto)).thenReturn(-1);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(1, 1, 1);

        // Act & Assert
        assertThrows(ShippingFailedException.class, () -> {
            shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto);
        });
        assertEquals(product.getProductQuantity(), 10);
        assertEquals(shop.getPurchaseHistory().size(), 0);
    }

    @Test
    public void testPurchaseCart_whenDeliveryFailedItsUser_shouldNotReduceStockNotMakeShopOrdersNotDeliverRefound()
            throws StockMarketException {
        // Arrange
        Date date = new Date();
        date.setTime(0);
        User user = new User("user", "passwordUser", "email", date);
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        Shop shop = new Shop("shopName1", "ownerUsername", "bank1", "address1", 1);
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);

        shop.setNotificationHandler(notificationHandlerMock);
        shoppingCartUnderTest.setShoppingBasketsRepository(shoppingBasketRepositoryMock);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(paymentInfoDto, supplyInfoDto, 
                new ArrayList<>(Arrays.asList(0)));
        Map<Double, String> paymentDetails = new HashMap<>();
        paymentDetails.put(200.0, "bank1");
        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(adapterSupplyMock.supply(supplyInfoDto)).thenReturn(-1);
        when(adapterPaymentMock.handshake()).thenReturn(true);
        when(adapterSupplyMock.handshake()).thenReturn(true);
        when(adapterPaymentMock.payment(paymentInfoDto, 200)).thenReturn(1000);
        when(adapterSupplyMock.supply(supplyInfoDto)).thenReturn(-1);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.addProduct(1, 1, 1);
        shoppingCartUnderTest.SetUser(user);

        // Act & Assert
        assertThrows(ShippingFailedException.class, () -> {
            shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto);
        });
        assertEquals(product.getProductQuantity(), 10);
        assertEquals(shop.getPurchaseHistory().size(), 0);
    }
}
