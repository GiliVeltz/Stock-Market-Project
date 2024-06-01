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
    // @Disabled("This test is disabled cuase needs to implement in real bridge")
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
    
    // Test senario of adding a new external service to the system.
    //@Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testAddExternalService() {
        assertTrue(_bridge.testAddExternalService("newSerivceName", "name", "phone", 111) ); // success
        assertFalse(_bridge.testAddExternalService("existSerivce", "name", "phone", 222) ); // fail - already exist
        assertFalse(_bridge.testAddExternalService("", "name", "phone", 222) ); // fail - empty name
        assertFalse(_bridge.testAddExternalService("existSerivce", "", "", 222) ); // fail - empty info person
    }
    
    // Test senario of change info of external service in the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void testChangeExternalService() {
        assertTrue(_bridge.testChangeExternalService(111, "newSerivceName", "name", "phone") ); // success
        assertFalse(_bridge.testChangeExternalService(222, "newSerivceName", "name", "phone") ); // fail - non exist external service with this id in the system
        assertFalse(_bridge.testChangeExternalService(222, "", "name", "phone") ); // fail - empty name
        assertFalse(_bridge.testChangeExternalService(222, "newSerivceName", "", "") ); // fail - empty info person
    }
}
