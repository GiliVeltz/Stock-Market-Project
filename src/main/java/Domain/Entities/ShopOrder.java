package Domain.Entities;

import Exceptions.StockMarketException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.CascadeType; // Add this line
import jakarta.persistence.JoinColumn;
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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "basket_id")
    private ShoppingBasket shoppingBasket;

    @Column(name = "total_order_amount", nullable = false)
    private double totalOrderAmount;
    

    // Default constructor
    public ShopOrder() { }

    // Constructor
    public ShopOrder(int shopId, ShoppingBasket shoppingBasket) throws StockMarketException {
        this.shoppingBasket = shoppingBasket;
        this.totalOrderAmount = shoppingBasket.getShoppingBasketPrice();
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

    public void setShoppingBasket(ShoppingBasket shoppingBasket) {
        this.shoppingBasket = shoppingBasket;
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

    public Object getId() {
        return shopOrderId;
    }

    public void setId(int size) {
        this.shopOrderId = size;
    }
}
