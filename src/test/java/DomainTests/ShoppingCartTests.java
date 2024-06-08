package DomainTests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import Domain.*;
import Domain.ExternalServices.PaymentService.AdapterPayment;
import Domain.ExternalServices.SupplyService.AdapterSupply;
import Domain.Facades.ShopFacade;
import Exceptions.ProdcutPolicyException;
import Exceptions.ProductDoesNotExistsException;
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
    public void testAddProduct_whenProductDoesNotExists_shouldThrowProductDoesNotExistsException() throws StockMarketException {
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
    public void testCancelPurchaseEditStock_whenBasketsToBuyIsNotEmpty_shouldCancelPurchase() throws StockMarketException {
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
        when(shoppingBasketMock.purchaseBasket()).thenReturn(true);
        
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
    public void testPurchaseCartEditStock_whenProductOutOfStock_shouldThrowProductOutOfStockExepction() throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        ShoppingBasket shoppingBasketMock = Mockito.mock(ShoppingBasket.class);
        List<Integer> basketsToBuy = new ArrayList<Integer>();
        basketsToBuy.add(0);
        when(shoppingBasketMock.purchaseBasket()).thenReturn(false);
        
        shoppingCartUnderTest.addShoppingBasket(shoppingBasketMock);
        
        // Act
        try {
            shoppingCartUnderTest.purchaseCartEditStock(basketsToBuy);
        }
        catch (ProductDoesNotExistsException e) {
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
    public void testPurchaseCartEditStock_whenProductOutOfStock_shouldCancelPurchase() throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        ShoppingBasket shoppingBasketMock = Mockito.mock(ShoppingBasket.class);
        List<Integer> basketsToBuy = new ArrayList<Integer>();
        basketsToBuy.add(0);
        when(shoppingBasketMock.purchaseBasket()).thenReturn(false);
        
        shoppingCartUnderTest.addShoppingBasket(shoppingBasketMock);
        
        // Act
        try {
            shoppingCartUnderTest.purchaseCartEditStock(basketsToBuy);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e.getMessage());
        }

        // Assert
        assertTrue(shoppingCartUnderTest.getCartSize() == 1);
    }

    @Test
    public void testPurchaseCartEditStock_whenShopPolicyException_shouldCancelPurchase() throws StockMarketException {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart(shopFacadeMock, adapterPaymentMock, adapterSupplyMock);
        ShoppingBasket shoppingBasketMock = Mockito.mock(ShoppingBasket.class);
        List<Integer> basketsToBuy = new ArrayList<Integer>();
        basketsToBuy.add(0);
        when(shoppingBasketMock.purchaseBasket()).thenThrow(new ShopPolicyException("ShopPolicyException"));
        
        shoppingCartUnderTest.addShoppingBasket(shoppingBasketMock);
        
        // Act
        try {
            shoppingCartUnderTest.purchaseCartEditStock(basketsToBuy);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e.getMessage());
        }

        // Assert
        assertTrue(shoppingCartUnderTest.getCartSize() == 1);
    }

    @Test
    public void testRemoveProduct_whenProductDoesNotExists_shouldThrowProductDoesNotExistsException() throws StockMarketException {
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
            assertTrue(e.getMessage().contains("Trying to remove product from shopping cart, but no shopping basket found for shop: 1"));
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
}
