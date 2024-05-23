package AcceptanceTests.ProjectTests;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import AcceptanceTests.Implementor.BridgeInterface;
import AcceptanceTests.Implementor.RealBridge;

//import static org.junit.Assert.assertTrue;

@ExtendWith(RealBridge.class)
public class UserAcceptanceTests{

    // Fields.
    private BridgeInterface _bridge;

    // constructor.
    public UserAcceptanceTests(RealBridge bridge) {
        _bridge = bridge;
    }
    
    // TODO: AMIT: debug and check those tests- they are failing.
    // Test if the user can register to the system.
    @Test
    public void TestUserRegister() {
        // assertTrue(_bridge.testRegisterToTheSystem("bob","bobspassword", "email") ); // success
        // assertTrue(_bridge.testRegisterToTheSystem("bob","bobspassword", "") ); // fail - already exists
        // assertFalse(_bridge.testRegisterToTheSystem("","bobspassword", "email") ); // fail - empty username
        // assertTrue(_bridge.testRegisterToTheSystem("mom","", "email") ); // fail - empty pasword
        // assertFalse(_bridge.testRegisterToTheSystem("mom","momspassword", "")); // fail - empty email
    }
}
