package AcceptanceTests.Implementor;

// A real conection to the system.
// The code is tested on the real information on te system.
public class RealBridge implements BridgeInterface{
    
    // Implement the missing method
    public boolean Login(String username, String password) {
        // TODO: Implement this method.
        // call to the user serive to login in the service layer.
        return false;
    }
}
