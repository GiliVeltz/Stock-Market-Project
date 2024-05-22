package Domain;

import java.util.ArrayList;
import java.util.List;

import Domain.Exceptions.ProductOutOfStockExepction;


public class ShoppingBasket {
    private Integer _shopId;
    private List<Product> _productList;
    private double _basketTotalAmount;

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
    public void purchaseBasket() {
        List<Product> boughtProductList = new ArrayList<>();
        for (Product product : _productList) {
            try{
                product.purchaseProduct();
                boughtProductList.add(product);
            }
            catch (ProductOutOfStockExepction e){
                for (Product boughtProduct : boughtProductList) {
                    boughtProduct.cancelPurchase();
                }
                throw new ProductOutOfStockExepction("One of the products in the basket is out of stock");
            }
        }
    }

    public void cancelPurchase() {
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
