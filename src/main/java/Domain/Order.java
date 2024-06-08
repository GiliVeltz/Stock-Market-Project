package Domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Exceptions.StockMarketException;

import javax.annotation.Generated;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;


// calss that represents an order for the user
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer _orderId;
    private Map<Integer ,ShoppingBasket> _shoppingBasketMap; // <ShopId, ShoppingBasketPerShop> 
    private double _totalOrderAmount;

    // Constructor
    public Order(List<ShoppingBasket> shoppingBasket) throws StockMarketException {
        this._shoppingBasketMap = new HashMap<>();
        setShoppingBasketMap(shoppingBasket);
        this._totalOrderAmount = 0.0;
        setTotalOrderAmount();
    }

    @Generated(value = "exclude from test coverage")
    public Integer getOrderId() {
        return _orderId;
    }

    // This method is used to set the shoppingBasketMap when creating the order
    private void setShoppingBasketMap(List<ShoppingBasket> shoppingBaskets){
        for (ShoppingBasket basket : shoppingBaskets) {
            _shoppingBasketMap.put(basket.getShopId(), basket.clone());
        }
    }

    // This method is used to set the total order amount when creating the order
    private void setTotalOrderAmount() throws StockMarketException {
        _totalOrderAmount = 0.0;
        for (Map.Entry<Integer, ShoppingBasket> entry : _shoppingBasketMap.entrySet()) {
            _totalOrderAmount += entry.getValue().getShoppingBasketPrice();
        }
    }

    // This method is used to calculate the total order amount
    public void calcTotalAmount() throws StockMarketException { 
        _totalOrderAmount = 0.0;
        for (Map.Entry<Integer, ShoppingBasket> entry : _shoppingBasketMap.entrySet()) {
            _totalOrderAmount += entry.getValue().getShoppingBasketPrice();
        }
    }

    @Generated(value = "exclude from test coverage")
    // Helper method to print all products in the order by shopId
    private String printAllShopAndProducts() 
    {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, ShoppingBasket> entry : _shoppingBasketMap.entrySet()) {
            sb.append("ShopId: " + entry.getKey() + "\n");
            sb.append(printAllProducts(entry.getValue()));
        }
        return sb.toString();
    }

    @Generated(value = "exclude from test coverage")
    private String printAllProducts(ShoppingBasket shoppingBasket) {
        return shoppingBasket.printAllProducts();
    }
    
    @Generated(value = "exclude from test coverage")
    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + _orderId +
                ", totalAmount=" + _totalOrderAmount +
                ", products= \n" + printAllShopAndProducts() +
                '}';
    }

    // Getters

    @Generated(value = "exclude from test coverage")
    public Map<Integer ,ShoppingBasket> getProductsByShoppingBasket() {
        return _shoppingBasketMap;
    }

    public double getOrderTotalAmount() throws StockMarketException { 
        if(_totalOrderAmount == 0.0)
            calcTotalAmount();
        return _totalOrderAmount; 
    }
}
