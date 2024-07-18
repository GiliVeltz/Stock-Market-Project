package Domain.Facades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Domain.Entities.Guest;
import Domain.Entities.Order;
import Domain.Entities.Product;
import Domain.Entities.ShoppingBasket;
import Domain.Entities.ShoppingCart;
import Domain.Entities.User;
import Domain.ExternalServices.PaymentService.AdapterPaymentImp;
import Domain.ExternalServices.PaymentService.ProxyPayment;
import Domain.ExternalServices.SupplyService.AdapterSupplyImp;
import Domain.ExternalServices.SupplyService.ProxySupply;
import Domain.Repositories.DbGuestRepository;
import Domain.Repositories.DbOrderRepository;
import Domain.Repositories.DbShopOrderRepository;
import Domain.Repositories.DbShoppingBasketRepository;
import Domain.Repositories.DbShoppingCartRepository;
import Domain.Repositories.DbUserRepository;
import Domain.Repositories.InterfaceGuestRepository;
import Domain.Repositories.InterfaceOrderRepository;
import Domain.Repositories.InterfaceShopOrderRepository;
import Domain.Repositories.InterfaceShoppingBasketRepository;
import Domain.Repositories.InterfaceShoppingCartRepository;
import Domain.Repositories.InterfaceUserRepository;
import Dtos.BasketDto;
import Dtos.PurchaseCartDetailsDto;
import Exceptions.StockMarketException;
import jakarta.transaction.Transactional;

@Service
public class ShoppingCartFacade {
    private UserFacade userFacade;
    private ShopFacade shopFacade;
    Map<String, ShoppingCart> _guestsCarts; // <guestID, ShoppingCart>
    InterfaceShoppingCartRepository _cartsRepository;
    InterfaceOrderRepository _orderRepository;
    InterfaceGuestRepository _guestRepository;
    InterfaceUserRepository _userRepository;
    InterfaceShoppingBasketRepository _basketRepository;
    InterfaceShopOrderRepository _shopOrderRepository;
    private static final Logger logger = Logger.getLogger(ShoppingCartFacade.class.getName());

    @Autowired
    public ShoppingCartFacade(DbShoppingCartRepository cartsRepository, DbOrderRepository orderRepository,
            DbGuestRepository guestRepository,
            DbUserRepository userRepository, DbShoppingBasketRepository basketRepository,
            DbShopOrderRepository shopOrderRepository, UserFacade userFacade, ShopFacade shopFacade) {
        _cartsRepository = cartsRepository;
        _orderRepository = orderRepository;
        _guestRepository = guestRepository;
        _userRepository = userRepository;
        _basketRepository = basketRepository;
        _shopOrderRepository = shopOrderRepository;
        this.userFacade = userFacade;
        this.shopFacade = shopFacade;
        _guestsCarts = new HashMap<>();
    }

    // set repositories to be used in test system
    public void setShoppingCartFacadeRepositories(InterfaceShoppingCartRepository cartsRepository,
            InterfaceOrderRepository orderRepository, InterfaceGuestRepository guestRepository,
            InterfaceUserRepository userRepository, InterfaceShoppingBasketRepository basketRepository,
            InterfaceShopOrderRepository shopOrderRepository) {
        _cartsRepository = cartsRepository;
        _orderRepository = orderRepository;
        _shopOrderRepository = shopOrderRepository;
        _guestRepository = guestRepository;
        _userRepository = userRepository;
        _basketRepository = basketRepository;
    }

    // Add a cart for a guest by token.
    public void addCartForGuest(String guestID) throws StockMarketException {
        Guest g = userFacade.getGuestById(guestID);
        if (g == null) {
            throw new StockMarketException("Guest with id: " + guestID + " does not exist");
        }
        ShoppingCart cart = new ShoppingCart(g);
        cart.setOrderRepository(_orderRepository);
        g.setShoppingCart(cart);
        _cartsRepository.save(cart);
        _guestsCarts.put(guestID, cart);
        _guestRepository.flush();
        // _guestsCarts.put(guestID, cart);
    }

    public void setPaymentMocksForGuestCart(String guestId, ProxyPayment paymentService, ProxySupply supplyService) throws StockMarketException{
        ShoppingCart cart = _guestsCarts.get(guestId);
        if(cart == null) {
            throw new StockMarketException("Guest with id: " + guestId + " does not have a cart");
        }
        cart.setPaymentMocksForGuestCart(paymentService, supplyService);
        _cartsRepository.save(cart);
        _guestRepository.flush();
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
            ShoppingCart existCart = _cartsRepository.getCartByUsername(guestID);
            if (existCart == null) {
                ShoppingCart newCart = new ShoppingCart(user);
                newCart.setOrderRepository(_orderRepository);
                _cartsRepository.save(newCart);
                _userRepository.flush();
            } else {
                existCart.SetUser(user);
                _cartsRepository.save(existCart);
            }
            // _cartsRepository.addCartForUser(user.getUserName(),
            // _guestsCarts.get(guestID));

        } else {
            // add the user to the cart
            cart.SetUser(user);
        }
        // System.out.println("test"+_cartsRepository.getCartByUsername(user.getUserName()));
    }

    /*
     * Add a product to a user cart by username.
     * This method called when a user add a product to his cart.
     */
    @Transactional
    public void addProductToUserCart(String userName, int productID, int shopID, int quantity)
            throws StockMarketException {
        ShoppingCart cart = getCartByUsernameOrToken(userName);
        if (cart != null) {
            try {
                cart.addProduct(productID, shopID, quantity);
            } catch (StockMarketException e) {
                throw e;
            }
            _cartsRepository.flush();
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
    public void addProductToGuestCart(String guestID, int productID, int shopID, int quantity)
            throws StockMarketException {
        ShoppingCart cart = getCartByUsernameOrToken(guestID);
        //ShoppingCart cart = _guestsCarts.get(guestID);
        if (cart != null) {
            try {
                cart.addProduct(productID, shopID, quantity);
            } catch (StockMarketException e) {
                throw e;
            }
            _cartsRepository.flush();
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
    public void removeProductFromUserCart(String userName, int productID, int shopID, int quantity) throws StockMarketException {
        ShoppingCart cart = getCartByUsernameOrToken(userName);
        if (cart != null) {
            Product product = shopFacade.getProductById(productID);
            cart.removeProduct(product, shopID, quantity);
            _cartsRepository.flush();
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
    public void removeProductFromGuestCart(String guestID, int productID, int shopID, int quantity) throws StockMarketException {
        ShoppingCart cart = getCartByUsernameOrToken(guestID);
        if (cart != null) {
            Product product = shopFacade.getProductById(productID);
            cart.removeProduct(product, shopID, quantity);
            _cartsRepository.flush();
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
    public void purchaseCartGuest(String guestID, PurchaseCartDetailsDto purchaseCartDetails)
            throws StockMarketException {
        logger.log(Level.INFO, "Start purchasing cart for guest.");
        try {
            // getGuestCart(guestID).purchaseCart(purchaseCartDetails);
            // getGuestCart(guestID).emptyCart();
            getCartByUsernameOrToken(guestID).purchaseCart(purchaseCartDetails);
            getCartByUsernameOrToken(guestID).emptyCart();
            _cartsRepository.flush();
        } catch (StockMarketException e) {
            logger.log(Level.WARNING, "Failed to purchase cart for guest: " + guestID);
            throw e;
        }
    }

    /*
     * Purchase the cart of a user.
     */
    @Transactional
    public void purchaseCartUser(String username, PurchaseCartDetailsDto purchaseCartDetails)
            throws StockMarketException {
        logger.log(Level.INFO, "Start purchasing cart for user.");
        try {
            getCartByUsernameOrToken(username).purchaseCart(purchaseCartDetails);
            getCartByUsernameOrToken(username).emptyCart();
            _cartsRepository.flush();
        } catch (StockMarketException e) {
            logger.log(Level.WARNING, "Failed to purchase cart for user: " + username);
            throw e;
        }
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

    // this function checks for the product in the past purchases of the user, and
    // if it exists, it returns the shopID.
    // next, this function will add a review on the product in the shop (if he still
    // exists).
    @SuppressWarnings({ "null" })
    @Transactional
    public void writeReview(String username, List<Order> purchaseHistory, int productID, int shopID, String review)
            throws StockMarketException {
        // check if the user has purchased the product in the past using
        // purchaseHistory.
        boolean foundProduct = false;
        ShoppingBasket shoppingBasket = null;
        for (Order order : purchaseHistory) {
            Map<Integer, ShoppingBasket> productsByShoppingBasket = order.getProductsByShoppingBasket();
            if (productsByShoppingBasket.containsKey(productID)) {
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
    public ShoppingCart getCartByUsernameOrToken(String username) {
        ShoppingCart returnedCart = _cartsRepository.getCartByUsername(username);
        returnedCart.setOrderRepository(_orderRepository);
        returnedCart.setShopOrderRepository(_shopOrderRepository);
        returnedCart.setShoppingBasketsRepository(_basketRepository);
        returnedCart.setShopFacade(shopFacade);
        returnedCart.setPaymentMethod(AdapterPaymentImp.getRealAdapterPayment());
        returnedCart.setSupplyMethod(AdapterSupplyImp.getAdapterSupply());
        for (ShoppingBasket basket : returnedCart.getShoppingBaskets()) {
            basket.setShop(shopFacade.getShopByShopId(basket.getShopId()));
        }
        return returnedCart;
    }

    /*
     * view the shopping cart of a user.
     */
    @Transactional
    public List<BasketDto> viewShoppingCart(String token, String username) throws StockMarketException {
        ShoppingCart cart;

        if (username == null) {
            //cart = _guestsCarts.get(token);
            cart = getCartByUsernameOrToken(token);
        } else {
            cart = getCartByUsernameOrToken(username);
        }
        List<BasketDto> baskets = new ArrayList<>();
        for (ShoppingBasket basket : cart.getShoppingBaskets()) {
            baskets.add(new BasketDto(basket.getShopId(), basket.getProductIdsList(),
                    basket.calculateShoppingBasketPrice()));
        }
        return baskets;
    }

    // for tests
    public void addCartForGuestForTests(String guestID, ShoppingCart cart) {
        _guestsCarts.put(guestID, cart);
        cart.setOrderRepository(_orderRepository);
    }

    // for tests
    public InterfaceShoppingCartRepository getCartsRepository() {
        return _cartsRepository;
    }

    // function to initilaize data for UI testing
    // public void initUI() throws StockMarketException {
    // ShoppingCart cartUI = new ShoppingCart();
    // _cartsRepository.addCartForUser("tal", cartUI);
    // addProductToUserCart("tal", 0, 0, 1);
    // addProductToUserCart("tal", 0, 0, 1);
    // addProductToUserCart("tal", 1, 1, 1);
    // addProductToUserCart("tal", 2, 1, 1);
    // }
}
