package Domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Exceptions.StockMarketException;

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

    public Integer getOrderId() {
        return _orderId;
    }
    private void setShoppingBasketMap(List<ShoppingBasket> shoppingBaskets){
        for (ShoppingBasket basket : shoppingBaskets) {
            _shoppingBasketMap.put(basket.getShopId(), basket.clone());
        }
    }

    private void setTotalOrderAmount() throws StockMarketException {
        _totalOrderAmount = 0.0;
        for (Map.Entry<Integer, ShoppingBasket> entry : _shoppingBasketMap.entrySet()) {
            _totalOrderAmount += entry.getValue().getShoppingBasketPrice();
        }
    }

    public Map<Integer ,ShoppingBasket> getProductsByShoppingBasket() {
        return _shoppingBasketMap;
    }

    public double getOrderTotalAmount() throws StockMarketException { 
        if(_totalOrderAmount == 0.0)
            calcTotalAmount();
        return _totalOrderAmount; 
    }

    public void calcTotalAmount() throws StockMarketException { 
        _totalOrderAmount = 0.0;
        for (Map.Entry<Integer, ShoppingBasket> entry : _shoppingBasketMap.entrySet()) {
            _totalOrderAmount += entry.getValue().getShoppingBasketPrice();
        }
    }

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

    private String printAllProducts(ShoppingBasket shoppingBasket) {
       return shoppingBasket.printAllProducts();
        
    }
    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + _orderId +
                ", totalAmount=" + _totalOrderAmount +
                ", products= \n" + printAllShopAndProducts() +
                '}';
    }
}
