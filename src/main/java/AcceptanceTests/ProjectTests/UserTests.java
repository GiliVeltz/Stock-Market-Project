package AcceptanceTests.ProjectTests;
import AcceptanceTests.Implementor.BridgeInterface;
import AcceptanceTests.Implementor.RealBridge;

//import static org.junit.Assert.assertTrue;

public class UserTests implements TestCases{

    // Fields.
    private BridgeInterface bridge;

    // constructor.
    public UserTests(RealBridge bridge) {
        this.bridge = bridge;
    }
    
    // Test if the user can login.
    public void TestUserLogin() {
        // DOTO: FIX THIS TEST. ITS NOT RECOGNIZED THE ASSERTION.
        // assertTrue(bridge.TRYTOLOGIN("bob","bobspassword") );
        // assertTrue(bridge.Login("BOB","bobspassword") );
        // assertFalse(bridge.Login("bob",""));
        // assertFalse(bridge.Login("bob","BOBSPASSWORD") );
    }

    public void ShopOpenTest{
        
assertTrue(RealBridge.OpenMewShop("Success", "client123", 5555));
assertTrue(RealBridge.OpenMewShop("false", "client123", 5555));
    }
}
