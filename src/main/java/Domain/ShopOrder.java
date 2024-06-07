package Domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import Exceptions.StockMarketException;

//class that represents an order for the shop
@Entity
public class ShopOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer _orderId;
    private ShoppingBasket _shoppingBasket;
    private double _totalOrderAmount;

    // Constructor
    //TODO - Metar: check why not applying the clone method of ShoppingBasket
    public ShopOrder(Integer shopId,ShoppingBasket shoppingBasket) throws StockMarketException {
        _shoppingBasket = shoppingBasket.clone();
        _totalOrderAmount = _shoppingBasket.getShoppingBasketPrice();
    }

    public Integer getOrderId() {
        return _orderId;
    }

    
    public double getOrderTotalAmount() {
         return _totalOrderAmount; 
        }

    public ShoppingBasket getShoppingBasket() {
        return _shoppingBasket;
    }

    //Helper method to print all products in the order
     private String printAllProduct() 
        {
            return _shoppingBasket.printAllProducts();
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
