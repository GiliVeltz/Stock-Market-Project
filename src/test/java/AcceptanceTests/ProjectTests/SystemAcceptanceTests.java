package AcceptanceTests.ProjectTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Disabled;

import AcceptanceTests.Implementor.BridgeInterface;
import AcceptanceTests.Implementor.RealBridge;

@ExtendWith(RealBridge.class)
public class SystemAcceptanceTests {

    // Fields.
    private BridgeInterface _bridge;

    // constructor.
    public SystemAcceptanceTests(RealBridge bridge) {
        _bridge = bridge;
    }
    
    // Test senario open the market system for shopping.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testOpenMarketSystem() {
        assertTrue(_bridge.testOpenMarketSystem("systemAdmin") ); // success
        assertFalse(_bridge.testOpenMarketSystem("guest") ); // fail
    }
    
    // Test senario of payment in the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testPayment() {
        assertTrue(_bridge.testPayment("true cards details") ); // success
        assertFalse(_bridge.testPayment("error") ); // fail
    }
    
    // Test senario of shipping in the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testShipping() {
        assertTrue(_bridge.testShipping("true shipping details") ); // success
        assertFalse(_bridge.testShipping("error") ); // fail
    }
}
