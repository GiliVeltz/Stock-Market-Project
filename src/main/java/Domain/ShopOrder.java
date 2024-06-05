package Domain;

import Exceptions.StockMarketException;

//class that represents an order for the shop
public class ShopOrder {
    private Integer _orderId;
    private ShoppingBasket _shoppingBasket;
    private double _totalOrderAmount;

    // Constructor
    //TODO - Metar: check why not applying the clone method of ShoppingBasket
    public ShopOrder(int orderId, Integer shopId,ShoppingBasket shoppingBasket) throws StockMarketException {
        this._orderId = orderId;
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
