package Domain;

import java.util.List;
//import java.util.logging.Logger;

public class User {
    private String _encoded_password;
    private String _username;
    private boolean _isAdmin;
    private String _email;
    private List<Order> _purchaseHistory;
    // private static final Logger logger =
    // Logger.getLogger(UserController.class.getName());

    public User(String username, String encoded_password, String email) {
        _username = username;
        _encoded_password = encoded_password;
        _isAdmin = false;
        _email = email;
    }

    public boolean isCurrUser(String username, String encoded_password) {
        if (_username == username & _encoded_password == encoded_password) {
            return true;
        }
        return false;
    }

    public String getUserName() {
        return _username;
    }

    public String getEncodedPassword() {
        return _encoded_password;
    }

    public String getEmail() {
        return _email;
    }

    public void setEmail(String email) {
        _email = email;
    }
    
    public boolean isAdmin(){
        return _isAdmin;
    }

    /**
     * Retrieves the user's purchase history.
     *
     * @return A list of ShoppingBasket objects representing the user's purchase
     *         history.
     */
    public List<Order> getPurchaseHistory() {
        return _purchaseHistory;
    }

}
