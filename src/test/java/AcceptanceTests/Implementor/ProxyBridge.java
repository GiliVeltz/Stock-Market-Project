package AcceptanceTests.Implementor;

// Proxy is a structural design pattern that lets you provide a substitute or placeholder for another object.
// A proxy controls access to the original object, allowing you to perform something either before or after the request gets through to the original object.
public class ProxyBridge implements BridgeInterface{
    
    // important: all the functions will return false because the real implementation is in the RealBridge class

    public boolean testRegisterToTheSystem(String username, String password, String email) {
        return false;
    }
}
