package Dtos;

import java.util.HashSet;
import java.util.Map;

import Domain.Product;

public class ProductGetterDto {
    @SuppressWarnings("unused")
    private Integer _productId;
    @SuppressWarnings("unused")
    private String _productName;
    @SuppressWarnings("unused")
    private double _price;
    @SuppressWarnings("unused")
    private Integer _quantity;
    @SuppressWarnings("unused")
    private HashSet<String> _keywords;
    @SuppressWarnings("unused")
    private Double _productRating;
    @SuppressWarnings("unused")
    private Integer _productRatersCounter;
    @SuppressWarnings("unused")
    private CategoryDto _category;
    @SuppressWarnings("unused")
    private ProductPolicyDto _productPolicy;
    @SuppressWarnings("unused")
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
