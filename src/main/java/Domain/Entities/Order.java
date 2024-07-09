package Domain.Entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import Exceptions.StockMarketException;

// calss that represents an order for the user
@Entity
@Table(name = "[order]")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer _orderId;

    // Transient because it's not persisted in the database, just used in memory
    @Transient
    private Map<Integer, ShoppingBasket> _shoppingBasketMap; // <ShopId, ShoppingBasketPerShop> 
    
    private double _totalOrderAmount;

    @ManyToOne

    // Default constructor
    public Order() {
    }
    
    // Constructor
    public Order(int orderId, List<ShoppingBasket> shoppingBasket) throws StockMarketException {
        _orderId = orderId;
        this._shoppingBasketMap = new HashMap<>();
        setShoppingBasketMap(shoppingBasket);
        this._totalOrderAmount = 0.0;
        setTotalOrderAmount();
    }

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

    // Getters

    public Map<Integer ,ShoppingBasket> getProductsByShoppingBasket() {
        return _shoppingBasketMap;
    }

    public double getOrderTotalAmount() throws StockMarketException { 
        if(_totalOrderAmount == 0.0)
            calcTotalAmount();
        return _totalOrderAmount; 
    }

    public Map<Integer, ShoppingBasket> getShoppingBasketMap() {
        return _shoppingBasketMap;
    }

    // for tests - get all product ids
    public List<Integer> getAllProductIds() {
        List<Integer> allProductIds = new java.util.ArrayList<>();
        for (Map.Entry<Integer, ShoppingBasket> entry : _shoppingBasketMap.entrySet()) {
            allProductIds.addAll(entry.getValue().getProductIdList());
        }
        return allProductIds;
    }
}
