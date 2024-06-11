package DomainTests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.concurrent.*;

import java.util.Date;

import org.hibernate.sql.exec.ExecutionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import Domain.Product;
import Domain.Shop;
import Domain.ShoppingBasket;
import Domain.User;
import Exceptions.ProdcutPolicyException;
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
    public void testAddProductToShoppingBasket_whenProductIsNotInBasket_shouldAddProductToBasket() throws StockMarketException {
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
    public void testAddProductToShoppingBasket_whenProductIsInBasket_shouldTheProductAddedMoreToBasket() throws StockMarketException {
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
    public void testAddProductToShoppingBasket_whenProductIsNotInShop_shouldThrowProdcutPolicyException() throws StockMarketException {
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
    public void testRemoveProductFromShoppingBasket_whenProductIsInBasket_shouldRemoveProductFromBasket() throws StockMarketException {
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
    public void testRemoveProductFromShoppingBasket_whenProductIsNotInBasket_shouldNotRemoveProductFromBasket() throws StockMarketException {
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
            shoppingBasketUnderTest.removeProductFromShoppingBasket(2);
            fail("Expected ProductDoesNotExistsException exception not thrown");
        } catch (ProductDoesNotExistsException e) {
            e.printStackTrace();
        }
        
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
    public void testCalculateShoppingBasketPrice_whenBasketIsNotEmpty_shouldReturnTotalPrice() throws StockMarketException {
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
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Unexpected StockMarketException exception thrown");
        }
    }

    @Test
    public void testClone_whenBasketIsNotEmpty_shouldReturnClonedBasket() throws StockMarketException {
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
    public void testGetShoppingBasketPrice_whenBasketIsNotEmpty_shouldReturnTotalPrice() throws StockMarketException {
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
        } catch (StockMarketException e) {
            e.printStackTrace();
            fail("Unexpected StockMarketException exception thrown");
        }
        
        // Assert
        assertTrue(actual == 0);
    }

    @Test
    public void testGetProductsList_whenBasketIsNotEmpty_shouldReturnAllProducts() throws StockMarketException {
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
    public void testGetProductIdList_whenBasketIsNotEmpty_shouldReturnAllProductIds() throws StockMarketException {
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
    public void testPurchaseBasket_whenBasketMeetsShopPolicyAndEverythingInStock_thenReturnedTrue() throws StockMarketException {
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
        assertTrue(result);
        assertEquals(product.getProductQuantity(), 0);
        assertEquals(product2.getProductQuantity(), 9);
    }

    @Test
    public void testPurchaseBasket_whenBasketMeetsShopPolicyAndFirstProductInStockSecondCompletlyNotInStock_thenReturnedFalseAndRestockFirstProduct() throws StockMarketException {
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
        assertFalse(result);
        assertEquals(product.getProductQuantity(), 3);
        assertEquals(product2.getProductQuantity(), 0);
    }

    @Test
    public void testPurchaseBasket_whenBasketMeetsShopPolicyAndFirstProductInStockSecondSomeInStock_thenReturnedFalseAndRestockProducts() throws StockMarketException {
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
        assertFalse(result);
        assertEquals(product.getProductQuantity(), 3);
        assertEquals(product2.getProductQuantity(), 1);
    }

    @Test
    public void testPurchaseBasket_whenBasketDoNotMeetsShopPolicyAndEverythingInStock_thenThrowsShopPolicyException() throws StockMarketException {
        // Arrange
        Date date = new Date();
        date.setTime(0);
        User buyer = new User("username1", "password1", "email1", date);
        ShoppingBasket shoppingBasket = new ShoppingBasket(shopMock);
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(3);
        shopMock.addProductToShop("ownerUsername", product);
        Product product2 = new Product(2, "product2", Category.ELECTRONICS, 100.0);
        product2.updateProductQuantity(10);
        shopMock.addProductToShop("ownerUsername", product2);
        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product2.getProductId());
        doThrow(new ShopPolicyException("Basket does not meet shop policy")).when(shopMock).ValidateBasketMeetsShopPolicy(shoppingBasket);

        // Act & Assert
        assertThrows(ShopPolicyException.class, () -> {
            shoppingBasket.purchaseBasket();
        });
        assertEquals(product.getProductQuantity(), 3);
        assertEquals(product2.getProductQuantity(), 10);
    }

    @Test
    public void testPurchaseBasket_whenBasketMeetsShopPolicyAndThereIsEnogthStockForOneBuyer_thenOneReturnTrueAndOneFalse() throws StockMarketException, java.util.concurrent.ExecutionException {
        // Arrange
        Date date = new Date();
        date.setTime(0);
        User buyer = new User("username1", "password1", "email1", date);
        User buyer2 = new User("username2", "password2", "email2", date);
        Shop shop = new Shop(1, "ownerUsername", "bank1", "address1");
        ShoppingBasket shoppingBasket = new ShoppingBasket(shop);
        ShoppingBasket shoppingBasket2 = new ShoppingBasket(shop);
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(2);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product(2, "product2", Category.ELECTRONICS, 100.0);
        product2.updateProductQuantity(1);
        shop.addProductToShop("ownerUsername", product2);

        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product2.getProductId());
        shoppingBasket2.addProductToShoppingBasket(buyer2, product.getProductId());
        shoppingBasket2.addProductToShoppingBasket(buyer2, product2.getProductId());
        final boolean[] results = new boolean[2];
        ExecutorService executor = Executors.newFixedThreadPool(2); // create a thread pool with 2 threads

        // Act
        // Task for first thread
        Callable<Boolean> task1 = () -> {
            try {
                return shoppingBasket.purchaseBasket();
            } catch (StockMarketException e) {
                return false;
            }
        };

        // Task for second thread
        Callable<Boolean> task2 = () -> {
            try {
                return shoppingBasket2.purchaseBasket();
            } catch (StockMarketException e) {
                return false;
            }
        };

        // Execute tasks
        Future<Boolean> future1 = executor.submit(task1);
        Future<Boolean> future2 = executor.submit(task2);

        try {
            results[0] = future1.get();
            results[1] = future2.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executor.shutdown(); // shut down executor service
        
        // Assert
        assertTrue((results[0] && !results[1]) || (!results[0] && results[1]));
        assertEquals(product.getProductQuantity(), 1);
        assertEquals(product2.getProductQuantity(), 0);
    }

    @Test
    public void testPurchaseBasket_whenBasketMeetsShopPolicyAndThereIsEnogthStockForEveryOne_thenEveryOneReturnTrue() throws StockMarketException, java.util.concurrent.ExecutionException {
        // Arrange
        Date date = new Date();
        date.setTime(0);
        User buyer = new User("username1", "password1", "email1", date);
        User buyer2 = new User("username2", "password2", "email2", date);
        Shop shop = new Shop(1, "ownerUsername", "bank1", "address1");
        ShoppingBasket shoppingBasket = new ShoppingBasket(shop);
        ShoppingBasket shoppingBasket2 = new ShoppingBasket(shop);
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(2);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product(2, "product2", Category.ELECTRONICS, 100.0);
        product2.updateProductQuantity(2);
        shop.addProductToShop("ownerUsername", product2);

        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product2.getProductId());
        shoppingBasket2.addProductToShoppingBasket(buyer2, product.getProductId());
        shoppingBasket2.addProductToShoppingBasket(buyer2, product2.getProductId());
        final boolean[] results = new boolean[2];
        ExecutorService executor = Executors.newFixedThreadPool(2); // create a thread pool with 2 threads

        // Act
        // Task for first thread
        Callable<Boolean> task1 = () -> {
            try {
                return shoppingBasket.purchaseBasket();
            } catch (StockMarketException e) {
                return false;
            }
        };

        // Task for second thread
        Callable<Boolean> task2 = () -> {
            try {
                return shoppingBasket2.purchaseBasket();
            } catch (StockMarketException e) {
                return false;
            }
        };

        // Execute tasks
        Future<Boolean> future1 = executor.submit(task1);
        Future<Boolean> future2 = executor.submit(task2);

        try {
            results[0] = future1.get();
            results[1] = future2.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executor.shutdown(); // shut down executor service
        
        // Assert
        assertTrue(results[0]&& results[1]);
        assertEquals(product.getProductQuantity(), 0);
        assertEquals(product2.getProductQuantity(), 0);
    }

    @Test
    public void testCancelPurchase_whenWholeBasketNeedToBeCanceled_thenRestockProducts() throws StockMarketException {
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
        product2.updateProductQuantity(3);
        shop.addProductToShop("ownerUsername", product2);

        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product2.getProductId());

        // Act
        shoppingBasket.cancelPurchase();
        
        // Assert
        assertEquals(product.getProductQuantity(), 4);
        assertEquals(product2.getProductQuantity(), 4);
    }

    @Test
    public void testCancelPurchase_whenTwoBasketWithSharedProductsNeedToBeCanceled_thenRestockProducts() throws StockMarketException {
        // Arrange
        Date date = new Date();
        date.setTime(0);
        User buyer = new User("username1", "password1", "email1", date);
        User buyer2 = new User("username2", "password2", "email2", date);
        Shop shop = new Shop(1, "ownerUsername", "bank1", "address1");
        ShoppingBasket shoppingBasket = new ShoppingBasket(shop);
        ShoppingBasket shoppingBasket2 = new ShoppingBasket(shop);
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(3);
        shop.addProductToShop("ownerUsername", product);
        Product product2 = new Product(2, "product2", Category.ELECTRONICS, 100.0);
        product2.updateProductQuantity(3);
        shop.addProductToShop("ownerUsername", product2);

        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product2.getProductId());
        shoppingBasket2.addProductToShoppingBasket(buyer2, product.getProductId());
        shoppingBasket2.addProductToShoppingBasket(buyer2, product2.getProductId());

        ExecutorService executor = Executors.newFixedThreadPool(2); // create a thread pool with 2 threads

        // Act
        // Task for first thread
        Runnable task1 = () -> {
            try {
                shoppingBasket.cancelPurchase();
            } catch (StockMarketException e) {
                e.printStackTrace();
            }
        };

        // Task for second thread
        Runnable task2 = () -> {
            try {
                shoppingBasket2.cancelPurchase();
            } catch (StockMarketException e) {
                e.printStackTrace();
            }
        };

        // Execute tasks
        executor.execute(task1);
        executor.execute(task2);

        // Initiates an orderly shutdown in which previously submitted tasks are executed, but no new tasks will be accepted
        executor.shutdown();

        try {
            // Wait a while for existing tasks to terminate
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Cancel currently executing tasks

                // Wait a while for tasks to respond to being cancelled
                if (!executor.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executor.shutdownNow();

            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
        
        // Assert
        assertEquals(product.getProductQuantity(), 5);
        assertEquals(product2.getProductQuantity(), 5);
    }

    @Test
    public void testCalculateShoppingBasketPrice_whenBasketPriceNeedToBeCalculate_thenReturnBasketPriceAfterDiscounts() throws StockMarketException {
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
        product2.updateProductQuantity(3);
        shop.addProductToShop("ownerUsername", product2);

        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product.getProductId());
        shoppingBasket.addProductToShoppingBasket(buyer, product2.getProductId());

        // Act
        double price = shoppingBasket.calculateShoppingBasketPrice();
        
        // Assert
        assertEquals(price, 300);
    }

}
