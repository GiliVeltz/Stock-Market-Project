package Dtos;

import java.util.HashSet;
import java.util.Map;

import Domain.Product;

public class ProductGetterDto {
    private Integer _productId;
    private String _productName;
    private double _price;
    private Integer _quantity;
    private HashSet<String> _keywords;
    private Double _productRating;
    private Integer _productRatersCounter;
    private CategoryDto _category;
    private ProductPolicyDto _productPolicy;
    private Map<String, String> _reviews; // usernames and reviews

    // Constructor
    public ProductGetterDto(Integer productId, String productName, double price, Integer productQuantity,
    HashSet<String> _keywords, Double _productRating, Integer _productRatersCounter, 
    CategoryDto category, ProductPolicyDto _productPolicy, Map<String, String> _reviews) {
        this._productId = productId;
        this._productName = productName;
        this._price = price;
        this._quantity = productQuantity;
        this._keywords = _keywords;
        this._productRating = _productRating;
        this._productRatersCounter = _productRatersCounter;
        this._category = category;
        this._productPolicy = _productPolicy;
        this._reviews = _reviews;
    }

    public ProductGetterDto(Product product) {
        this._productId = product.getProductId();
        this._productName = product.getProductName();
        this._price = product.getPrice();
        this._quantity = product.getProductQuantity();
        this._keywords = product.getKeywords();
        this._productRating = product.getProductRating();
        this._productRatersCounter = product.getProductRatersCounter();
        this._category = new CategoryDto(product.getCategory());
        this._productPolicy = new ProductPolicyDto(product.getProductPolicy());
        this._reviews = product.getReviews();
    }
}
