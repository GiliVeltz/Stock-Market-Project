public class UserTests implements TestCases{

    // Fields.
    private BridgeInterface bridge;

    // constructor.
    public UserTests(BridgeInterface bridge) {
        this.bridge = bridge;
    }
    
    // Test if the user can login.
    public void TestUserLogin() {
        assertTrue(bridge.Login("bob","bobspassword") );
        assertTrue(bridge.Login("BOB","bobspassword") );
        assertFalse(bridge.Login("bob",""));
        assertFalse(bridge.Login("bob","BOBSPASSWORD") );
    }
}
