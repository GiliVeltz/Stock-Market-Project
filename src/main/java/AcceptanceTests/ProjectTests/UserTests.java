package AcceptanceTests.ProjectTests;
import AcceptanceTests.Implementor.BridgeInterface;

//import static org.junit.Assert.assertTrue;

public class UserTests implements TestCases{

    // Fields.
    private BridgeInterface bridge;

    // constructor.
    public UserTests(BridgeInterface bridge) {
        this.bridge = bridge;
    }
    
    // Test if the user can login.
    public void TestUserLogin() {
        // DOTO: FIX THIS TEST. ITS NOT RECOGNIZED THE ASSERTION.
        // assertTrue(bridge.Login("bob","bobspassword") );
        // assertTrue(bridge.Login("BOB","bobspassword") );
        // assertFalse(bridge.Login("bob",""));
        // assertFalse(bridge.Login("bob","BOBSPASSWORD") );
    }
}
