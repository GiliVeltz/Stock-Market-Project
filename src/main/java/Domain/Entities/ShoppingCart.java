package Domain.Entities;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.HashMap;

import Domain.ExternalServices.PaymentService.AdapterPaymentImp;
import Domain.ExternalServices.SupplyService.AdapterSupplyImp;
import Domain.Facades.ShopFacade;
import Dtos.PurchaseCartDetailsDto;

import java.util.Optional;
import Exceptions.PaymentFailedException;
import Exceptions.ProdcutPolicyException;
import Exceptions.ProductDoesNotExistsException;
import Exceptions.ProductOutOfStockExepction;
import Exceptions.ShippingFailedException;
import Exceptions.StockMarketException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import Exceptions.ShopPolicyException;

//TODO: TAL: add pay and ship methods to this class.

// This class represents a shopping cart that contains a list of shopping baskets.
// The shopping cart connected to one user at any time.
@Entity
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long _shoppingCartId;
    // @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL)
    @Transient
    private List<ShoppingBasket> _shoppingBaskets;
    @Transient
    private AdapterPaymentImp _paymentMethod;
    @Transient
    private AdapterSupplyImp _supplyMethod;
    @Transient
    private ShopFacade _shopFacade;
    // @OneToOne(mappedBy = "shoppingCart", cascade = CascadeType.ALL)
    @Transient
    private User _user; // if the user is null, the cart is for a guest.
    private static final Logger logger = Logger.getLogger(ShoppingCart.class.getName());

    public ShoppingCart() {
        _shoppingBaskets = new ArrayList<>();
        _paymentMethod = AdapterPaymentImp.getAdapterPayment();
        _supplyMethod = AdapterSupplyImp.getAdapterSupply();
        _shopFacade = ShopFacade.getShopFacade();
        _user = null;
    }

    // for tests
    public ShoppingCart(ShopFacade shopFacade, AdapterPaymentImp paymentMethod, AdapterSupplyImp supplyMethod) {
        _shoppingBaskets = new ArrayList<>();
        _paymentMethod = paymentMethod;
        _supplyMethod = supplyMethod;
        _shopFacade = shopFacade;
        _user = null;
    }
    
    public ShoppingCart(ShopFacade shopFacade) {
        _shoppingBaskets = new ArrayList<>();
        _paymentMethod = AdapterPaymentImp.getAdapterPayment();
        _supplyMethod = AdapterSupplyImp.getAdapterSupply();
        _shopFacade = shopFacade;
        _user = null;
    }

    public void emptyCart() {
        _shoppingBaskets.clear();
    }

    /*
     * This method is responsible for purchasing the cart.
     * It first calls the purchaseCart method of the shopping cart which reaponsible
     * for changing the item's stock.
     * Then it tries to pay and deliver the items.
     * If the payment or the delivery fails, it cancels the purchase and restock the
     * item.
     */
    public void purchaseCart(PurchaseCartDetailsDto purchaseCartDetailsDto, int ordersId)
            throws PaymentFailedException, ShippingFailedException, StockMarketException {
        try {
            purchaseCartEditStock(purchaseCartDetailsDto.getBasketsToBuy());
        } catch (StockMarketException e) {
            logger.log(Level.SEVERE, "StockMarketException has been thrown: " + e.getMessage(), e);
            throw e;
        }
        
        Map<Double, String> priceToShopDetails = new HashMap<>();
        double overallPrice = 0;

        for (Integer basketNum : purchaseCartDetailsDto.getBasketsToBuy()) {
            ShoppingBasket shoppingBasket = _shoppingBaskets.get(basketNum);
            double amountToPay = shoppingBasket.calculateShoppingBasketPrice();
            overallPrice += amountToPay;
            priceToShopDetails.put(amountToPay, shoppingBasket.getShopBankDetails());
        }

        int paymentTransactionId = -1;
        int supplyTransactionId = -1;

        try {
            if (!_paymentMethod.handshake())
                throw new PaymentFailedException("Payment service is not available");

            if (!_supplyMethod.handshake())
                throw new ShippingFailedException("Shipping service is not available");

            paymentTransactionId = _paymentMethod.payment(purchaseCartDetailsDto.getPaymentInfo(), overallPrice);
            if (paymentTransactionId == -1)
                throw new PaymentFailedException("Payment failed");

            supplyTransactionId = _supplyMethod.supply(purchaseCartDetailsDto.getSupplyInfo());
            if (supplyTransactionId == -1)
                throw new ShippingFailedException("Shipping failed");
                
            List<ShoppingBasket> shoppingBasketsForOrder = new ArrayList<>();
            for (Integer basketNum : purchaseCartDetailsDto.getBasketsToBuy()) {
                ShoppingBasket shoppingBasket = _shoppingBaskets.get(basketNum);
                shoppingBasketsForOrder.add(shoppingBasket);
                //_supplyMethod.deliver(details.address, shoppingBasket.getShopAddress());
            }

            if (_user != null) {
                Order order = new Order(ordersId, shoppingBasketsForOrder, paymentTransactionId, supplyTransactionId);
                _user.addOrder(order);
            }

            for (ShoppingBasket shoppingBasket : shoppingBasketsForOrder) {
                ShopOrder shopOrder = new ShopOrder(ordersId, shoppingBasket.getShop().getShopId(), shoppingBasket);
                shoppingBasket.getShop().addOrderToOrderHistory(shopOrder);
            }

        } catch (PaymentFailedException e) {
            logger.log(Level.SEVERE, "Payment has been failed with exception: " + e.getMessage(), e);
            cancelPurchaseEditStock(purchaseCartDetailsDto.getBasketsToBuy());
            throw new PaymentFailedException("Payment failed");
        } catch (ShippingFailedException e) {
            logger.log(Level.SEVERE, "Shipping has been failed with exception: " + e.getMessage(), e);
            cancelPurchaseEditStock(purchaseCartDetailsDto.getBasketsToBuy());
            _paymentMethod.cancel_pay(paymentTransactionId);
            throw new ShippingFailedException("Shipping failed");
        }
    }
    
    public String getUsernameString() {
        return _user == null ? "Guest" : _user.getUserName();
    }

    /*
     * Go thorugh the list of baskets to buy and purchase them.
     * If an exception is thrown, cancel the purchase of all the baskets that were
     * bought.
     * This function only updates the item's stock.
     */
    public void purchaseCartEditStock(List<Integer> busketsToBuy) throws StockMarketException {
        logger.log(Level.FINE, "ShoppingCart - purchaseCart - Start purchasing cart.");
        List<Integer> boughtBasketList = new ArrayList<>();

        for (Integer basketId : busketsToBuy) {
            try {
                if (!_shoppingBaskets.get(basketId).purchaseBasket(getUsernameString()))
                    throw new ProductOutOfStockExepction("One of the products in the basket is out of stock");
                boughtBasketList.add(basketId);
            } catch (ProductOutOfStockExepction e) {
                logger.log(Level.SEVERE, "ShoppingCart - purchaseCart - Product out of stock for basket number: "
                        + basketId + ". Exception: " + e.getMessage(), e);
                logger.log(Level.FINE, "ShoppingCart - purchaseCart - Canceling purchase of all baskets.");
                for (Integer basket : boughtBasketList) {
                    _shoppingBaskets.get(basket).cancelPurchase();
                }
                throw e;
            } catch (ShopPolicyException e) {
                logger.log(Level.SEVERE,
                        "ShoppingCart - purchaseCart - Basket " + basketId + " Validated the policy of the shop.");
                logger.log(Level.FINE, "ShoppingCart - purchaseCart - Canceling purchase of all baskets.");
                for (Integer basket : boughtBasketList) {
                    _shoppingBaskets.get(basket).cancelPurchase();
                }
                throw e;
            }
        }
    }

    /*
     * Go through the list of baskets to cancel and cancel the purchase of them.
     * This function only updates the item's stock.
     */
    public void cancelPurchaseEditStock(List<Integer> busketsToBuy) throws StockMarketException {
        logger.log(Level.FINE, "ShoppingCart - cancelPurchase - Canceling purchase of all baskets.");
        for (Integer basketId : busketsToBuy) {
            getShoppingBasket(basketId).cancelPurchase();
        }
    }

    public int getCartSize() {
        return _shoppingBaskets.size();
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        for (ShoppingBasket shoppingBasket : _shoppingBaskets) {
            output.append(shoppingBasket.toString()).append("\n");
        }
        return output.toString(); // Convert StringBuilder to String
    }

    /**
     * Add a product to the shopping cart of a user.
     * 
     * @param productID the product to add.
     * @param shopID    the shop the product is from.
     * @param user      the user that wants to add the prodcut.
     * @throws ProdcutPolicyException
     * @throws ProductDoesNotExistsException
     */
    public void addProduct(int productID, int shopID, int quantity) throws StockMarketException {
        // Check if the product exists in the shop.
        if (_shopFacade.getShopByShopId(shopID).getProductById(productID) == null) {
            logger.log(Level.SEVERE, "Product does not exists in shop: " + shopID);
            throw new ProductDoesNotExistsException("Product does not exists in shop: " + shopID);
        }

        // basketOptional is the basket of the user for the shop.
        Optional<ShoppingBasket> basketOptional = _shoppingBaskets.stream()
                .filter(basket -> basket.getShop().getShopId() == shopID).findFirst();

        // create a new basket if the user does not have a basket for this shop.
        ShoppingBasket basket;
        if (basketOptional.isPresent()) {
            basket = basketOptional.get();
        } else {
            basket = new ShoppingBasket(_shopFacade.getShopByShopId(shopID));
        }

        // add the product to the basket.
        try {
            basket.addProductToShoppingBasket(_user, productID, quantity);
            if (!basketOptional.isPresent())
                _shoppingBaskets.add(basket);
        }
        catch (ProductOutOfStockExepction e) {
            logger.log(Level.SEVERE, "Product out of stock in shop: " + shopID);
            throw e;
        }
        catch (ShopPolicyException e) {
            logger.log(Level.SEVERE, "Shop policy exception in shop: " + shopID);
            throw e;
        }
        
        logger.log(Level.INFO, "Product added to shopping basket: " + productID + " in shop: " + shopID);
    }

    // Remove a product from the shopping cart of a user.
    public void removeProduct(int productID, int shopID, int quantity) throws StockMarketException {
        Optional<ShoppingBasket> basketOptional = _shoppingBaskets.stream()
                .filter(basket -> basket.getShop().getShopId() == shopID).findFirst();

        if (basketOptional.isPresent()) {
            ShoppingBasket basket = basketOptional.get();
            basket.removeProductFromShoppingBasket(productID, quantity);
            logger.log(Level.INFO, "Product removed from shopping basket: " + productID + " in shop: " + shopID);
            if (basket.isEmpty()) {
                _shoppingBaskets.remove(basket);
                logger.log(Level.INFO, "Shopping basket for shop: " + shopID + " is empty and has been removed.");
            }
        } else {
            logger.log(Level.WARNING, "No shopping basket found for shop: " + shopID);
            throw new StockMarketException(
                    "Trying to remove product from shopping cart, but no shopping basket found for shop: " + shopID);
        }
    }

    // Set the user of the cart.
    public void SetUser(User user) {
        _user = user;
    }

    // Get shopping baskets of the cart.
    public List<ShoppingBasket> getShoppingBaskets() {
        return _shoppingBaskets;
    }

    // Get a shopping basket by index.
    public ShoppingBasket getShoppingBasket(int i) {
        return getShoppingBaskets().get(i);
    }

    // for tests
    public void addShoppingBasket(ShoppingBasket basket) {
        _shoppingBaskets.add(basket);
    }

    // return all the products in the cart
    public Map<Integer, Product> getProducts() throws StockMarketException {
        Map<Integer, Product> products = new HashMap<Integer, Product>();
        for (ShoppingBasket basket : _shoppingBaskets) {
            for (Product product : basket.getProductsList()) {
                products.put(product.getProductId(), product);
            }
        }
        return products;
    }

    // return all the purchases in the cart
    public Map<String, ShoppingBasket> getPurchases() {
        Map<String, ShoppingBasket> purchases = new HashMap<String, ShoppingBasket>();
        for (ShoppingBasket basket : _shoppingBaskets) {
            purchases.put(basket.getShop().getShopName(), basket);
        }
        return purchases;
    }

    // return true if the cart has a basket with the given shopID
    public boolean containsKey(int shopID) {
        for (ShoppingBasket basket : _shoppingBaskets) {
            if (basket.getShop().getShopId() == shopID) {
                return true;
            }
        }
        return false;
    }
}
