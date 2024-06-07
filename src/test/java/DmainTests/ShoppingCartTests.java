package DmainTests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

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
        shoppingCartUnderTest = null;
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testAddProduct_whenProductDoesNotExists_shouldThrowProductDoesNotExistsException() {
        // Arrange
        shoppingCartUnderTest = new ShoppingCart();
        Product product = new Product(1, "product", Category.CLOTHING, 10);
        when(shopMock.getProductsByName("product")).thenReturn(null);

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
        }
    }

}
