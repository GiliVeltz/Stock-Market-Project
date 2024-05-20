import java.util.HashMap;
import java.util.Map;

public class Shop {
    private Integer m_ShopId;
    private String m_ShopFounder;
    private Map<Integer ,Product> m_ProductMap; // <ProductId, Product> 
    private List<ShoppingBasket> m_OrderHistory; 

    // Constructor
    public Shop(Integer pi_ShopId, String pi_ShopFounder){
        this.m_ShopId = pi_ShopId;
        this.m_ShopFounder = pi_ShopFounder;
        this.m_ProductMap = new HashMap<>(); // Initialize the product map
        this.m_OrderHistory = new ArrayList<>();
    }

    public void addProductToShop(Product pi_Product) {
        m_ProductList.add(pi_product); // Add product to the map
    }

    public Product findProductById(Integer pi_ProductId) {
        return m_ProductMap.get(pi_ProductId); // Get product by ID from the map
    }

    public Map<Integer ,Product> getShopProducts() {
        return m_ProductMap;
    }

    public List<ShoppingBasket> getShopOrderHistory() {
        return m_OrderHistory;
    }

    public void createOrder(ShoppingBasket pi_Order) {
        m_OrderHistory.add(pi_Order); // Add order to the history
    }

    @Override
    public String toString() {
        return "Shop{" +
                "Shop ID=" + m_ShopId +
                ", Shop Founder=" + m_ShopFounder +
                ", Products= \n" + m_ProductMap +
                ", Order History= \n " + m_OrderHistory +
                '}';
    }
}
