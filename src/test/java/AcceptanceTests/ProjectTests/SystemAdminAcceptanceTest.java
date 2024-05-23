package AcceptanceTests.ProjectTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Disabled;

import AcceptanceTests.Implementor.BridgeInterface;
import AcceptanceTests.Implementor.RealBridge;
import net.bytebuddy.implementation.bind.annotation.IgnoreForBinding;

@ExtendWith(RealBridge.class)
public class SystemAdminAcceptanceTest {
    
        // Fields.
        private BridgeInterface _bridge;
    
        // constructor.
        public SystemAdminAcceptanceTest(RealBridge bridge) {
            _bridge = bridge;
        }
        
        @Disabled("This test is disabled cuase needs to implement in real bridge")
        // Test system nanger can see at any time the history purchased of the users and the shops.
        @Test
        public void testSystemManagerViewHistoryPurcaseInUsers() {
            assertTrue(_bridge.testSystemManagerViewHistoryPurcaseInUsers("manager", "userName") ); // success
            assertFalse(_bridge.testSystemManagerViewHistoryPurcaseInUsers("manager", "invalidUserName") ); // fail - invalid user name
            assertFalse(_bridge.testSystemManagerViewHistoryPurcaseInUsers("guest", "userName") ); // fail - not the system manager
        }
        
        @Disabled("This test is disabled cuase needs to implement in real bridge")
        @Test
        public void testSystemManagerViewHistoryPurcaseInShops() {
            assertTrue(_bridge.testSystemManagerViewHistoryPurcaseInShops("manager", "shopId1") ); // success
            assertFalse(_bridge.testSystemManagerViewHistoryPurcaseInShops("manager", "shopId2") ); // fail - invalid shop Id
            assertFalse(_bridge.testSystemManagerViewHistoryPurcaseInShops("guest", "shopId1") ); // fail - not the system manager
        }
    
}
