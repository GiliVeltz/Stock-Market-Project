package DmainTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Domain.Product;
import Exceptions.ProductOutOfStockExepction;
import enums.Category;

public class ProductTest {
     
    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testPurchaseProduct_whenProductInStock_thenStockUpdated() throws ProductOutOfStockExepction {
        // Arrange - Create a new Product object.
        Product product = new Product("product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(10);

        // Act
        product.purchaseProduct();

        // Assert
        assertEquals(9, product.getProductQuantity());
    }

    @Test
    public void testPurchaseProduct_whenProductOutOfStock_thenExceptionThrown() {
        // Arrange - Create a new Product object.
        Product product = new Product("product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(0);

        // Act & Assert
        assertThrows(ProductOutOfStockExepction.class, () -> {product.purchaseProduct();});
    }

    @Test
    public void testCancelPurchase_whenProductPurchased_thenStockUpdated() throws ProductOutOfStockExepction {
        // Arrange - Create a new Product object.
        Product product = new Product("product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(10);
        product.purchaseProduct();

        // Act
        product.cancelPurchase();

        // Assert
        assertEquals(10, product.getProductQuantity());
    }

}
