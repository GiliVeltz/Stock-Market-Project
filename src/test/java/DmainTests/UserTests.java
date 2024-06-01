package DmainTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Domain.Order;
import Domain.ShoppingBasket;
import Domain.User;
import Exceptions.StockMarketException;

public class UserTests {

    // private fields.

    // mock fields.
    // @Mock

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testSetEmail_whenNewEmail_thenEmailSet() {
        // Arrange - Create a new User object
        User user = new User("john_doe", "password123", "email");

        // Act - try to set a new email
        user.setEmail("newEmail");

        // Assert - Verify that the email was set successfully
        assertEquals("newEmail", user.getEmail());
    }

    @Test
    public void testGetPurchaseHistory_whenNoPurchaseHistory_thenEmptyList() {
        // Arrange - Create a new User object
        User user = new User("john_doe", "password123", "email");

        // Act - try to get the purchase history
        List<Order> purchaseHistory = user.getPurchaseHistory();

        // Assert - Verify that the purchase history is empty
        assertEquals(0, purchaseHistory.size());
    }

    @Test
    public void testAddOrder_whenNewOrder_thenOrderAdded() throws StockMarketException {
        // Arrange - Create a new User object
        User user = new User("john_doe", "password123", "email");
        Order order = new Order(1, new ArrayList<ShoppingBasket>());
        
        // Act - try to add a new order
        user.addOrder(order);

        // Assert - Verify that the order was added successfully
        assertEquals(1, user.getPurchaseHistory().size());
    }


        
    @Test
    public void testIsCurrUser_whenCorrectUsernameAndPassword_thenTrue() {
        // Arrange - Create a new User object
        User user = new User("john_doe", "password123", "email");

        // Act - try to check if the user is the current user
        boolean isCurrUser = user.isCurrUser("john_doe", "password123");

        // Assert - Verify that the user is the current user
        assertEquals(true, isCurrUser);
    }

    @Test
    public void testAddOrder_whenNewOrderFails_thenOrderNotAdded() throws Exception {
        // Arrange - Create a new User object
        User user = new User("john_doe", "password123", "email");
        
        // Act - try to add a new order
        try {
            user.addOrder(null);
            fail("Expected StockMarketException to be thrown");
        } catch (Exception e) {
            // Assert - Verify that the order was not added
            assertEquals(0, user.getPurchaseHistory().size());
        }
    }

    @Test
    public void testIsCurrUser_whenIncorrectUsernameAndPassword_thenFalse() {
        // Arrange - Create a new User object
        User user = new User("john_doe", "password123", "email");

        // Act - try to check if the user is the current user
        boolean isCurrUser = user.isCurrUser("john_doe", "password");

        // Assert - Verify that the user is not the current user
        assertEquals(false, isCurrUser);
    }
}
