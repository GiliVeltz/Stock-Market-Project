package DomainTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Domain.Entities.Order;
import Domain.Entities.ShoppingBasket;
import Domain.Entities.User;
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
        User user = new User("john_doe", "password123", "email@example.com", new Date());

        // Act - try to set a new email
        user.setEmail("newemail@example.com");

        // Assert - Verify that the email was set successfully
        assertEquals("newemail@example.com", user.getEmail());
    }

    @Test
    public void testGetPurchaseHistory_whenNoPurchaseHistory_thenEmptyList() {
        // Arrange - Create a new User object
        User user = new User("john_doe", "password123", "email@example.com", new Date());

        // Act - try to get the purchase history
        List<Order> purchaseHistory = user.getPurchaseHistory();

        // Assert - Verify that the purchase history is empty
        assertEquals(0, purchaseHistory.size());
    }

    @Test
    public void testAddOrder_whenNewOrder_thenOrderAdded() throws StockMarketException {
        // Arrange - Create a new User object
        User user = new User("john_doe", "password123", "email@example.com", new Date());
        Order order = new Order(new ArrayList<ShoppingBasket>(), 1, 1);
        
        // Act - try to add a new order
        user.addOrder(order);

        // Assert - Verify that the order was added successfully
        assertEquals(1, user.getPurchaseHistory().size());
    }


        
    @Test
    public void testIsCurrUser_whenCorrectUsernameAndPassword_thenTrue() {
        // Arrange - Create a new User object
        User user = new User("john_doe", "password123", "email@example.com", new Date());

        // Act - try to check if the user is the current user
        boolean isCurrUser = user.isCurrUser("john_doe", "password123");

        // Assert - Verify that the user is the current user
        assertEquals(true, isCurrUser);
    }

    @Test
    public void testAddOrder_whenNewOrderFails_thenOrderNotAdded() throws StockMarketException {
        // Arrange - Create a new User object
        User user = new User("john_doe", "password123", "email@example.com", new Date());
        
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
        User user = new User("john_doe", "password123", "email@example.com", new Date());

        // Act - try to check if the user is the current user
        boolean isCurrUser = user.isCurrUser("john_doe", "password");

        // Assert - Verify that the user is not the current user
        assertEquals(false, isCurrUser);
    }

    @Test
    public void testCheckAgeAbove_ReturnsTrue_WhenAgeAboveThreshold() {
        // Arrange
        Calendar calendar = Calendar.getInstance();
        calendar.set(1990, Calendar.JANUARY, 1); // Birth date: January 1, 1990
        Date birthDate = calendar.getTime();
        User user = new User("testUser", "password", "test@example.com", birthDate);

        // Act
        boolean result = user.checkAgeAbove(30); // Check if age is above 30

        // Assert
        Assertions.assertTrue(result);
    }

    @Test
    public void testCheckAgeAbove_ReturnsFalse_WhenAgeBelowThreshold() {
        // Arrange
        Calendar calendar = Calendar.getInstance();
        calendar.set(2000, Calendar.JANUARY, 1); // Birth date: January 1, 2000
        Date birthDate = calendar.getTime();
        User user = new User("testUser", "password", "test@example.com", birthDate);

        // Act
        boolean result = user.checkAgeAbove(30); // Check if age is above 30

        // Assert
        Assertions.assertFalse(result);
    }

    @Test
    public void testCheckAgeAbove_ReturnsTrue_WhenAgeEqualsThreshold() {
        // Arrange
        Calendar calendar = Calendar.getInstance();
        calendar.set(1995, Calendar.JANUARY, 1); // Birth date: January 1, 1995
        Date birthDate = calendar.getTime();
        User user = new User("testUser", "password", "test@example.com", birthDate);

        // Act
        boolean result = user.checkAgeAbove(25); // Check if age is above 25

        // Assert
        Assertions.assertTrue(result);
    }
}
