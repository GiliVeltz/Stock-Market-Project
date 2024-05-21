package Domain;

import java.util.List;
import java.util.ArrayList;

import Domain.Exceptions.ItemOutOfStockExepction;

public class ShoppingCart {
    private List<ShoppingBasket> _shoppingBaskets;

    public ShoppingCart() {
        _shoppingBaskets = new ArrayList<>();
    }

    /*
     * Go thorugh the list of baskets to buy and purchase them.
     * If an exception is thrown, cancel the purchase of all the baskets that were bought.
     * This function only updates the item's stock. 
     */
    public void purchaseCart(User buyer, List<Integer> busketsToBuy) {
        List<Integer> boughtBasketList = new ArrayList<>();

        for (Integer basketId : busketsToBuy) {
            try{
                _shoppingBaskets.get(basketId).purchaseBasket();
                boughtBasketList.add(basketId);
            }
            catch (ItemOutOfStockExepction e){
                for (Integer basket : boughtBasketList) {
                    _shoppingBaskets.get(basket).cancelPurchase();
                }
            }
            
        }
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        for (ShoppingBasket shoppingBasket : _shoppingBaskets) {
            output.append(shoppingBasket.toString()).append("\n");
        }
        return output.toString(); // Convert StringBuilder to String
    }
}
