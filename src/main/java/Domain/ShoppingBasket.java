package Domain;

import java.util.ArrayList;
import java.util.List;

public class ShoppingBasket {
    private Integer _shopId;
    private List<Product> _productList;
    private double _basketTotalAmount;

    // Constructor
    public ShoppingBasket(Integer shopId){
        this._shopId = shopId;
        this._productList = new ArrayList<>();
        this._basketTotalAmount = 0.0;
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

    @Override
    public String toString() {
        return "ShoppingBasket{" +
                "ShopId=" + _shopId +
                ", products=" + _productList +
                '}';
    }
}
