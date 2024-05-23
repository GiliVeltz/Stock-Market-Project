package Domain;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Domain.Exceptions.PaymentFailedException;
import Domain.Exceptions.ShippingFailedException;
import Domain.ExternalServices.PaymentService.ProxyPayment;
import Domain.ExternalServices.SupplyService.ProxySupply;

public class User {
    private String _encoded_password;
    private String _username;
    private boolean _isAdmin;
    private String _email;
    private ShoppingCart _shoppingCart;
    private List<Order> _purchaseHistory;
    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    public User(String username, String encoded_password, String email) {
        _username = username;
        _encoded_password = encoded_password;
        _isAdmin = false;
        _email = email;
        _shoppingCart = new ShoppingCart();
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

    /*
     * This method is responsible for purchasing the cart.
     * It first calls the purchaseCart method of the shopping cart which reaponsible
     * for changing the item's stock.
     * Then it tries to pay and deliver the items.
     * If the payment or the delivery fails, it cancels the purchase and restock the
     * item.
     */
    public void purchaseCart(List<Integer> busketsToBuy, PaymentMethod paymentMethod, SupplyMethod shippingMethod) {
        logger.log(Level.INFO, "User " + _username + " is trying to purchase the cart.");
        _shoppingCart.purchaseCart(this, busketsToBuy);
        try {
            paymentMethod.pay();
            shippingMethod.deliver();
        } catch (PaymentFailedException e) {
            logger.log(Level.SEVERE,
                    "Payment for user: " + _username + " has been failed with exception: " + e.getMessage(), e);
            _shoppingCart.cancelPurchase(this, busketsToBuy);
            throw new PaymentFailedException("Payment failed");
        } catch (ShippingFailedException e) {
            logger.log(Level.SEVERE,
                    "Shipping for user: " + _username + " has been failed with exception: " + e.getMessage(), e);
            _shoppingCart.cancelPurchase(this, busketsToBuy);
            throw new ShippingFailedException("Shipping failed");
        }

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

}
