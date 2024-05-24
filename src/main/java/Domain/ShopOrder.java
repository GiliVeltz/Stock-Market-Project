package Domain;

import java.util.HashMap;
import java.util.Map;

public class ShopOrder {
     private Integer _orderId;
    private Map<Integer ,ShoppingBasket> _shoppingBasketMap; // <UserId, ShoppingBasketPerShop>
    private double _totalOrderAmount;

    // Constructor
    public ShopOrder(Integer orderId) {
        this._orderId = orderId;
        this._shoppingBasketMap = new HashMap<>();
        this._totalOrderAmount = 0.0;
    }

    public Integer getOrderId() {
        return _orderId;
    }

    public Map<Integer ,ShoppingBasket> getProductsByShoppingBasket() {
        return _shoppingBasketMap;
    }

    public double getOrderTotalAmount() { return _totalOrderAmount; }

  
    public void calcTotalAmount() { 
        _totalOrderAmount = 0.0;
        for (Map.Entry<Integer, ShoppingBasket> entry : _shoppingBasketMap.entrySet()) {
            _totalOrderAmount += entry.getValue().getShoppingBasketPrice();
        }
    }

    // Helper method to print all products in the order
    private String printAllProduct() 
    {
        StringBuilder output = new StringBuilder();
        for (Map.Entry<Integer, ShoppingBasket> entry : _shoppingBasketMap.entrySet()) {
            output.append(entry.getValue().toString()).append("\n");
        }
        return output.toString(); // Convert StringBuilder to String
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId = " + _orderId +
                ", totalAmount = " + _totalOrderAmount +
                ", products = \n" + printAllProduct() +
                '}';
    }
}
