package Domain.Entities;

import Exceptions.StockMarketException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

//class that represents an order for the shop
@Entity
@Table(name = "[shop_order]")
public class ShopOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer shopOrderId;

    @OneToOne(mappedBy = "shopOrder", optional = false, cascade = CascadeType.ALL)
    private ShoppingBasket shoppingBasket;

    @Column(name = "total_order_amount", nullable = false)
    private double totalOrderAmount;
    
    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    // Default constructor
    public ShopOrder() { }

    // Constructor
    //TODO - Metar: check why not applying the clone method of ShoppingBasket
    public ShopOrder(int orderId, int shopId, ShoppingBasket shoppingBasket) throws StockMarketException {
        this.shopOrderId = orderId;
        shoppingBasket = shoppingBasket.clone();
        totalOrderAmount = shoppingBasket.getShoppingBasketPrice();
    }

    public Integer getShopOrderId() {
        return shopOrderId;
    }

    
    public double getOrderTotalAmount() {
         return totalOrderAmount; 
        }

    public ShoppingBasket getShoppingBasket() {
        return shoppingBasket;
    }

    //Helper method to print all products in the order
     private String printAllProduct() 
        {
            return shoppingBasket.printAllProducts();
        }
   

    @Override
    public String toString() {
        return "Order{" +
                "orderId = " + shopOrderId +
                ", totalAmount = " + totalOrderAmount +
                ", products = \n" + printAllProduct() +
                '}';
    }
}
