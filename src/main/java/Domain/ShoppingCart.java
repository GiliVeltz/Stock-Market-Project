package Domain;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

import Domain.Exceptions.ProductOutOfStockExepction;

public class ShoppingCart {
    private List<ShoppingBasket> _shoppingBaskets;
    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    public ShoppingCart() {
        _shoppingBaskets = new ArrayList<>();
    }

    /*
     * Go thorugh the list of baskets to buy and purchase them.
     * If an exception is thrown, cancel the purchase of all the baskets that were bought.
     * This function only updates the item's stock. 
     */
    public void purchaseCart(User buyer, List<Integer> busketsToBuy) {
        logger.log(Level.FINE, "ShoppingCart - purchaseCart - user: " + buyer.getUserName() + ". Start purchasing cart.");
        List<Integer> boughtBasketList = new ArrayList<>();

        for (Integer basketId : busketsToBuy) {
            try{
                if(!_shoppingBaskets.get(basketId).purchaseBasket(buyer))
                    throw new ProductOutOfStockExepction("One of the products in the basket is out of stock");
                boughtBasketList.add(basketId);
            }
            catch (ProductOutOfStockExepction e){
                logger.log(Level.SEVERE, "ShoppingCart - purchaseCart - user: " + buyer.getUserName() 
                + ". Product out of stock for baket number: " + basketId + ". Exception: " + e.getMessage(), e);
                logger.log(Level.FINE, "ShoppingCart - purchaseCart - user: " + buyer.getUserName() + ". Canceling purchase of all baskets.");
                for (Integer basket : boughtBasketList) {
                    _shoppingBaskets.get(basket).cancelPurchase(buyer);
                }
            }
            
        }
    }

    /*
     * Go through the list of baskets to cancel and cancel the purchase of them.
     * This function only updates the item's stock.
     */
    public void cancelPurchase(User buyer, List<Integer> busketsToBuy){
        logger.log(Level.FINE, "ShoppingCart - cancelPurchase - user: " + buyer.getUserName() + ". Canceling purchase of all baskets.");
        for (Integer basketId : busketsToBuy) {
            _shoppingBaskets.get(basketId).cancelPurchase(buyer); 
        }
    }

    //TODO: add pay and ship methods.
    
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (ShoppingBasket shoppingBasket : _shoppingBaskets) {
            output.append(shoppingBasket.toString()).append("\n");
        }
        return output.toString(); // Convert StringBuilder to String
    }
}
