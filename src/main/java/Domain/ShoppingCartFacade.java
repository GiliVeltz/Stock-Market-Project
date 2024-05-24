package Domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShoppingCartFacade {
    Map<String, ShoppingCart> _guestsCarts; // <guestID, ShoppingCart>
    Map<String, ShoppingCart> _usersCarts; // <Username, ShoppingCart>
    private static final Logger logger = Logger.getLogger(ShoppingCartFacade.class.getName());

    public ShoppingCartFacade() {
        _guestsCarts = new HashMap<>();
        _usersCarts = new HashMap<>();
    }

    /*
     * Add a cart for a guest by token.
     */
    public void addCartForGuest(String guestID) {
        ShoppingCart cart = new ShoppingCart();
        _guestsCarts.put(guestID, cart);
    }

    /*
     * Add a cart for a user by username.
     * This methos called only when a guest user register to the system.
     * The cart of the guest user will be added to the user's cart and then will be deleted in the guests carts.
     */
    public void addCartForUser(String guestID, String username) {
        _usersCarts.put(username, _guestsCarts.remove(guestID));
    }

    /*
     * Remove a cart from a guest user by token.
     * This method called when a guest user leave the system.
     */
    public void removeCartForGuest(String guestID) {
        _guestsCarts.remove(guestID);
    }

    public void purchaseCartGuest(String guestID,  String cardNumber, String address) {
        ArrayList<Integer> allBaskets = new ArrayList<Integer>();

        for (int i = 0; i < _guestsCarts.get(guestID).getCartSize(); i++) 
            allBaskets.add(i + 1);
        logger.log(Level.INFO, "Start purchasing cart for guest.");
        _guestsCarts.get(guestID).purchaseCart(allBaskets, cardNumber, address);
    }

    public void purchaseCartUser(String username, List<Integer> busketsToBuy, String cardNumber, String address) {
        logger.log(Level.INFO, "Start purchasing cart for user.");
        _usersCarts.get(username).purchaseCart(busketsToBuy, cardNumber, address);
    }
}
