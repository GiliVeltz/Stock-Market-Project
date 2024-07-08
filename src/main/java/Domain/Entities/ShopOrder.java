package Domain.Entities;

import Exceptions.StockMarketException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;

//class that represents an order for the shop
@Entity
public class ShopOrder {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer _orderId;

    // @OneToOne(mappedBy = "shopOrder")
    @Transient
    private ShoppingBasket _shoppingBasket;

    @Column(name = "totalOrderAmount", nullable = false)
    private double _totalOrderAmount;

    // Constructor
    //TODO - Metar: check why not applying the clone method of ShoppingBasket
    public ShopOrder(int orderId, int shopId, ShoppingBasket shoppingBasket) throws StockMarketException {
        _orderId = orderId;
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
