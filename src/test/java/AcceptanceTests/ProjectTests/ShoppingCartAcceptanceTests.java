package AcceptanceTests.ProjectTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import AcceptanceTests.Implementor.BridgeInterface;
import AcceptanceTests.Implementor.RealBridge;
import net.bytebuddy.implementation.bind.annotation.IgnoreForBinding;

@ExtendWith(RealBridge.class)
public class ShoppingCartAcceptanceTests {

    // Fields.
    private BridgeInterface _bridge;

    // constructor.
    public ShoppingCartAcceptanceTests(RealBridge bridge) {
        _bridge = bridge;
    }
    
    // Test try to add product to shopping cart as register and signed in user in the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testAddProductToShoppingCartAsUser() {
        assertTrue(_bridge.testAddProductToShoppingCartUser("userName", "product1", "shopId") ); // success
        assertFalse(_bridge.testAddProductToShoppingCartUser("userName", "product3", "shopId") ); // fail
    }
    
    // Test try to add product to shopping cart as guest in the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testAddProductToShoppingCartAsGuest() {
        assertTrue(_bridge.testAddProductToShoppingCartGuest("userName", "product1", "shopId") ); // success
        assertFalse(_bridge.testAddProductToShoppingCartGuest("userName", "product3", "shopId") ); // fail
    }
}
