package Domain;

//class that represents an order for the shop

public class ShopOrder {
    private Integer _orderId;
    private ShoppingBasket _shoppingBasket;
    private double _totalOrderAmount;

    // Constructor
    public ShopOrder(Integer orderId , Integer shopId,ShoppingBasket shoppingBasket) {
        this._orderId = orderId;
        this._shoppingBasket = shoppingBasket.clone();
        this._totalOrderAmount = _shoppingBasket.getShoppingBasketPrice();
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
            StringBuilder sb = new StringBuilder();
            for (Product product : _shoppingBasket.getProductList()) {
                sb.append(product.toString());
                sb.append("\n");
            }
            return sb.toString();
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
