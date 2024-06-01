package Domain;

import java.util.ArrayList;
import java.util.List;
//import java.util.logging.Logger;

public class User {
    private String _encoded_password;
    private String _username;
    private boolean _isAdmin;
    private String _email;
    private List<Order> _purchaseHistory;
    //private static final Logger logger = Logger.getLogger(UserFacade.class.getName());

    public User(String username, String encoded_password, String email) {
        _username = username;
        _encoded_password = encoded_password;
        _isAdmin = false;
        _email = email;
        _purchaseHistory = new ArrayList<Order>();
        
    }

    public String getEncodedPassword() {
        return _encoded_password;
    }

    public boolean isCurrUser(String username, String encoded_password) {
        if (_username == username & _encoded_password == encoded_password) {
            return true;
        }
        return false;
    }

    //add order to the purchase history
    public void addOrder(Order order) {
        if (order == null) {
            //logger.severe("Order is null");
            throw new IllegalArgumentException("Order is null");
            
        }
        _purchaseHistory.add(order);
    }

    /**
     * Checks if the user is an admin.
     *
     * @return A boolean indicating whether the user is an admin.
     */
    public boolean isAdmin() {
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

    // getters and setters:

    public String getUserName() {
        return _username;
    }

    public String getEmail() {
        return _email;
    }

    public void setEmail(String email) {
        _email = email;
        }

    public String getPassword() {
        return _encoded_password;
    }

    public void setPassword(String password) {
        _encoded_password = password;
    }

    public String toString() {
        return "User [username=" + _username + ", encoded password= " + _encoded_password + ", email=" + _email + "]";
    }
}
