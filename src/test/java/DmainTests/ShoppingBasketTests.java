package DmainTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

import Domain.Shop;
import Domain.ShoppingBasket;
import Exceptions.ProductDoesNotExistsException;

public class ShoppingBasketTests {
    @Mock
    private Shop shop;
    private ShoppingBasket shoppingBasket;

    @BeforeEach
    public void setUp() {
        shop = Mockito.mock(Shop.class);
        shoppingBasket = new ShoppingBasket(shop);
    }

    @Test
    public void testAddProductToShoppingBasket_whenAddingProductToBasket_thenProductIsAdded() {
        // Arrange
        Integer productId = 1;

        // Act
        shoppingBasket.addProductToShoppingBasket(productId);

        // Assert
        assertTrue(shoppingBasket.getProductIdList().contains(productId));
    }
}