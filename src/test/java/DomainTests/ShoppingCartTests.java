package DomainTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import Domain.*;
import Domain.Entities.Product;
import Domain.Entities.Shop;
import Domain.Entities.ShoppingBasket;
import Domain.Entities.ShoppingCart;
import Domain.Entities.User;
import Domain.ExternalServices.PaymentService.AdapterPayment;
import Domain.ExternalServices.SupplyService.AdapterSupply;
import Domain.Facades.ShopFacade;
import Dtos.PurchaseCartDetailsDto;
import Exceptions.PaymentFailedException;
import Exceptions.ProdcutPolicyException;
import Exceptions.ProductDoesNotExistsException;
import Exceptions.ShippingFailedException;
import Exceptions.ShopPolicyException;
import Exceptions.StockMarketException;
import enums.Category;

public class ShoppingCartTests {

    private ShoppingCart shoppingCartUnderTest;

    @Mock
    private Shop shopMock;

    @Mock
    private User userMock;

    @Mock
    private AdapterPayment adapterPaymentMock;

    @Mock
    private AdapterSupply adapterSupplyMock;

    @Mock
    private ShopFacade shopFacadeMock;

    @BeforeEach
    public void setUp() {
        shopMock = Mockito.mock(Shop.class);
        userMock = Mockito.mock(User.class);
        adapterPaymentMock = Mockito.mock(AdapterPayment.class);
        adapterSupplyMock = Mockito.mock(AdapterSupply.class);
        shopFacadeMock = Mockito.mock(ShopFacade.class);
        shoppingCartUnderTest = null;
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
            shoppingCartUnderTest.addProduct(1, 1);
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
        Product product = new Product(1, "product", Category.CLOTHING, 10);
        when(shopMock.getProductById(1)).thenReturn(product);
        Mockito.doNothing().when(shopMock).ValidateProdcutPolicy(Mockito.any(User.class), Mockito.any(Product.class));
        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shopMock);
        when(userMock.getUserName()).thenReturn("userMock");

        shoppingCartUnderTest.SetUser(userMock);

        // Act
        try {
            shoppingCartUnderTest.addProduct(1, 1);
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
            shoppingCartUnderTest.removeProduct(1, 1);
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
        Product product = new Product(1, "product", Category.CLOTHING, 10);
        when(shopMock.getProductById(1)).thenReturn(product);
        when(shopMock.getShopId()).thenReturn(1);
        Mockito.doNothing().when(shopMock).ValidateProdcutPolicy(Mockito.any(User.class), Mockito.any(Product.class));
        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shopMock);
        when(userMock.getUserName()).thenReturn("userMock");

        shoppingCartUnderTest.SetUser(userMock);
        shoppingCartUnderTest.addProduct(1, 1);

        // Act
        try {
            shoppingCartUnderTest.removeProduct(1, 1);
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
        Shop shop = new Shop(1, "shopName1", "ownerUsername", "bank1", "address1");
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product(2, "product2", Category.ELECTRONICS, 100.0);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop(2, "shopName2", "ownerUsername2", "bank2", "address2");
        Product product3 = new Product(3, "product3", Category.ELECTRONICS, 100.0);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product(4, "product4", Category.ELECTRONICS, 100.0);
        product4.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product4);

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(2, 1);
        shoppingCartUnderTest.addProduct(3, 2);
        shoppingCartUnderTest.addProduct(4, 2);

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
        Shop shop = new Shop(1, "shopName1", "ownerUsername", "bank1", "address1");
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product(2, "product2", Category.ELECTRONICS, 100.0);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop(2, "shopName2", "ownerUsername2", "bank2", "address2");
        Product product3 = new Product(3, "product3", Category.ELECTRONICS, 100.0);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product(4, "product4", Category.ELECTRONICS, 100.0);
        product4.updateProductQuantity(0);
        shop2.addProductToShop("ownerUsername2", product4);

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(2, 1);
        shoppingCartUnderTest.addProduct(3, 2);
        shoppingCartUnderTest.addProduct(4, 2);

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
        Shop shop = new Shop(1, "shopName1", "ownerUsername", "bank1", "address1");
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product(2, "product2", Category.ELECTRONICS, 100.0);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop(2, "shopName2", "ownerUsername2", "bank2", "address2");
        Product product3 = new Product(3, "product3", Category.ELECTRONICS, 100.0);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product(4, "product4", Category.ELECTRONICS, 100.0);
        product4.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product4);

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(2, 1);
        shoppingCartUnderTest.addProduct(3, 2);
        shoppingCartUnderTest.addProduct(4, 2);

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
        Shop shop = new Shop(1, "shopName1", "ownerUsername", "bank1", "address1");
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product(2, "product2", Category.ELECTRONICS, 100.0);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop(2, "shopName2", "ownerUsername2", "bank2", "address2");
        Product product3 = new Product(3, "product3", Category.ELECTRONICS, 100.0);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product(4, "product4", Category.ELECTRONICS, 100.0);
        product4.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product4);

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(2, 1);
        shoppingCartUnderTest.addProduct(3, 2);
        shoppingCartUnderTest.addProduct(4, 2);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(new ArrayList<>(Arrays.asList(0, 1)),
                "123456789", "Guest Address");

        // Act
        shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto, 0);

        // Assert
        assertEquals(product.getProductQuantity(), 8);
        assertEquals(product2.getProductQuantity(), 9);
        assertEquals(product3.getProductQuantity(), 9);
        assertEquals(product4.getProductQuantity(), 9);
        assertEquals(shop.getPurchaseHistory().size(), 1);
        assertEquals(shop.getPurchaseHistory().get(0).getOrderId(), 0);
        assertEquals(shop2.getPurchaseHistory().size(), 1);
        assertEquals(shop2.getPurchaseHistory().get(0).getOrderId(), 0);
    }

    @Test
    public void testPurchaseCart_whenEveryThingIsOkAndItsUser_shouldReduceStockMakeOrdersPayDeliver()
            throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        Date date = new Date();
        date.setTime(0);
        User user = new User("user", "passwordUser", "email", date);
        Shop shop = new Shop(1, "shopName1", "ownerUsername", "bank1", "address1");
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product(2, "product2", Category.ELECTRONICS, 100.0);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop(2, "shopName2", "ownerUsername2", "bank2", "address2");
        Product product3 = new Product(3, "product3", Category.ELECTRONICS, 100.0);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product(4, "product4", Category.ELECTRONICS, 100.0);
        product4.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product4);

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        shoppingCartUnderTest.SetUser(user);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(2, 1);
        shoppingCartUnderTest.addProduct(3, 2);
        shoppingCartUnderTest.addProduct(4, 2);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(new ArrayList<>(Arrays.asList(0, 1)),
                "123456789", "Guest Address");

        // Act
        shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto, 0);

        // Assert
        assertEquals(product.getProductQuantity(), 8);
        assertEquals(product2.getProductQuantity(), 9);
        assertEquals(product3.getProductQuantity(), 9);
        assertEquals(product4.getProductQuantity(), 9);
        assertEquals(shop.getPurchaseHistory().size(), 1);
        assertEquals(shop.getPurchaseHistory().get(0).getOrderId(), 0);
        assertEquals(shop2.getPurchaseHistory().size(), 1);
        assertEquals(shop2.getPurchaseHistory().get(0).getOrderId(), 0);
        assertEquals(user.getPurchaseHistory().size(), 1);
        assertEquals(user.getPurchaseHistory().get(0).getOrderId(), 0);
    }

    @Test
    public void testPurchaseCart_whenSomeProductsNotInStockItsGuest_shouldNotReduceStockNotMakeShopOrdersNotDeliverNotPay()
            throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        Shop shop = new Shop(1, "shopName1", "ownerUsername", "bank1", "address1");
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product(2, "product2", Category.ELECTRONICS, 100.0);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop(2, "shopName2", "ownerUsername2", "bank2", "address2");
        Product product3 = new Product(3, "product3", Category.ELECTRONICS, 100.0);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product(4, "product4", Category.ELECTRONICS, 100.0);
        product4.updateProductQuantity(0);
        shop2.addProductToShop("ownerUsername2", product4);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(new ArrayList<>(Arrays.asList(0, 1)),
                "123456789", "Guest Address");

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(2, 1);
        shoppingCartUnderTest.addProduct(3, 2);
        shoppingCartUnderTest.addProduct(4, 2);

        // Act & Assert
        assertThrows(StockMarketException.class, () -> {
            shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto, 0);
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
        Shop shop = new Shop(1, "shopName1", "ownerUsername", "bank1", "address1");
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product(2, "product2", Category.ELECTRONICS, 100.0);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop(2, "shopName2", "ownerUsername2", "bank2", "address2");
        Product product3 = new Product(3, "product3", Category.ELECTRONICS, 100.0);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product(4, "product4", Category.ELECTRONICS, 100.0);
        product4.updateProductQuantity(0);
        shop2.addProductToShop("ownerUsername2", product4);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(new ArrayList<>(Arrays.asList(0, 1)),
                "123456789", "Guest Address");

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        shoppingCartUnderTest.SetUser(user);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(2, 1);
        shoppingCartUnderTest.addProduct(3, 2);
        shoppingCartUnderTest.addProduct(4, 2);

        // Act & Assert
        assertThrows(StockMarketException.class, () -> {
            shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto, 0);
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
        Shop shop = new Shop(1, "shopName1", "ownerUsername", "bank1", "address1");
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product(2, "product2", Category.ELECTRONICS, 100.0);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop(2, "shopName2", "ownerUsername2", "bank2", "address2");
        Product product3 = new Product(3, "product3", Category.ELECTRONICS, 100.0);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product(4, "product4", Category.ELECTRONICS, 100.0);
        product4.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product4);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(new ArrayList<>(Arrays.asList(0, 1)),
                "123456789", "Guest Address");

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        doThrow(new PaymentFailedException("Payment failed")).when(adapterPaymentMock)
        .checkIfPaymentOk(purchaseCartDetailsDto.cardNumber);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(2, 1);
        shoppingCartUnderTest.addProduct(3, 2);
        shoppingCartUnderTest.addProduct(4, 2);

        // Act & Assert
        assertThrows(PaymentFailedException.class, () -> {
            shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto, 0);
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
        Shop shop = new Shop(1, "shopName1", "ownerUsername", "bank1", "address1");
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product(2, "product2", Category.ELECTRONICS, 100.0);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop(2, "shopName2", "ownerUsername2", "bank2", "address2");
        Product product3 = new Product(3, "product3", Category.ELECTRONICS, 100.0);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product(4, "product4", Category.ELECTRONICS, 100.0);
        product4.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product4);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(new ArrayList<>(Arrays.asList(0, 1)),
                "123456789", "Guest Address");

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        doThrow(new PaymentFailedException("Payment failed")).when(adapterPaymentMock)
        .checkIfPaymentOk(purchaseCartDetailsDto.cardNumber);
        shoppingCartUnderTest.SetUser(user);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(2, 1);
        shoppingCartUnderTest.addProduct(3, 2);
        shoppingCartUnderTest.addProduct(4, 2);

        // Act & Assert
        assertThrows(PaymentFailedException.class, () -> {
            shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto, 0);
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
        Shop shop = new Shop(1, "shopName1", "ownerUsername", "bank1", "address1");
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product(2, "product2", Category.ELECTRONICS, 100.0);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop(2, "shopName2", "ownerUsername2", "bank2", "address2");
        Product product3 = new Product(3, "product3", Category.ELECTRONICS, 100.0);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product(4, "product4", Category.ELECTRONICS, 100.0);
        product4.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product4);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(new ArrayList<>(Arrays.asList(0, 1)),
                "123456789", "Guest Address");

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        doThrow(new ShippingFailedException("Delivery failed")).when(adapterSupplyMock)
        .checkIfDeliverOk(purchaseCartDetailsDto.address);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(2, 1);
        shoppingCartUnderTest.addProduct(3, 2);
        shoppingCartUnderTest.addProduct(4, 2);

        // Act & Assert
        assertThrows(ShippingFailedException.class, () -> {
            shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto, 0);
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
        Shop shop = new Shop(1, "shopName1", "ownerUsername", "bank1", "address1");
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product(2, "product2", Category.ELECTRONICS, 100.0);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        Shop shop2 = new Shop(2, "shopName2", "ownerUsername2", "bank2", "address2");
        Product product3 = new Product(3, "product3", Category.ELECTRONICS, 100.0);
        product3.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product3);
        Product product4 = new Product(4, "product4", Category.ELECTRONICS, 100.0);
        product4.updateProductQuantity(10);
        shop2.addProductToShop("ownerUsername2", product4);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(new ArrayList<>(Arrays.asList(0, 1)),
                "123456789", "Guest Address");

        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        when(shopFacadeMock.getShopByShopId(2)).thenReturn(shop2);
        doThrow(new ShippingFailedException("Delivery falied")).when(adapterSupplyMock)
        .checkIfDeliverOk(purchaseCartDetailsDto.address);
        shoppingCartUnderTest.SetUser(user);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(2, 1);
        shoppingCartUnderTest.addProduct(3, 2);
        shoppingCartUnderTest.addProduct(4, 2);

        // Act & Assert
        assertThrows(ShippingFailedException.class, () -> {
            shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto, 0);
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
        Shop shop = new Shop(1, "shopName1", "ownerUsername", "bank1", "address1");
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(new ArrayList<>(Arrays.asList(0)),
                "123456789", "Guest Address");
        Map<Double, String> paymentDetails = new HashMap<>();
        paymentDetails.put(200.0, "bank1");
        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        doThrow(new PaymentFailedException("Payment failed")).when(adapterPaymentMock)
        .pay(purchaseCartDetailsDto.cardNumber, paymentDetails, 200.0);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(1, 1);

        // Act & Assert
        assertThrows(PaymentFailedException.class, () -> {
            shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto, 0);
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
        Shop shop = new Shop(1, "shopName1", "ownerUsername", "bank1", "address1");
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(new ArrayList<>(Arrays.asList(0)),
                "123456789", "Guest Address");
        Map<Double, String> paymentDetails = new HashMap<>();
        paymentDetails.put(200.0, "bank1");
        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        doThrow(new PaymentFailedException("Payment failed")).when(adapterPaymentMock)
        .pay(purchaseCartDetailsDto.cardNumber, paymentDetails, 200.0);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.SetUser(user);

        // Act & Assert
        assertThrows(PaymentFailedException.class, () -> {
            shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto, 0);
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
        Shop shop = new Shop(1, "shopName1", "ownerUsername", "bank1", "address1");
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(new ArrayList<>(Arrays.asList(0)),
                "123456789", "Guest Address");
        Map<Double, String> paymentDetails = new HashMap<>();
        paymentDetails.put(200.0, "bank1");
        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        doThrow(new ShippingFailedException("Delivery failed")).when(adapterSupplyMock)
        .deliver(purchaseCartDetailsDto.address, "address1");
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(1, 1);

        // Act & Assert
        assertThrows(ShippingFailedException.class, () -> {
            shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto, 0);
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
        Shop shop = new Shop(1, "shopName1", "ownerUsername", "bank1", "address1");
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product);

        PurchaseCartDetailsDto purchaseCartDetailsDto = new PurchaseCartDetailsDto(new ArrayList<>(Arrays.asList(0)),
                "123456789", "Guest Address");
        Map<Double, String> paymentDetails = new HashMap<>();
        paymentDetails.put(200.0, "bank1");
        when(shopFacadeMock.getShopByShopId(1)).thenReturn(shop);
        doThrow(new ShippingFailedException("Delivery failed")).when(adapterSupplyMock)
        .deliver(purchaseCartDetailsDto.address, "address1");
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.addProduct(1, 1);
        shoppingCartUnderTest.SetUser(user);

        // Act & Assert
        assertThrows(ShippingFailedException.class, () -> {
            shoppingCartUnderTest.purchaseCart(purchaseCartDetailsDto, 0);
        });
        assertEquals(product.getProductQuantity(), 10);
        assertEquals(shop.getPurchaseHistory().size(), 0);
    }
}
