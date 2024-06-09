package AcceptanceTests.ProjectTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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
        @Test
        public void testPermissionForShopManager() {
            assertTrue(_bridge.testPermissionForShopManager("shopManager", 0, "possiblePermission") ); // success
            assertFalse(_bridge.testPermissionForShopManager("shopManager", 1, "inPossiblePermission") ); // fail
        }
    
}
