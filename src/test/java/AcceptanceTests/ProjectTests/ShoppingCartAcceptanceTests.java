package AcceptanceTests.ProjectTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import AcceptanceTests.Implementor.BridgeInterface;
import AcceptanceTests.Implementor.RealBridge;

@ExtendWith(RealBridge.class)
public class ShoppingCartAcceptanceTests {

    // Fields.
    private BridgeInterface _bridge;

    // constructor.
    public ShoppingCartAcceptanceTests(RealBridge bridge) {
        _bridge = bridge;
    }
    
    // Test try to add product to shopping cart as register and signed in user in the system.
    @Test
    public void testAddProductToShoppingCartAsUser() {
        assertTrue(_bridge.testAddProductToShoppingCartUser("userName", "0", "0") ); // success - product in shop
        assertFalse(_bridge.testAddProductToShoppingCartUser("userName", "2", "0") ); // fail - product not in shop
    }
    
    // Test try to add product to shopping cart as guest in the system.
    @Test
    public void testAddProductToShoppingCartAsGuest() {
        assertTrue(_bridge.testAddProductToShoppingCartGuest("guestname", "0", "0") ); // success - product in shop
        assertFalse(_bridge.testAddProductToShoppingCartGuest("guestname", "2", "0") ); // fail - product not in shop
    }
}
