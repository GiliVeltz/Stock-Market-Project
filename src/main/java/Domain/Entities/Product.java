package Domain.Entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.Map;

import Domain.Entities.Policies.ProductPolicy;
import Domain.Entities.enums.Category;
import Exceptions.ProductOutOfStockExepction;
import Exceptions.StockMarketException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class Product implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer productId;

    @Column(name = "product_name", nullable = false)
    private String _productName;

    @Column(name = "price", nullable = false)
    private double _price;

    @Column(name = "quantity", nullable = false)
    private Integer _quantity;

    @Transient
    private HashSet<String> _keywords;

    @Column(name = "product_rating", nullable = true)
    private Double _productRating;

    @Column(name = "product_raters_counter", nullable = true)
    private Integer _productRatersCounter;

    @Transient
    private Category _category;

    @Transient
    private ProductPolicy _productPolicy;

    @Transient
    private Map<String, String> _reviews; // usernames and reviews

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "shopping_basket_id", nullable = false)
    private ShoppingBasket shoppingBasket;

    private static final Logger logger = Logger.getLogger(Product.class.getName());

    // Default constructor
    public Product() { }

    // Constructor
    public Product(int productId, String productName, Category category, double price) {
        this.productId = productId;
        this._productName = productName;
        this._category = category;
        this._price = price;
        this._quantity = 0;
        this._keywords = new HashSet<>();
        this._keywords.add(productName);
        this._keywords.add(category.toString());
        this._productRating = -1.0;
        this._productRatersCounter = 0;
        this._productPolicy = new ProductPolicy();
        this._reviews = new HashMap<>();
    }

    // this function responsible for adding a rating to the product
    public void addProductRating(Integer rating) throws StockMarketException {
        if(rating > 5 || rating < 1)
            throw new StockMarketException(String.format("Product ID: %d rating is not in range 1 to 5.", productId));
        Double newRating = Double.valueOf(rating);
        if (_productRating == -1.0) {
            _productRating = newRating;
        } else {
            _productRating = ((_productRating * _productRatersCounter) + newRating) / (_productRatersCounter + 1);
        }
        _productRatersCounter++;
    }

    // this function responsible for purchasing a product: decrease the quantity of the product by 1 and add the product to the user's cart
    public synchronized void purchaseProduct() throws StockMarketException {
        if (_quantity == 0) {
            logger.log(Level.SEVERE, "Product - purchaseProduct - Product " + _productName + " with id: " + productId
                    + " out of stock -- thorwing ProductOutOfStockExepction.");
            throw new ProductOutOfStockExepction("Product is out of stock");
        }
        _quantity--;
        logger.log(Level.FINE, "Product - purchaseProduct - Product " + _productName + " with id: " + productId
                + " had been purchased -- -1 to stock.");
    }

    public synchronized void cancelPurchase() {
        _quantity++;
        logger.log(Level.FINE, "Product - cancelPurchase - Product " + _productName + " with id: " + productId
                + " had been purchased cancel -- +1 to stock.");
    }

    // this function add a review to the product
    public void addReview(String username, String review) {
        _reviews.put(username, review);
    }

    @Override
    public Product clone() {
        try {
            return (Product) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Can't happen as we implement Cloneable
        }
    }

    public boolean isKeywordExist(String keyword) {
        return _keywords.stream().anyMatch(k -> k.equalsIgnoreCase(keyword));
    }

    public boolean isKeywordListExist(List<String> keywords) {
        for (String keyword : keywords) {
            if (isKeywordExist(keyword)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPriceInRange(double minPrice, double maxPrice) {
        return _price >= minPrice && _price <= maxPrice;
    }
    

    public void updateProductQuantity(int newQuantitiy)
    {
        _quantity = newQuantitiy;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + productId +
                ", name='" + _productName + '\'' +
                ", category=" + _category +
                ", price=" + _price +
                ", keywords=" + _keywords +
                ", rating=" + _productRating +
                '}';
    }

    // Getters and Setters

    public String getProductPolicyInfo() {
        return _productPolicy.toString();
    }

    public String getProductGeneralInfo() {
        return "Product ID: " + productId + " | Product Name: " + _productName + " | Product Category: " + _category + " | Product Price: " + _price + " | Product Quantity: " + _quantity + " | Product Rating: " + _productRating;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return _productName;
    }

    public double getPrice() {
        return _price;
    }

    public Integer getProductQuantity() {
        return _quantity;
    }

    public HashSet<String> getKeywords() {
        return _keywords;
    }

    public Double getProductRating() {
        return _productRating;
    }

    public Integer getProductRatersCounter() {
        return _productRatersCounter;
    }

    public Category getCategory() {
        return _category;
    }

    public ProductPolicy getProductPolicy() {
        return _productPolicy;
    }

    public Map<String, String> getReviews() {
        return _reviews;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this._productName = productName;
    }

    public void setPrice(double price) {
        this._price = price;
    }

    public void setProductQuantity(Integer productQuantity) {
        this._quantity = productQuantity;
    }

    public void setKeywords(HashSet<String> keywords) {
        this._keywords = keywords;
    }

    public void setProductRating(Double productRating) {
        this._productRating = productRating;
    }

    public void setProductRatersCounter(Integer productRatersCounter) {
        this._productRatersCounter = productRatersCounter;
    }

    public void setCategory(Category category) {
        this._category = category;
    }

    public void setProductPolicy(ProductPolicy productPolicy) {
        this._productPolicy = productPolicy;
    }

    public void setReviews(Map<String, String> reviews) {
        this._reviews = reviews;
    }

    public void setKeywords(String keyword) {
        this._keywords.add(keyword);
    }

    public void removeKeywords(String keyword) {
        this._keywords.remove(keyword);
    }

    public void setProductRating(double productRating) {
        this._productRating = productRating;
    }

    public void setProductRatersCounter(int productRatersCounter) {
        this._productRatersCounter = productRatersCounter;
    }

    public void addKeyword(String keyword) {
        _keywords.add(keyword);
    }
}
