package Domain;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

import Domain.ExternalServices.PaymentService.AdapterPayment;
import Domain.ExternalServices.SupplyService.AdapterSupply;
import Domain.Facades.ShopFacade;

import java.util.Optional;
import Exceptions.PaymentFailedException;
import Exceptions.ProdcutPolicyException;
import Exceptions.ProductOutOfStockExepction;
import Exceptions.ShippingFailedException;
import Exceptions.StockMarketException;
import Exceptions.ShopPolicyException;

//TODO: TAL: add pay and ship methods to this class.
//TODO: function to add prodcut to cart?

// This class represents a shopping cart that contains a list of shopping baskets.
// The shopping cart connected to one user at any time.
public class ShoppingCart {
    private List<ShoppingBasket> _shoppingBaskets;
    private AdapterPayment _paymentMethod;
    private AdapterSupply _supplyMethod;
    private ShopFacade _shopFacade;
    private User _user;
    private static final Logger logger = Logger.getLogger(ShoppingCart.class.getName());

    //TODO: Add user to constructor
    public ShoppingCart() {
        _shoppingBaskets = new ArrayList<>();
        _paymentMethod = AdapterPayment.getAdapterPayment();;
        _supplyMethod = AdapterSupply.getAdapterPayment();
        _shopFacade = ShopFacade.getShopFacade();
    }

    /*
     * This method is responsible for purchasing the cart.
     * It first calls the purchaseCart method of the shopping cart which reaponsible
     * for changing the item's stock.
     * Then it tries to pay and deliver the items.
     * If the payment or the delivery fails, it cancels the purchase and restock the
     * item.
     */
    public void purchaseCart(List<Integer> busketsToBuy, String cardNumber, String address)
            throws PaymentFailedException, ShippingFailedException, StockMarketException {
        purchaseCartEditStock(busketsToBuy);
        try {
            for (ShoppingBasket shoppingBasket : _shoppingBaskets) {
                double amountToPay = shoppingBasket.calculateShoppingBasketPrice();
                _paymentMethod.checkIfPaymentOk(cardNumber, shoppingBasket.getShopBankDetails(), amountToPay);
                _supplyMethod.checkIfDeliverOk(address, shoppingBasket.getShopAddress());
            }
            for (ShoppingBasket shoppingBasket : _shoppingBaskets) {
                double amountToPay = shoppingBasket.calculateShoppingBasketPrice();
                _paymentMethod.pay(cardNumber, shoppingBasket.getShopBankDetails(), amountToPay);
                _supplyMethod.deliver(address, shoppingBasket.getShopAddress());
            }
        } catch (PaymentFailedException e) {
            logger.log(Level.SEVERE, "Payment has been failed with exception: " + e.getMessage(), e);
            cancelPurchaseEditStock(busketsToBuy);
            throw new PaymentFailedException("Payment failed");
        } catch (ShippingFailedException e) {
            logger.log(Level.SEVERE, "Shipping has been failed with exception: " + e.getMessage(), e);
            cancelPurchaseEditStock(busketsToBuy);
            _paymentMethod.refound(cardNumber);
            throw new ShippingFailedException("Shipping failed");
        }

    }

    /*
     * Go thorugh the list of baskets to buy and purchase them.
     * If an exception is thrown, cancel the purchase of all the baskets that were
     * bought.
     * This function only updates the item's stock.
     */
    private void purchaseCartEditStock(List<Integer> busketsToBuy) {
        logger.log(Level.FINE, "ShoppingCart - purchaseCart - Start purchasing cart.");
        List<Integer> boughtBasketList = new ArrayList<>();

        for (Integer basketId : busketsToBuy) {
            try {
                if (!_shoppingBaskets.get(basketId).purchaseBasket())
                    throw new ProductOutOfStockExepction("One of the products in the basket is out of stock");
                boughtBasketList.add(basketId);
            } catch (ProductOutOfStockExepction e) {
                logger.log(Level.SEVERE, "ShoppingCart - purchaseCart - Product out of stock for basket number: "
                        + basketId + ". Exception: " + e.getMessage(), e);
                logger.log(Level.FINE, "ShoppingCart - purchaseCart - Canceling purchase of all baskets.");
                for (Integer basket : boughtBasketList) {
                    _shoppingBaskets.get(basket).cancelPurchase();
                }
            } catch(ShopPolicyException e){
                logger.log(Level.SEVERE, "ShoppingCart - purchaseCart - Basket "+basketId+" Validated the policy of the shop.");
                logger.log(Level.FINE, "ShoppingCart - purchaseCart - Canceling purchase of all baskets.");
                for (Integer basket : boughtBasketList) {
                    _shoppingBaskets.get(basket).cancelPurchase();
                }
            }

        }
    }

    /*
     * Go through the list of baskets to cancel and cancel the purchase of them.
     * This function only updates the item's stock.
     */
    private void cancelPurchaseEditStock(List<Integer> busketsToBuy) {
        logger.log(Level.FINE, "ShoppingCart - cancelPurchase - Canceling purchase of all baskets.");
        for (Integer basketId : busketsToBuy) {
            _shoppingBaskets.get(basketId).cancelPurchase();
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
     * @param productID the product to add.
     * @param shopID the shop the product is from.
     * @param user the user that wants to add the prodcut.
     * @throws ProdcutPolicyException
     */
    public void addProduct(int productID, int shopID) throws ProdcutPolicyException {
        Optional<ShoppingBasket> basketOptional = _shoppingBaskets.stream()
                .filter(basket -> basket.getShop().getShopId() == shopID).findFirst();

        ShoppingBasket basket;
        if (basketOptional.isPresent()) {
            basket = basketOptional.get();
        } else {
            basket = new ShoppingBasket(_shopFacade.getShopByShopId(shopID));
            _shoppingBaskets.add(basket);
        }

        basket.addProductToShoppingBasket(_user, productID);
        logger.log(Level.INFO, "Product added to shopping basket: " + productID + " in shop: " + shopID);
    }

    public void removeProduct(int productID, int shopID) {
        Optional<ShoppingBasket> basketOptional = _shoppingBaskets.stream()
                .filter(basket -> basket.getShop().getShopId() == shopID).findFirst();

        if (basketOptional.isPresent()) {
            ShoppingBasket basket = basketOptional.get();
            basket.removeProductFromShoppingBasket(productID);
            logger.log(Level.INFO, "Product removed from shopping basket: " + productID + " in shop: " + shopID);
            if (basket.isEmpty()) {
                _shoppingBaskets.remove(basket);
                logger.log(Level.INFO, "Shopping basket for shop: " + shopID + " is empty and has been removed.");
            }
        } else {
            logger.log(Level.WARNING, "No shopping basket found for shop: " + shopID);
        }
    }

}
