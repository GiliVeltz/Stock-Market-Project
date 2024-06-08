package AcceptanceTests.ProjectTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import AcceptanceTests.Implementor.BridgeInterface;
import AcceptanceTests.Implementor.RealBridge;

@ExtendWith(RealBridge.class)
public class SystemAdminAcceptanceTests {
    
        // Fields.
        private BridgeInterface _bridge;
    
        // constructor.
        public SystemAdminAcceptanceTests(RealBridge bridge) {
            _bridge = bridge;
        }
        
        // Test system nanger can see at any time the history purchased of the users and the shops.
        @Test
        public void testSystemManagerViewHistoryPurcaseInUsers() {
            assertTrue(_bridge.testSystemManagerViewHistoryPurcaseInUsers("manager", "userName") ); // success
            assertFalse(_bridge.testSystemManagerViewHistoryPurcaseInUsers("manager", "invalidUserName") ); // fail - invalid user name
            assertFalse(_bridge.testSystemManagerViewHistoryPurcaseInUsers("guest", "userName") ); // fail - not the system manager
        }
        
        @Test
        public void testSystemManagerViewHistoryPurcaseInShops() {
            assertTrue(_bridge.testSystemManagerViewHistoryPurcaseInShops("manager", 0) ); // success
            assertFalse(_bridge.testSystemManagerViewHistoryPurcaseInShops("manager", -1) ); // fail - invalid shop Id
            assertFalse(_bridge.testSystemManagerViewHistoryPurcaseInShops("guest", 0) ); // fail - not the system manager
        }
    
}
