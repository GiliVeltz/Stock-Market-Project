package Domain;

import java.util.HashMap;
import java.util.Map;

//class that represents an order for the shop

public class ShopOrder {
    private Integer _orderId;
    private ShoppingBasket _shoppingBasket;
    private double _totalOrderAmount;

    // Constructor
    public ShopOrder(Integer orderId , Integer shopId,ShoppingBasket shoppingBasket) {
        this._orderId = orderId;
        this._shoppingBasket = shoppingBasket.clone();
        this._totalOrderAmount = 0.0;
    }

    public Integer getOrderId() {
        return _orderId;
    }

    
    public double getOrderTotalAmount() { return _totalOrderAmount; }

  
    // public void calcTotalAmount() { 
    //     _totalOrderAmount = 0.0;
    //     for (Map.Entry<Integer, ShoppingBasket> entry : _shoppingBasketMap.entrySet()) {
    //         _totalOrderAmount += entry.getValue().getShoppingBasketPrice();
    //     }
    // }

    // // Helper method to print all products in the order
    // private String printAllProduct() 
    // {
    //     StringBuilder output = new StringBuilder();
    //     for (Map.Entry<Integer, ShoppingBasket> entry : _shoppingBasketMap.entrySet()) {
    //         output.append(entry.getValue().toString()).append("\n");
    //     }
    //     return output.toString(); // Convert StringBuilder to String
    // }

    @Override
    public String toString() {
        return "Order{" +
                "orderId = " + _orderId +
                ", totalAmount = " + _totalOrderAmount +
                // ", products = \n" + printAllProduct() +
                '}';
    }
}
