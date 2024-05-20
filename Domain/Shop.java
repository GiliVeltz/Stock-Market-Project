import java.util.HashMap;
import java.util.Map;

public class Shop {
    private Integer _shopId;
    private String _shopFounder;
    private Map<Integer ,Product> _productMap; // <ProductId, Product> 
    private List<ShoppingBasket> _orderHistory; 
    //private Map<String, List<Permission>> //<userName, List<Permissions>
    //TODO:: List<Discount> 

    // Constructor
    public Shop(Integer shopId, String shopFounder){
        this._shopId = shopId;
        this._shopFounder = shopFounder;
        this._productMap = new HashMap<>(); // Initialize the product map
        this._orderHistory = new ArrayList<>();
    }

    public void addProductToShop(Product product) {
        if (_productMap.containsKey(product.getProductId())) {
            throw new ProductAlreadyExistsException("Product with ID " + product.getProductId() + " already exists.");
        }
        _productMap.put(product.getProductId(), product); // Add product to the map
    }

    public Product getProductById(Integer productId) {
        return _productMap.get(productId); // Get product by ID from the map
    }

    public Map<Integer ,Product> getShopProducts() {
        return _productMap;
    }

    public List<ShoppingBasket> getShopOrderHistory() {
        return _orderHistory;
    }

    public void addOrderToOrderHistory(ShoppingBasket order) {
        _orderHistory.add(order); // Add order to the history
    }

    @Override
    public String toString() {
        return "Shop{" +
                "Shop ID=" + _shopId +
                ", Shop Founder=" + _shopFounder +
                ", Products= \n" + _productMap +
                ", Order History= \n " + _orderHistory +
                '}';
    }
}
