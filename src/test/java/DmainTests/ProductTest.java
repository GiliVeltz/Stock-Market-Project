package DmainTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        Product product = new Product(1, "product1", Category.ELECTRONICS, 100.0);
        product.updateProductQuantity(10);

        // Act
        product.purchaseProduct();

        // Assert
        assertEquals(9, product.getProductQuantity());
    }

}
