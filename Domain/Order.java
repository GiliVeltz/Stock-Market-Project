import java.util.HashMap;
import java.util.Map;

public class Order {
    private Integer m_OrderId;
    private Map<Integer ,ShoppingBasket> m_ShoppingBasketMap; // <ShopId, ShoppingBasketPerShop>
    private double m_TotalOrderAmount;

    // Constructor
    public Order(Integer orderId) {
        this.m_OrderId = orderId;
        this.m_ShoppingBasketMap = new HashMap<>();
        this.m_TotalOrderAmount = 0.0;
    }

    public Integer getOrderId() {
        return m_OrderId;
    }

    public Map<Integer ,ShoppingBasket> getProductsByShoppingBasket() {
        return m_ShoppingBasketMap;
    }

    public double getOrderTotalAmount() { return m_TotalOrderAmount; }

    // Add a product to the order under a specific shop
    public void addProductToOrder(Product pi_Product, Integer pi_ShopId) {
        m_ShoppingBasketMap.putIfAbsent(pi_ShopId, new ShoppingBasket(pi_ShopId));
        m_ShoppingBasketMap.get(pi_Product).AddProductToShoppingBasket(pi_Product);
        calcTotalAmount();
    }

    public void calcTotalAmount() { 
        m_TotalOrderAmount = 0.0;
        for (Map.Entry<Integer, ShoppingBasket> entry : map.entrySet()) {
            m_TotalOrderAmount += entry.getValue().getShoppingBasketPrice();
        }
    }

    // Helper method to print all products in the order
    private String printAllProduct() 
    {
        StringBuilder output = new StringBuilder();
        for (Map.Entry<Integer, ShoppingBasket> entry : map.entrySet()) {
            output.append(entry.getValue().toString()).append("\n");
        }
        return output;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + m_OrderId +
                ", totalAmount=" + totalAmount +
                ", products= \n" + printAllProduct() +
                '}';
    }
}
