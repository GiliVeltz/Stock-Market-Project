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
public class ShoppingBasket implements Cloneable {
    private Integer _shopId;
    private List<Product> _productList;
    private double _basketTotalAmount;
    private static final Logger logger = Logger.getLogger(ShoppingBasket.class.getName());

    public Map<Integer, SortedMap<Double, Integer>> productToPriceToAmount;

    // Constructor
    public ShoppingBasket(Integer shopId) {
        _shopId = shopId;
        _productList = new ArrayList<>();
        _basketTotalAmount = 0.0;
    }

    public void addProductToShoppingBasket(Product product) {
        _productList.add(product);
    }


    // Calculate and return the total price of all products in the basket
    private double calculateShoppingBasketPrice() {
        _basketTotalAmount = 0.0;
        //case where there are no discounts on the basket
        if(productToPriceToAmount.size() == 0) {
        for (Product product : _productList) {
            _basketTotalAmount += product.getPrice();
        }
    }
        else{
            //iterate over the productt to price to amount map and calculate the total price
            for (Map.Entry<Integer, SortedMap<Double, Integer>> entry : productToPriceToAmount.entrySet()) {
                for (Map.Entry<Double, Integer> priceToAmount : entry.getValue().entrySet()) {
                    _basketTotalAmount += priceToAmount.getKey() * priceToAmount.getValue();
                }
            }
        }
        return _basketTotalAmount;
    }

    public double getShoppingBasketPrice() {
        if (_basketTotalAmount == 0.0)
            return calculateShoppingBasketPrice();
        return _basketTotalAmount;
    }

    public List<Product> getProductList() {
        return _productList;
    }

    /*
     * Go through the list of products in the basket and purchase them.
     * If an exception is thrown, cancel the purchase of all the products that were
     * bought. This function only updates the item's stock.
     */
    public boolean purchaseBasket() {
        logger.log(Level.FINE, "ShoppingBasket - purchaseBasket - Start purchasing basket from shodId: " + _shopId);
        List<Product> boughtProductList = new ArrayList<>();

        // TODO: consider the discounts using productToPriceToAmount

        for (Product product : _productList) {
            try {
                product.purchaseProduct();
                boughtProductList.add(product);
            } catch (ProductOutOfStockExepction e) {
                logger.log(Level.SEVERE,
                        "ShoppingBasket - purchaseBasket - Product out of stock in basket from shopId: "
                                + _shopId + ". Exception: " + e.getMessage(),
                        e);
                logger.log(Level.FINE,
                        "ShoppingBasket - purchaseBasket - Canceling purchase of all products from basket from shopId: "
                                + _shopId);
                for (Product boughtProduct : boughtProductList) {
                    boughtProduct.cancelPurchase();
                }
                return false;
            }
        }
        return true;
    }

    public void cancelPurchase() {
        logger.log(Level.FINE,
                "ShoppingBasket - cancelPurchase - Canceling purchase of all products from basket from shodId: "
                        + _shopId);
        for (Product product : _productList) {
            product.cancelPurchase();
        }
    }

    public int getProductCount(Integer productId) {
        int count = 0;

        for (Product product : _productList)
            if (product.getProductId() == productId)
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

        for (Product product : _productList) {
            int pid = product.getProductId();
            double price = product.getPrice();
            if (!productToPriceToAmount.containsKey(pid))
                productToPriceToAmount.put(pid, new TreeMap<>((a, b) -> a > b ? 1 : -1));
            if (!productToPriceToAmount.get(pid).containsKey(price))
                productToPriceToAmount.get(pid).put(price, 0);

            int oldAmount = productToPriceToAmount.get(pid).get(price);
            productToPriceToAmount.get(pid).put(price, oldAmount + 1);
        }
    }

    @Override
    public ShoppingBasket clone() {
        try {
            ShoppingBasket cloned = (ShoppingBasket) super.clone();
            cloned._productList = new ArrayList<>();
            for (Product product : _productList) {
                cloned._productList.add(product.clone());
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Can't happen as we implement Cloneable
        }
    }

    @Override
    public String toString() {
        return "ShoppingBasket{" +
                "ShopId=" + _shopId +
                ", products=" + _productList +
                '}';
    }
}
