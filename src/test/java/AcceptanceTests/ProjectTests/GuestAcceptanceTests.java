package AcceptanceTests.ProjectTests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import AcceptanceTests.Implementor.BridgeInterface;
import AcceptanceTests.Implementor.RealBridge;

@ExtendWith(RealBridge.class)

public class GuestAcceptanceTests {

    // Fields.
    private BridgeInterface _bridge;

    // constructor.
    public GuestAcceptanceTests(RealBridge bridge) {
        _bridge = bridge;
    }
    
    // Test if the guest can enter the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void TestGuestEnterTheSystem() {
        assertTrue(_bridge.TestGuestEnterTheSystem("open") ); // success
        assertFalse(_bridge.TestGuestEnterTheSystem("close") ); // fail - already exists
    }
    
    // Test if the user can register to the system.
    @Test
    public void TestGuestRegisterToTheSystem() {
        assertTrue(_bridge.TestGuestRegisterToTheSystem("bob","bobspassword", "email") ); // success
        assertFalse(_bridge.TestGuestRegisterToTheSystem("bobi","bobspassword", "") ); // fail - already exists
        assertFalse(_bridge.TestGuestRegisterToTheSystem("","bobspassword", "email") ); // fail - empty username
        assertFalse(_bridge.TestGuestRegisterToTheSystem("mom","", "email") ); // fail - empty pasword
        assertFalse(_bridge.TestGuestRegisterToTheSystem("mom","momspassword", "")); // fail - empty email
    }
    
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    // Test if the guest can enter the system.
    @Test
    public void TestUserEnterTheSystem() {
        assertTrue(_bridge.TestUserEnterTheSystem("open") ); // success
        assertFalse(_bridge.TestUserEnterTheSystem("close") ); // fail - already exists
    }
    
    // Test if the user can login to the system.
    @Disabled("This test is disabled cuase needs to implement in real bridge")
    @Test
    public void TestUserLogin() {
        assertTrue(_bridge.testLoginToTheSystem("bob","bobspassword") ); // success
        assertFalse(_bridge.testLoginToTheSystem("","bobspassword") ); // fail - empty username
        assertFalse(_bridge.testLoginToTheSystem("bob","") ); // fail - empty pasword
        assertFalse(_bridge.testLoginToTheSystem("mom","momspassword")); // not a user in the system
    }
    
}
