package AcceptanceTests.Implementor;

// A real conection to the system.
// The code is tested on the real information on te system.
public class RealBridge implements BridgeInterface{
    
    public boolean Login(String username, String password) {
        return false;
        // TODO: Implement this method.
        // call to the user serive to login in the service layer.
    }
}
