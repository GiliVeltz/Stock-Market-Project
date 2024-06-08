package Domain.Facades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.web.bind.annotation.RestController;

import Domain.ShoppingCart;
import Domain.User;
import Domain.Repositories.MemoryShoppingCartRepository;
import Domain.Repositories.ShoppingCartRepositoryInterface;
import Dtos.PurchaseCartDetailsDto;
import Exceptions.PaymentFailedException;
import Exceptions.ProdcutPolicyException;
import Exceptions.ProductDoesNotExistsException;
import Exceptions.ShippingFailedException;
import Exceptions.StockMarketException;

@RestController
public class ShoppingCartFacade {
    private static ShoppingCartFacade _shoppingCartFacade;
    Map<String, ShoppingCart> _guestsCarts; // <guestID, ShoppingCart>
    ShoppingCartRepositoryInterface _cartsRepo;
    private static final Logger logger = Logger.getLogger(ShoppingCartFacade.class.getName());

    public ShoppingCartFacade() {
        _guestsCarts = new HashMap<>();
        _cartsRepo = new MemoryShoppingCartRepository();
    }

    // Public method to provide access to the _shoppingCartFacade
    public static synchronized ShoppingCartFacade getShoppingCartFacade() {
        if (_shoppingCartFacade == null) {
            _shoppingCartFacade = new ShoppingCartFacade();
        }
        return _shoppingCartFacade;
    }

    /*
     * Add a cart for a guest by token.
     */
    public void addCartForGuest(String guestID) {
        ShoppingCart cart = new ShoppingCart();
        _guestsCarts.put(guestID, cart);
    }

    /*
     * Add a cart for a user.
     * If user already has a cart - we will use the same cart as before.
     * If user don't have a cart (Just registerd/ already purchase the cart) - we
     * will use it's guest cart
     */
    public void addCartForUser(String guestID, User user) {
        if (_cartsRepo.getCartByUsername(user.getUserName()) == null) {
            _cartsRepo.addCartForUser(user.getUserName(), _guestsCarts.get(guestID));
        }
        
        // add the user to the cart
        _cartsRepo.getCartByUsername(user.getUserName()).SetUser(user);
    }

    public void addProductToUserCart(String userName, int productID, int shopID) throws ProdcutPolicyException, ProductDoesNotExistsException {
        ShoppingCart cart = _cartsRepo.getCartByUsername(userName);
        if (cart != null) {
            cart.addProduct(productID, shopID);
            logger.log(Level.INFO, "Product added to user's cart: " + userName);
        } else {
            logger.log(Level.WARNING, "User cart not found: " + userName);
        }
    }

    public void addProductToGuestCart(String guestID, int productID, int shopID) throws ProdcutPolicyException, ProductDoesNotExistsException {
        ShoppingCart cart = _guestsCarts.get(guestID);
        if (cart != null) {
            cart.addProduct(productID, shopID);
            logger.log(Level.INFO, "Product added to guest's cart: " + guestID);
        } else {
            logger.log(Level.WARNING, "Guest cart not found: " + guestID);
        }
    }

    public void removeProductFromUserCart(String userName, int productID, int shopID) {
        ShoppingCart cart = _cartsRepo.getCartByUsername(userName);
        if (cart != null) {
            cart.removeProduct(productID, shopID);
            logger.log(Level.INFO, "Product removed from guest's cart: " + userName);
        } else {
            logger.log(Level.WARNING, "User cart not found: " + userName);
        }
    }

    public void removeProductFromGuestCart(String guestID, int productID, int shopID) {
        ShoppingCart cart = _guestsCarts.get(guestID);
        if (cart != null) {
            cart.removeProduct(productID, shopID);
            logger.log(Level.INFO, "Product removed from guest's cart: " + guestID);
        } else {
            logger.log(Level.WARNING, "Guest cart not found: " + guestID);
        }
    }

    /*
     * Remove a cart from a guest user by token.
     * This method called when a guest user leave the system.
     */
    public void removeCartForGuest(String guestID) {
        _guestsCarts.remove(guestID);
    }

    public void purchaseCartGuest(String guestID, PurchaseCartDetailsDto details)
            throws PaymentFailedException, ShippingFailedException, StockMarketException {
        ArrayList<Integer> allBaskets = new ArrayList<Integer>();

        for (int i = 0; i < _guestsCarts.get(guestID).getCartSize(); i++)
            allBaskets.add(i + 1);
        logger.log(Level.INFO, "Start purchasing cart for guest.");
        details.basketsToBuy = allBaskets;
        _guestsCarts.get(guestID).purchaseCart(details);
    }

    public void purchaseCartUser(String username, PurchaseCartDetailsDto details)
            throws PaymentFailedException, ShippingFailedException, StockMarketException {
        logger.log(Level.INFO, "Start purchasing cart for user.");
        _cartsRepo.getCartByUsername(username).purchaseCart(details);
    }
}
