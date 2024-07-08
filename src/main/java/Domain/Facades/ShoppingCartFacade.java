package Domain.Facades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Domain.Entities.Order;
import Domain.Entities.ShoppingBasket;
import Domain.Entities.ShoppingCart;
import Domain.Entities.User;
import Domain.Repositories.MemoryShoppingCartRepository;
import Domain.Repositories.DbShoppingCartRepository;
import Domain.Repositories.InterfaceShoppingCartRepository;
import Domain.Repositories.InterfaceUserRepository;
import Dtos.BasketDto;
import Dtos.PurchaseCartDetailsDto;
import Exceptions.StockMarketException;
import jakarta.transaction.Transactional;

@Service
public class ShoppingCartFacade {
    private static ShoppingCartFacade instance;
    Map<String, ShoppingCart> _guestsCarts; // <guestID, ShoppingCart>
    InterfaceShoppingCartRepository _cartsRepository;
    private static final Logger logger = Logger.getLogger(ShoppingCartFacade.class.getName());
    
    @Autowired
    public ShoppingCartFacade(DbShoppingCartRepository cartsRepository) {
        _cartsRepository = cartsRepository;
        _guestsCarts = new HashMap<>();
    }


    // set shopping cart repository to be used in real system
    public void setShoppingCartRepository(InterfaceShoppingCartRepository cartsRepo) {
        _cartsRepository = cartsRepo;
    }

    // Add a cart for a guest by token.
    @Transactional
    public void addCartForGuest(String guestID) {
        ShoppingCart cart = new ShoppingCart(guestID);
        _guestsCarts.put(guestID, cart);
    }

    /*
     * Add a cart for a user.
     * If user already has a cart - we will use the same cart as before.
     * If user don't have a cart (Just registerd/ already purchase the cart) - we
     * will use it's guest cart
     */
    @Transactional
    public void addCartForUser(String guestID, User user) {
        ShoppingCart cart = _cartsRepository.getCartByUsername(user.getUserName());
        if (cart == null) {
            _cartsRepository.save(new ShoppingCart(guestID));
            //_cartsRepository.addCartForUser(user.getUserName(), _guestsCarts.get(guestID));

        }
        System.out.println("test"+_cartsRepository.getCartByUsername(user.getUserName()));
        // add the user to the cart
        _cartsRepository.getCartByUsername(user.getUserName()).SetUser(user);
    }

    /*
     * Add a product to a user cart by username.
     * This method called when a user add a product to his cart.
     */
    @Transactional
    public void addProductToUserCart(String userName, int productID, int shopID, int quantity) throws StockMarketException {
        ShoppingCart cart = _cartsRepository.getCartByUsername(userName);
        if (cart != null) {
            cart.addProduct(productID, shopID, quantity);
            logger.log(Level.INFO, "Product added to user's cart: " + userName);
        } else {
            logger.log(Level.WARNING, "User cart not found: " + userName);
        }
    }

    /*
     * Add a product to a guest cart by token.
     * This method called when a guest user add a product to his cart.
     */
    @Transactional
    public void addProductToGuestCart(String guestID, int productID, int shopID, int quantity) throws StockMarketException {
        ShoppingCart cart = _guestsCarts.get(guestID);
        if (cart != null) {
            cart.addProduct(productID, shopID, quantity);
            logger.log(Level.INFO, "Product added to guest's cart: " + guestID);
        } else {
            logger.log(Level.WARNING, "Guest cart not found: " + guestID);
        }
    }

    /*
     * Remove a product from a user cart by username.
     * This method called when a user remove a product from his cart.
     */
    @Transactional
    public void removeProductFromUserCart(String userName, int productID, int shopID) throws StockMarketException {
        ShoppingCart cart = _cartsRepository.getCartByUsername(userName);
        if (cart != null) {
            cart.removeProduct(productID, shopID);
            logger.log(Level.INFO, "Product removed from guest's cart: " + userName);
        } else {
            logger.log(Level.WARNING, "User cart not found: " + userName);
        }
    }

    /*
     * Remove a product from a guest user cart by token.
     * This method called when a guest user remove a product from his cart.
     */
    @Transactional
    public void removeProductFromGuestCart(String guestID, int productID, int shopID) throws StockMarketException {
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
    @Transactional
    public void removeCartForGuest(String guestID) {
        _guestsCarts.remove(guestID);
    }

    /*
     * Remove a cart from a user by username.
     * This method called when a user leave the system.
     */
     @Transactional
    public void purchaseCartGuest(String guestID, PurchaseCartDetailsDto details) throws StockMarketException {
        ArrayList<Integer> allBaskets = new ArrayList<Integer>();

        for (int i = 0; i < _guestsCarts.get(guestID).getCartSize(); i++)
            allBaskets.add(i);
        logger.log(Level.INFO, "Start purchasing cart for guest.");
        details.basketsToBuy = allBaskets;
        _guestsCarts.get(guestID).purchaseCart(details, _cartsRepository.getUniqueOrderID());
    }

    /*
     * Purchase the cart of a user.
     */
    @Transactional
    public void purchaseCartUser(String username, PurchaseCartDetailsDto details) throws StockMarketException {
        logger.log(Level.INFO, "Start purchasing cart for user.");
        _cartsRepository.getCartByUsername(username).purchaseCart(details, _cartsRepository.getUniqueOrderID());
    }

    // Getters
    public Map<String, ShoppingCart> get_guestsCarts() {
        return _guestsCarts;
    }

    /*
     * get user cart.
     * If user already has a cart - we will return the same cart as before.
     * If user don't have a cart (Just registerd/ already purchase the cart) - we
     * will use it's guest cart
     */
    @Transactional
    public ShoppingCart getUserCart(String username) throws StockMarketException {
        if (_cartsRepository.getCartByUsername(username) == null) {
            throw new StockMarketException("user does not have a cart");
        }
        return _cartsRepository.getCartByUsername(username);
    }

    /*
     * get guest cart.
     */
    @Transactional
    public ShoppingCart getGuestCart(String guest) throws StockMarketException {
        if (_guestsCarts.get(guest) == null) {
            throw new StockMarketException("guest does not have a cart");
        }
        return _guestsCarts.get(guest);
    }
  
    // this function checks for the product in the past purchases of the user, and if it exists, it returns the shopID.
    // next, this function will add a review on the product in the shop (if he still exists).
    @SuppressWarnings({ "null" })
    @Transactional
    public void writeReview(String username, List<Order> purchaseHistory, int productID, int shopID, String review) throws StockMarketException {
        // check if the user has purchased the product in the past using purchaseHistory.
        boolean foundProduct = false;
        ShoppingBasket shoppingBasket = null;
        for (Order order : purchaseHistory) {
            Map<Integer, ShoppingBasket> productsByShoppingBasket = order.getProductsByShoppingBasket();
            if (productsByShoppingBasket.containsKey(productID)){
                shoppingBasket = productsByShoppingBasket.get(productID);
                foundProduct = true;
            }
        }
        if (!foundProduct) {
            logger.log(Level.WARNING, "User has not purchased the product in the past.");
            throw new StockMarketException("User has not purchased the product in the past.");
        }
        // check if the shop still exists.
        if (shoppingBasket.getShopId() != shopID) {
            logger.log(Level.WARNING, "Shop does not exist.");
            throw new StockMarketException("Shop does not exist.");
        }
        // add the review.
        shoppingBasket.getShop().addReview(username, productID, review);
    }

    // this function returns the cart of the user by username.
    @Transactional
    public Object getCartByUsername(String username) {
        return _cartsRepository.getCartByUsername(username);
    }

    /*
     * view the shopping cart of a user.
     */
    @Transactional
    public List<BasketDto> viewShoppingCart(String token, String username) throws StockMarketException {
        ShoppingCart cart;

        if (username == null) {
            cart = _guestsCarts.get(token);
        } else {
            cart = _cartsRepository.getCartByUsername(username);
        }
        List<BasketDto> baskets = new ArrayList<>();
        for (ShoppingBasket basket : cart.getShoppingBaskets()) {
            baskets.add(new BasketDto(basket.getShopId(), basket.getProductIdList(), basket.calculateShoppingBasketPrice()));
        }
        return baskets;
    }

    // for tests
    public void addCartForGuestForTests(String guestID, ShoppingCart cart) {
        _guestsCarts.put(guestID, cart);
    }

    // function to initilaize data for UI testing
    // public void initUI() throws StockMarketException {
    //     ShoppingCart cartUI = new ShoppingCart();
    //     _cartsRepository.addCartForUser("tal", cartUI);
    //     addProductToUserCart("tal", 0, 0, 1);
    //     addProductToUserCart("tal", 0, 0, 1);
    //     addProductToUserCart("tal", 1, 1, 1);
    //     addProductToUserCart("tal", 2, 1, 1);    
    // }
}
