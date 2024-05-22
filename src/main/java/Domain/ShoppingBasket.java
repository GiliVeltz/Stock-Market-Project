package Domain;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Domain.Exceptions.ProductOutOfStockExepction;

public class ShoppingBasket {
    private Integer _shopId;
    private List<Product> _productList;
    private double _basketTotalAmount;
    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    // Constructor
    public ShoppingBasket(Integer shopId){
        _shopId = shopId;
        _productList = new ArrayList<>();
        _basketTotalAmount = 0.0;
    }


    public void addProductToShoppingBasket(Product product){
        _productList.add(product);
    }
 
    // Calculate and return the total price of all products in the basket
    private double calculateShoppingBasketPrice() {
        _basketTotalAmount = 0.0; 
        for (Product product : _productList) {
            _basketTotalAmount += product.getPrice();
        }
        return _basketTotalAmount;
    }

    public double getShoppingBasketPrice() {
        return _basketTotalAmount;
    }

    /*
     * Go through the list of products in the basket and purchase them.
     * If an exception is thrown, cancel the purchase of all the products that were bought.
     * This function only updates the item's stock.
     */
    public boolean purchaseBasket(User buyer){
        logger.log(Level.FINE, "ShoppingBasket - purchaseBasket - user: " + buyer.getUserName() + ". Start purchasing basket from shodId: " + _shopId);
        List<Product> boughtProductList = new ArrayList<>();

        for (Product product : _productList) {
            try{
                product.purchaseProduct();
                boughtProductList.add(product);
            }
            catch (ProductOutOfStockExepction e){
                logger.log(Level.SEVERE, "ShoppingBasket - purchaseBasket - user: " + buyer.getUserName() 
                + " Product out of stock in basket from shopId: " + _shopId + ". Exception: " + e.getMessage(), e);
                logger.log(Level.FINE, "ShoppingBasket - purchaseBasket - user: " 
                + buyer.getUserName() + ". Canceling purchase of all products from basket from shopId: " + _shopId);
                for (Product boughtProduct : boughtProductList) {
                    boughtProduct.cancelPurchase();
                }
                return false;
            }
        }
        return true;
    }

    public void cancelPurchase(User buyer) {
        logger.log(Level.FINE, "ShoppingBasket - cancelPurchase - user: " + buyer.getUserName() 
        + ". Canceling purchase of all products from basket from shodId: " + _shopId);
        for (Product product : _productList) {
            product.cancelPurchase();
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
