package DmainTests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import Domain.Product;
import Domain.Shop;
import Domain.ShoppingBasket;
import Domain.User;
import Exceptions.PermissionException;
import Exceptions.ProdcutPolicyException;
import Exceptions.ProductAlreadyExistsException;
import Exceptions.ProductDoesNotExistsException;
import Exceptions.ShopPolicyException;
import Exceptions.StockMarketException;
import enums.Category;
import org.mockito.Mockito;

public class ShoppingBasketTests {

    private ShoppingBasket shoppingBasketUnderTest;

    @Mock
    private Shop shopMock;

    @Mock
    private User userMock;

    @BeforeEach
    public void setUp() {
        shopMock = Mockito.mock(Shop.class);
        userMock = Mockito.mock(User.class);
        shoppingBasketUnderTest = null;
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testAddProductToShoppingBasket_whenProductIsNotInBasket_shouldAddProductToBasket() throws ProductDoesNotExistsException {
        // Arrange
        Product product = new Product(1, "Product 1", Category.ELECTRONICS, 100.0);
        when(shopMock.getProductById(1)).thenReturn(product);
        
        shoppingBasketUnderTest = new ShoppingBasket(shopMock);
        
        // Act
        try {
            shoppingBasketUnderTest.addProductToShoppingBasket(userMock, 1);
        } catch (ProdcutPolicyException e) {
            e.printStackTrace();
            fail("Unexpected ProdcutPolicyException exception thrown");
        }
        
        // Assert
        assertTrue(shoppingBasketUnderTest.getProductIdList().contains(1));
    }

    @Test
    public void testAddProductToShoppingBasket_whenProductIsInBasket_shouldTheProductAddedMoreToBasket() throws ProductDoesNotExistsException {
        // Arrange
        Product product = new Product(1, "Product 1", Category.ELECTRONICS, 100.0);
        when(shopMock.getProductById(1)).thenReturn(product);

        shoppingBasketUnderTest = new ShoppingBasket(shopMock);

        try {
            shoppingBasketUnderTest.addProductToShoppingBasket(userMock, 1);
        } catch (ProdcutPolicyException e) {
            e.printStackTrace();
            fail("Unexpected ProdcutPolicyException exception thrown");
        }

        // Act
        try {
            shoppingBasketUnderTest.addProductToShoppingBasket(userMock, 1);
        } catch (ProdcutPolicyException e) {
            e.printStackTrace();
            fail("Unexpected ProdcutPolicyException exception thrown");
        }
        
        // Assert
        assertTrue(shoppingBasketUnderTest.getProductIdList().contains(1));
        assertTrue(shoppingBasketUnderTest.getProductIdList().size() == 2);
        assertTrue(shoppingBasketUnderTest.getProductsList().size() == 2);
    }

    @Test
    public void testAddProductToShoppingBasket_whenProductIsNotInShop_shouldThrowProdcutPolicyException() throws ProductDoesNotExistsException {
        // Arrange
        Product product = new Product(1, "Product 1", Category.ELECTRONICS, 100.0);
        when(shopMock.getProductById(1)).thenReturn(product);
        when(shopMock.getProductById(2)).thenThrow(new ProductDoesNotExistsException("Product not found in shop"));

        shoppingBasketUnderTest = new ShoppingBasket(shopMock);

        // Act
        try {
            shoppingBasketUnderTest.addProductToShoppingBasket(userMock, 2);
            fail("Expected ProductDoesNotExistsException exception not thrown");
        } catch (Exception e) {
            // Assert
            assertTrue(e.getMessage().equals("Product not found in shop"));
        }
    }

    @Test
    public void testRemoveProductFromShoppingBasket_whenProductIsInBasket_shouldRemoveProductFromBasket() throws ProductDoesNotExistsException {
        // Arrange
        Product product = new Product(1, "Product 1", Category.ELECTRONICS, 100.0);
        when(shopMock.getProductById(1)).thenReturn(product);

        shoppingBasketUnderTest = new ShoppingBasket(shopMock);

        try {
            shoppingBasketUnderTest.addProductToShoppingBasket(userMock, 1);
        } catch (ProdcutPolicyException e) {
            e.printStackTrace();
            fail("Unexpected ProdcutPolicyException exception thrown");
        }

        // Act
        shoppingBasketUnderTest.removeProductFromShoppingBasket(1);
        
        // Assert
        assertTrue(!shoppingBasketUnderTest.getProductIdList().contains(1));
    }

    @Test
    public void testRemoveProductFromShoppingBasket_whenProductIsNotInBasket_shouldNotRemoveProductFromBasket() throws ProductDoesNotExistsException {
        // Arrange
        Product product = new Product(1, "Product 1", Category.ELECTRONICS, 100.0);
        when(shopMock.getProductById(1)).thenReturn(product);

        shoppingBasketUnderTest = new ShoppingBasket(shopMock);

        try {
            shoppingBasketUnderTest.addProductToShoppingBasket(userMock, 1);
        } catch (ProdcutPolicyException e) {
            e.printStackTrace();
            fail("Unexpected ProdcutPolicyException exception thrown");
        }

        // Act
        shoppingBasketUnderTest.removeProductFromShoppingBasket(2);
        
        // Assert
        assertTrue(shoppingBasketUnderTest.getProductIdList().contains(1));
    }

    @Test
    public void testCalculateShoppingBasketPrice_whenBasketIsEmpty_shouldReturnZero() {
        // Arrange
        shoppingBasketUnderTest = new ShoppingBasket(shopMock);

        // Act
        double actual = 0.0;
        try {
            actual = shoppingBasketUnderTest.calculateShoppingBasketPrice();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception thrown");
        }
        
        // Assert
        assertTrue(actual == 0.0);
    }

    @Test
    public void testCalculateShoppingBasketPrice_whenBasketIsNotEmpty_shouldReturnTotalPrice() throws ProductDoesNotExistsException {
        // Arrange
        Product product1 = new Product(1, "Product 1", Category.ELECTRONICS, 100.0);
        Product product2 = new Product(2, "Product 2", Category.ELECTRONICS, 200.0);
        when(shopMock.getProductById(1)).thenReturn(product1);
        when(shopMock.getProductById(2)).thenReturn(product2);

        shoppingBasketUnderTest = new ShoppingBasket(shopMock);

        try {
            shoppingBasketUnderTest.addProductToShoppingBasket(userMock, 1);
            shoppingBasketUnderTest.addProductToShoppingBasket(userMock, 2);
        } catch (ProdcutPolicyException e) {
            e.printStackTrace();
            fail("Unexpected ProdcutPolicyException exception thrown");
        }

        // Act
        double actual = 0.0;
        try {
            actual = shoppingBasketUnderTest.calculateShoppingBasketPrice();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception thrown");
        }
        
        // Assert
        assertTrue(actual == 300.0);
    }

    @Test
    public void testPrintAllProducts_whenBasketIsEmpty_shouldReturnEmptyString() {
        // Arrange
        shoppingBasketUnderTest = new ShoppingBasket(shopMock);

        // Act
        String actual = shoppingBasketUnderTest.printAllProducts();
        
        // Assert
        assertTrue(actual.equals(""));
    }

    @Test
    public void testClone_whenBasketIsEmpty_shouldReturnEmptyBasket() {
        // Arrange
        shoppingBasketUnderTest = new ShoppingBasket(shopMock);

        // Act
        ShoppingBasket actual = shoppingBasketUnderTest.clone();
        
        // Assert
        try {
            assertTrue(actual.getProductsList().size() == 0);
        } catch (ProductDoesNotExistsException e) {
            e.printStackTrace();
            fail("Unexpected ProductDoesNotExistsException exception thrown");
        }
    }

    @Test
    public void testClone_whenBasketIsNotEmpty_shouldReturnClonedBasket() throws ProductDoesNotExistsException {
        // Arrange
        Product product1 = new Product(1, "Product 1", Category.ELECTRONICS, 100.0);
        Product product2 = new Product(2, "Product 2", Category.ELECTRONICS, 200.0);
        when(shopMock.getProductById(1)).thenReturn(product1);
        when(shopMock.getProductById(2)).thenReturn(product2);

        shoppingBasketUnderTest = new ShoppingBasket(shopMock);

        try {
            shoppingBasketUnderTest.addProductToShoppingBasket(userMock, 1);
            shoppingBasketUnderTest.addProductToShoppingBasket(userMock, 2);
        } catch (ProdcutPolicyException e) {
            e.printStackTrace();
            fail("Unexpected ProdcutPolicyException exception thrown");
        }

        // Act
        ShoppingBasket actual = shoppingBasketUnderTest.clone();
        
        // Assert
        assertTrue(actual.getProductsList().size() == 2);
    }

    @Test
    public void testGetShoppingBasketPrice_whenBasketIsEmpty_shouldReturnZero() {
        // Arrange
        shoppingBasketUnderTest = new ShoppingBasket(shopMock);

        // Act
        double actual = -1;
        try {
            actual = shoppingBasketUnderTest.getShoppingBasketPrice();
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Unexpected StockMarketException exception thrown");
        }
        
        // Assert
        assertTrue(actual == 0.0);
    }

    @Test
    public void testGetShoppingBasketPrice_whenBasketIsNotEmpty_shouldReturnTotalPrice() throws ProductDoesNotExistsException {
        // Arrange
        Product product1 = new Product(1, "Product 1", Category.ELECTRONICS, 100.0);
        Product product2 = new Product(2, "Product 2", Category.ELECTRONICS, 200.0);
        when(shopMock.getProductById(1)).thenReturn(product1);
        when(shopMock.getProductById(2)).thenReturn(product2);

        shoppingBasketUnderTest = new ShoppingBasket(shopMock);

        try {
            shoppingBasketUnderTest.addProductToShoppingBasket(userMock, 1);
            shoppingBasketUnderTest.addProductToShoppingBasket(userMock, 2);
        } catch (ProdcutPolicyException e) {
            e.printStackTrace();
            fail("Unexpected ProdcutPolicyException exception thrown");
        }

        // Act
        double actual = -1;
        try {
            actual = shoppingBasketUnderTest.getShoppingBasketPrice();
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Unexpected StockMarketException exception thrown");
        }
        
        // Assert
        assertTrue(actual == 300.0);
    }

    @Test
    public void testGetShop_whenBasketIsNotEmpty_shouldReturnShop() {
        // Arrange
        shoppingBasketUnderTest = new ShoppingBasket(shopMock);

        // Act
        Shop actual = shoppingBasketUnderTest.getShop();
        
        // Assert
        assertTrue(actual.equals(shopMock));
    }

    @Test
    public void testGetProductsList_whenBasketIsEmpty_shouldReturnEmptyList() {
        // Arrange
        shoppingBasketUnderTest = new ShoppingBasket(shopMock);

        // Act
        int actual = -1;
        try {
            actual = shoppingBasketUnderTest.getProductsList().size();
        } catch (ProductDoesNotExistsException e) {
            e.printStackTrace();
            fail("Unexpected ProductDoesNotExistsException exception thrown");
        }
        
        // Assert
        assertTrue(actual == 0);
    }

    @Test
    public void testGetProductsList_whenBasketIsNotEmpty_shouldReturnAllProducts() throws ProductDoesNotExistsException {
        // Arrange
        Product product1 = new Product(1, "Product 1", Category.ELECTRONICS, 100.0);
        Product product2 = new Product(2, "Product 2", Category.ELECTRONICS, 200.0);
        when(shopMock.getProductById(1)).thenReturn(product1);
        when(shopMock.getProductById(2)).thenReturn(product2);

        shoppingBasketUnderTest = new ShoppingBasket(shopMock);

        try {
            shoppingBasketUnderTest.addProductToShoppingBasket(userMock, 1);
            shoppingBasketUnderTest.addProductToShoppingBasket(userMock, 2);
        } catch (ProdcutPolicyException e) {
            e.printStackTrace();
            fail("Unexpected ProdcutPolicyException exception thrown");
        }

        // Act
        int actual = shoppingBasketUnderTest.getProductsList().size();
        
        // Assert
        assertTrue(actual == 2);
    }

    @Test
    public void testGetProductIdList_whenBasketIsEmpty_shouldReturnEmptyList() {
        // Arrange
        shoppingBasketUnderTest = new ShoppingBasket(shopMock);

        // Act
        int actual = shoppingBasketUnderTest.getProductIdList().size();
        
        // Assert
        assertTrue(actual == 0);
    }

    @Test
    public void testGetProductIdList_whenBasketIsNotEmpty_shouldReturnAllProductIds() throws ProductDoesNotExistsException {
        // Arrange
        Product product1 = new Product(1, "Product 1", Category.ELECTRONICS, 100.0);
        Product product2 = new Product(2, "Product 2", Category.ELECTRONICS, 200.0);
        when(shopMock.getProductById(1)).thenReturn(product1);
        when(shopMock.getProductById(2)).thenReturn(product2);

        shoppingBasketUnderTest = new ShoppingBasket(shopMock);

        try {
            shoppingBasketUnderTest.addProductToShoppingBasket(userMock, 1);
            shoppingBasketUnderTest.addProductToShoppingBasket(userMock, 2);
        } catch (ProdcutPolicyException e) {
            e.printStackTrace();
            fail("Unexpected ProdcutPolicyException exception thrown");
        }

        // Act
        int actual = shoppingBasketUnderTest.getProductIdList().size();
        
        // Assert
        assertTrue(actual == 2);
    }

    @Test
    public void testGetShopId_whenBasketIsEmpty_shouldReturnZero() {
        // Arrange
        shoppingBasketUnderTest = new ShoppingBasket(shopMock);

        // Act
        int actual = shoppingBasketUnderTest.getShopId();
        
        // Assert
        assertTrue(actual == 0);
    }

    @Test
    public void testGetShopId_whenBasketIsNotEmpty_shouldReturnShopId() {
        // Arrange
        shoppingBasketUnderTest = new ShoppingBasket(shopMock);

        // Act
        int actual = shoppingBasketUnderTest.getShopId();
        
        // Assert
        assertTrue(actual == 0);
    }

    @Test
    public void testPurchaseBasket_whenBasketMeetsShopPolicyAndEverythingInStock_thenReturnedTrue() throws ProdcutPolicyException,
     ShopPolicyException, ProductAlreadyExistsException, PermissionException, StockMarketException {
        // Arrange
        Date date = new Date();
        date.setTime(0);
        User buyer = new User("username1", "password1", "email1", date);
        Shop shop = new Shop(1, "ownerUsername", "bank1", "address1");
        ShoppingBasket shoppingBasket = new ShoppingBasket(shop);
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(3);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product(2, "product2", Category.ELECTRONICS, 100.0);
        product2.updateProductQuantity(10);
        shop.addProductToShop("ownerUsername", product2);
        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product2.getProductId());

        // Act
        boolean result = shoppingBasket.purchaseBasket();
        
        // Assert
        assertEquals(result, true);
        assertEquals(product.getProductQuantity(), 0);
        assertEquals(product2.getProductQuantity(), Integer.valueOf(9));
    }

    @Test
    public void testPurchaseBasket_whenBasketMeetsShopPolicyAndFirstProductInStockSecondCompletlyNotInStock_thenReturnedFalseAndRestockFirstProduct() throws ProdcutPolicyException,
     ShopPolicyException, ProductAlreadyExistsException, PermissionException, StockMarketException {
        // Arrange
        Date date = new Date();
        date.setTime(0);
        User buyer = new User("username1", "password1", "email1", date);
        Shop shop = new Shop(1, "ownerUsername", "bank1", "address1");
        ShoppingBasket shoppingBasket = new ShoppingBasket(shop);
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(3);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product(2, "product2", Category.ELECTRONICS, 100.0);
        product2.updateProductQuantity(0);
        shop.addProductToShop("ownerUsername", product2);
        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product2.getProductId());

        // Act
        boolean result = shoppingBasket.purchaseBasket();
        
        // Assert
        assertEquals(result, false);
        assertEquals(product.getProductQuantity(), 3);
        assertEquals(product2.getProductQuantity(), 0);
    }

    @Test
    public void testPurchaseBasket_whenBasketMeetsShopPolicyAndFirstProductInStockSecondSomeInStock_thenReturnedFalseAndRestockProducts() throws ProdcutPolicyException,
     ShopPolicyException, ProductAlreadyExistsException, PermissionException, StockMarketException {
        // Arrange
        Date date = new Date();
        date.setTime(0);
        User buyer = new User("username1", "password1", "email1", date);
        Shop shop = new Shop(1, "ownerUsername", "bank1", "address1");
        ShoppingBasket shoppingBasket = new ShoppingBasket(shop);
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(3);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product(2, "product2", Category.ELECTRONICS, 100.0);
        product2.updateProductQuantity(1);
        shop.addProductToShop("ownerUsername", product2);
        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product2.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product2.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product2.getProductId());

        // Act
        boolean result = shoppingBasket.purchaseBasket();
        
        // Assert
        assertEquals(result, false);
        assertEquals(product.getProductQuantity(), 3);
        assertEquals(product2.getProductQuantity(), 1);
    }
}
