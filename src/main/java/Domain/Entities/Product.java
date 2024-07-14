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
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;

@Entity
public class Product implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer productId;

    @Column(name = "productName", nullable = false)
    private String productName;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ElementCollection(fetch = FetchType.LAZY) // Lazy loading is often a good default for collections
    @CollectionTable(name = "product_keywords", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "keyword") // Name of the column in the collection table
    private HashSet<String> keywords;

    @Column(name = "productRating", nullable = true)
    private Double productRating;

    @Column(name = "productRatersCounter", nullable = true)
    private Integer productRatersCounter;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Transient
    private ProductPolicy productPolicy;

    @Transient
    private Map<String, String> reviews; // usernames and reviews

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "shopping_basket_id", nullable = true)
    private ShoppingBasket shoppingBasket;

    private static final Logger logger = Logger.getLogger(Product.class.getName());

    // Default constructor
    public Product() { }

    // Constructor
    public Product(String productName, Category category, double price, Shop shop) {
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.quantity = 0;
        this.keywords = new HashSet<>();
        this.keywords.add(productName);
        this.keywords.add(category.toString());
        this.productRating = -1.0;
        this.productRatersCounter = 0;
        this.productPolicy = new ProductPolicy();
        this.reviews = new HashMap<>();
        this.shop = shop;
    }

    // this function responsible for adding a rating to the product
    public void addProductRating(Integer rating) throws StockMarketException {
        if(rating > 5 || rating < 1)
            throw new StockMarketException(String.format("Product ID: %d rating is not in range 1 to 5.", productId));
        Double newRating = Double.valueOf(rating);
        if (productRating == -1.0) {
            productRating = newRating;
        } else {
            productRating = ((productRating * productRatersCounter) + newRating) / (productRatersCounter + 1);
        }
        productRatersCounter++;
    }

    // this function responsible for purchasing a product: decrease the quantity of the product by 1 and add the product to the user's cart
    public synchronized void purchaseProduct() throws StockMarketException {
        if (quantity == 0) {
            logger.log(Level.SEVERE, "Product - purchaseProduct - Product " + productName + " with id: " + productId
                    + " out of stock -- thorwing ProductOutOfStockExepction.");
            throw new ProductOutOfStockExepction("Product is out of stock");
        }
        quantity--;
        logger.log(Level.FINE, "Product - purchaseProduct - Product " + productName + " with id: " + productId
                + " had been purchased -- -1 to stock.");
    }

    public synchronized void cancelPurchase() {
        quantity++;
        logger.log(Level.FINE, "Product - cancelPurchase - Product " + productName + " with id: " + productId
                + " had been purchased cancel -- +1 to stock.");
    }

    // this function add a review to the product
    public void addReview(String username, String review) {
        reviews.put(username, review);
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
        return keywords.stream().anyMatch(k -> k.equalsIgnoreCase(keyword));
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
        return price >= minPrice && price <= maxPrice;
    }
    

    public void updateProductQuantity(int newQuantitiy)
    {
        quantity = newQuantitiy;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + productId +
                ", name='" + productName + '\'' +
                ", category=" + category +
                ", price=" + price +
                ", keywords=" + keywords +
                ", rating=" + productRating +
                '}';
    }

    // Getters and Setters

    public String getProductPolicyInfo() {
        return productPolicy.toString();
    }

    public String getProductGeneralInfo() {
        return "Product ID: " + productId + " | Product Name: " + productName + " | Product Category: " + category + " | Product Price: " + price + " | Product Quantity: " + quantity + " | Product Rating: " + productRating;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public Integer getProductQuantity() {
        return quantity;
    }

    public HashSet<String> getKeywords() {
        return keywords;
    }

    public Double getProductRating() {
        return productRating;
    }

    public Integer getProductRatersCounter() {
        return productRatersCounter;
    }

    public Category getCategory() {
        return category;
    }

    public ProductPolicy getProductPolicy() {
        return productPolicy;
    }

    public Map<String, String> getReviews() {
        return reviews;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setProductQuantity(Integer productQuantity) {
        this.quantity = productQuantity;
    }

    public void setKeywords(HashSet<String> keywords) {
        this.keywords = keywords;
    }

    public void setProductRating(Double productRating) {
        this.productRating = productRating;
    }

    public void setProductRatersCounter(Integer productRatersCounter) {
        this.productRatersCounter = productRatersCounter;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setProductPolicy(ProductPolicy productPolicy) {
        this.productPolicy = productPolicy;
    }

    public void setReviews(Map<String, String> reviews) {
        this.reviews = reviews;
    }

    public void setKeywords(String keyword) {
        this.keywords.add(keyword);
    }

    public void removeKeywords(String keyword) {
        this.keywords.remove(keyword);
    }

    public void setProductRating(double productRating) {
        this.productRating = productRating;
    }

    public void setProductRatersCounter(int productRatersCounter) {
        this.productRatersCounter = productRatersCounter;
    }

    public void addKeyword(String keyword) {
        keywords.add(keyword);
    }
}
