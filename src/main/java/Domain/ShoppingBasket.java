package Domain;

import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Domain.Exceptions.ProductOutOfStockExepction;

// This class represents a shopping basket that contains a list of products.
// The shopping basket can belongs to one and only shop and one user.
public class ShoppingBasket {
    private Shop _shop;
    private List<Integer> _productIdList;
    private double _basketTotalAmount;
    private static final Logger logger = Logger.getLogger(ShoppingBasket.class.getName());

    public Map<Integer, SortedMap<Double, Integer>> productToPriceToAmount;

    // Constructor
    public ShoppingBasket(Shop shop) {
        _shop = shop;
        _productIdList = new ArrayList<>();
        _basketTotalAmount = 0.0;
    }

    public void addProductToShoppingBasket(Integer productId) {
        _productIdList.add(productId);
    }
 
    // Calculate and return the total price of all products in the basket
    public double calculateShoppingBasketPrice() {
        resetProductToPriceToAmount();
        _shop.applyDiscounts(this);
        _basketTotalAmount = 0.0;
        for (Integer productId : productToPriceToAmount.keySet()) {
            for (Double price : productToPriceToAmount.get(productId).keySet()) {
                _basketTotalAmount += productToPriceToAmount.get(productId).get(price) * price;
            }
        }
        return _basketTotalAmount;
    }

    public double getShoppingBasketPrice() {
        return _basketTotalAmount;
    }

    /*
     * Go through the list of products in the basket and purchase them.
     * If an exception is thrown, cancel the purchase of all the products that were
     * bought. This function only updates the item's stock.
     */
    public boolean purchaseBasket() {
        logger.log(Level.FINE, "ShoppingBasket - purchaseBasket - Start purchasing basket from shodId: " + _shop.getShopId());
        List<Integer> boughtProductIdList = new ArrayList<>();

        // TODO: consider the discounts using productToPriceToAmount

        for (Integer productId : _productIdList) {
            try {
                _shop.getProductById(productId).purchaseProduct();
                boughtProductIdList.add(productId);
            } catch (ProductOutOfStockExepction e) {
                logger.log(Level.SEVERE,
                        "ShoppingBasket - purchaseBasket - Product out of stock in basket from shopId: "
                                + _shop.getShopId() + ". Exception: " + e.getMessage(),
                        e);
                logger.log(Level.FINE,
                        "ShoppingBasket - purchaseBasket - Canceling purchase of all products from basket from shopId: "
                                + _shop.getShopId());
                for (Integer boughtProductId : boughtProductIdList) {
                    _shop.getProductById(boughtProductId).cancelPurchase();
                }
                return false;
            }
        }
        return true;
    }

    public void cancelPurchase() {
        logger.log(Level.FINE,
                "ShoppingBasket - cancelPurchase - Canceling purchase of all products from basket from shodId: "
                        + _shop.getShopId());
        for (Integer productId : _productIdList) {
            _shop.getProductById(productId).cancelPurchase();
        }
    }

    public int getProductCount(Integer productId) {
        int count = 0;

        for (Integer product : _productIdList)
            if (product == productId)
                count++;

        return count;
    }

    /**
     * Resets the product to price to amount mapping in the shopping basket.
     * This method iterates through the product list and updates the mapping
     * based on the product ID, price, and quantity.
     */
    public void resetProductToPriceToAmount() {
        productToPriceToAmount = new HashMap<>();

        for (Integer productId : _productIdList) {
            double price = _shop.getProductById(productId).getPrice();
            if (!productToPriceToAmount.containsKey(productId))
                productToPriceToAmount.put(productId, new TreeMap<>((a, b) -> a > b ? 1 : -1));
            if (!productToPriceToAmount.get(productId).containsKey(price))
                productToPriceToAmount.get(productId).put(price, 0);

            int oldAmount = productToPriceToAmount.get(productId).get(price);
            productToPriceToAmount.get(productId).put(price, oldAmount + 1);
        }
    }

    public String getShopBankDetails() {
        return _shop.getBankDetails();
    }

    public String getShopAddress() {
        return _shop.getShopAddress();
    }

    @Override
    public String toString() {
        return "ShoppingBasket{" +
                "ShopId=" + _shop.getShopId() +
                ", products=" + _productIdList +
                '}';
    }
}
