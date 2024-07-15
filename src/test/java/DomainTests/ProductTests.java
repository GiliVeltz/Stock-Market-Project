package DomainTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import Domain.Entities.Product;
import Domain.Entities.Shop;
import Domain.Entities.enums.Category;
import Exceptions.ProductOutOfStockExepction;
import Exceptions.StockMarketException;

public class ProductTests {

    @Mock
    private Shop shop;
     
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testPurchaseProduct_whenProductInStock_thenStockUpdated() throws StockMarketException {
        // Arrange - Create a new Product object.
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);

        // Act
        product.purchaseProduct();

        // Assert
        assertEquals(9, product.getProductQuantity());
    }

    @Test
    public void testPurchaseProduct_whenProductOutOfStock_thenExceptionThrown() {
        // Arrange - Create a new Product object.
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(0);

        // Act & Assert
        assertThrows(ProductOutOfStockExepction.class, () -> {product.purchaseProduct();});
    }

    @Test
    public void testCancelPurchase_whenProductPurchased_thenStockUpdated() throws StockMarketException {
        // Arrange - Create a new Product object.
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        product.purchaseProduct();

        // Act
        product.cancelPurchase();

        // Assert
        assertEquals(10, product.getProductQuantity());
    }

    @Test
    public void testAddProductRating_whenProductRatingInRange_thenSuccess()  {
        // Arrange - Create a new Product object.
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        
        // Act
        try{
            product.addProductRating(5);
            product.addProductRating(3);

        }
        catch(Exception e)
        {
            fail("Error: failed to rating product, test should pass");
        }
        
        // Assert
        assertEquals(4, product.getProductRating());
    }

    @Test
    public void testAddProductRating_whenProductRatingOutOfRange_thenError()  {
        // Arrange - Create a new Product object.
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        
        // Act
        assertThrows(StockMarketException.class, () -> {product.addProductRating(9);});        
        assertThrows(StockMarketException.class, () -> {product.addProductRating(0);});        
        assertThrows(StockMarketException.class, () -> {product.addProductRating(-5);});        

    }

    
    @Test
    public void testIsPriceInRange_whenPriceInRange_thenSuccess()  {
        // Arrange - Create a new Product object.
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        product.setPrice(55);
        
        boolean result = product.isPriceInRange(10, 90);
        
        // Assert
        assertTrue(result);
    }

    @Test
    public void testIsPriceInRange_whenPriceOutOfRange_thenFalse()  {
        // Arrange - Create a new Product object.
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        product.setPrice(55);
        
        boolean result = product.isPriceInRange(10, 20);
        
        // Assert
        assertFalse(result);
    }

    @Test
    public void testAddReview_whenReviewAdded_thenSuccess()  {
        // Arrange - Create a new Product object.
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        
        // Act
        product.addReview("user1", "good product");
        product.addReview("user2", "bad product");
        
        // Assert
        assertEquals(2, product.getReviews().size());
    }

    @Test
    public void testAddReview_whenReviewAdded_thenCorrectReviewAdded()  {
        // Arrange - Create a new Product object.
        Product product = new Product("product1", Category.ELECTRONICS, 100.0, shop, 1);
        product.updateProductQuantity(10);
        
        // Act
        product.addReview("user1", "good product");
        product.addReview("user2", "bad product");
        
        // Assert
        assertEquals("good product", product.getReviews().get("user1"));
        assertEquals("bad product", product.getReviews().get("user2"));
    }
}
