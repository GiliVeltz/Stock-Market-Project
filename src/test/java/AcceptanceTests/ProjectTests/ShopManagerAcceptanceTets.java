package AcceptanceTests.ProjectTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Disabled;

import AcceptanceTests.Implementor.BridgeInterface;
import AcceptanceTests.Implementor.RealBridge;

@ExtendWith(RealBridge.class)

public class ShopManagerAcceptanceTets {
    
        // Fields.
        private BridgeInterface _bridge;
    
        // constructor.
        public ShopManagerAcceptanceTets(RealBridge bridge) {
            _bridge = bridge;
        }
        
        // Test that shop manager can do only what the shop owner allowed him.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
        @Test
        public void testOpenMarketSystem() {
            assertTrue(_bridge.testOpenMarketSystem("shopManager", "shopId", "possiblePermission") ); // success
            assertFalse(_bridge.testOpenMarketSystem("shopManager", "shopId", "inPossiblePermission") ); // fail
        }
    
}
