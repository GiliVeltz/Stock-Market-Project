package Domain;

import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Domain.Policies.ProductPolicy;
import Exceptions.ProductOutOfStockExepction;
import Exceptions.StockMarketException;
import enums.Category;

public class Product implements Cloneable {
    private Integer _productId;
    private String _productName;
    private double _price;
    private Integer _quantity;
    private HashSet<String> _keywords;
    private Double _productRating;
    private Integer _productRatersCounter;
    private Category _category;
    private ProductPolicy _productPolicy;
    private static final Logger logger = Logger.getLogger(Product.class.getName());

    // Constructor
    public Product(Integer productId, String productName, Category category, double price) {
        this._productId = productId;
        this._productName = productName;
        this._category = category;
        this._price = price;
        this._quantity = 0;
        this._keywords = new HashSet<>();
        this._productRating = -1.0;
        this._productRatersCounter = 0;
        this._productPolicy = new ProductPolicy();
    }

    public int getProductId() {
        return _productId;
    }

    public String getProductName() {
        return _productName;
    }

    public Category getCategory() {
        return _category;
    }

    public double getPrice() {
        return _price;
    }

    public void addKeyword(String keyword) {
        _keywords.add(keyword);
    }

    public Double getProductRating() {
        return _productRating;
    }

    public ProductPolicy getProductPolicy(){
        return _productPolicy;
    }

    //set product price
    public void setPrice(double price) {
        _price = price;
    }

    public void addProductRating(Integer rating) throws StockMarketException {
        if(rating > 5 || rating < 1)
            throw new StockMarketException(String.format("Product ID: %d rating is not in range 1 to 5.", _productId));

        Double newRating = Double.valueOf(rating);
        if (_productRating == -1.0) {
            _productRating = newRating;
        } else {
            _productRating = ((_productRating * _productRatersCounter) + newRating) / (_productRatersCounter + 1);
        }
        _productRatersCounter++;
    }

    public void purchaseProduct() throws ProductOutOfStockExepction {
        if (_quantity == 0) {
            logger.log(Level.SEVERE, "Product - purchaseProduct - Product " + _productName + " with id: " + _productId
                    + " out of stock -- thorwing ProductOutOfStockExepction.");
            throw new ProductOutOfStockExepction("Product is out of stock");
        }
        _quantity--;
        logger.log(Level.FINE, "Product - purchaseProduct - Product " + _productName + " with id: " + _productId
                + " had been purchased -- -1 to stock.");
    }

    public void cancelPurchase() {
        _quantity++;
        logger.log(Level.FINE, "Product - cancelPurchase - Product " + _productName + " with id: " + _productId
                + " had been purchased cancel -- +1 to stock.");
    }

        @Override
        public Product clone() {
            try {
                return (Product) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(); // Can't happen as we implement Cloneable
            }
        }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + _productId +
                ", name='" + _productName + '\'' +
                ", category=" + _category +
                ", price=" + _price +
                ", keywords=" + _keywords +
                ", rating=" + _productRating +
                '}';
    }

    public boolean isKeywordExist(String keyword) {
        return _keywords.contains(keyword);
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

    public Integer getProductQuantity() {
        return _quantity;
    }

    public String getProductPolicyInfo() {
        return _productPolicy.toString();
    }

    public String getProductGeneralInfo() {
        return "Product ID: " + _productId + " | Product Name: " + _productName + " | Product Category: " + _category + " | Product Price: " + _price + " | Product Quantity: " + _quantity + " | Product Rating: " + _productRating;
    }
}
