package Domain;

import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Domain.Exceptions.ProductOutOfStockExepction;

public class Product {
    private Integer _productId;
    private String _productName;
    private double _price;
    private Integer _quantity;
    private HashSet<String> _keywords;
    private Double _rating;
    private Integer _ratersCounter;
    private static final Logger logger = Logger.getLogger(UserFacade.class.getName());
    //TODO: private List<discount> 

    // Constructor
    public Product(Integer productId, String productName, double price) {
        this._productId = productId;
        this._productName = productName;
        this._price = price;
        this._quantity = 0;
        this._keywords = new HashSet<>();
        this._rating = -1.0;
        this._ratersCounter = 0;
    }

    public int getProductId() {
        return _productId;
    }

    public String getProductName() {
        return _productName;
    }

    public double getPrice() {
        return _price;
    }

    public void addKeyword(String keyword) {
        _keywords.add(keyword);
    }

    public Double getRating() {
        return _rating;
    }

    public void addRating(Integer rating) {
        //TODO: limit the rating to 1-5 
        Double newRating = Double.valueOf(rating);
        if (_rating == -1.0) {
            _rating = newRating;
        } else {
            _rating = ((_rating * _ratersCounter) + newRating) / (_ratersCounter + 1);
        }
        _ratersCounter++;
    }

    public void purchaseProduct() {
        if (_quantity == 0) {
            logger.log(Level.SEVERE, "Product - purchaseProduct - Product " + _productName + " with id: " + _productId + " out of stock -- thorwing ProductOutOfStockExepction.");
            throw new ProductOutOfStockExepction("Product is out of stock");
        }
        _quantity--;
        logger.log(Level.FINE, "Product - purchaseProduct - Product " + _productName + " with id: " + _productId + " had been purchased -- -1 to stock.");
    }

    public void cancelPurchase() {
        _quantity++;
        logger.log(Level.FINE, "Product - cancelPurchase - Product " + _productName + " with id: " + _productId + " had been purchased cancel -- +1 to stock.");
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + _productId +
                ", name='" + _productName + '\'' +
                ", price=" + _price +
                ", keywords=" + _keywords +
                ", rating=" + _rating +
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
    
}
