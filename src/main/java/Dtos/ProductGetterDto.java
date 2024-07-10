package Dtos;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import Domain.Entities.Product;

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
    // @SuppressWarnings("unused")
    // private ProductPolicyDto _productPolicy;
    @SuppressWarnings("unused")
    private List<BasicDiscountDto> _productDiscounts;
    @SuppressWarnings("unused")
    private Map<String, String> _reviews; // usernames and reviews

    // // Constructor
    // public ProductGetterDto(Integer productId, String productName, double price, Integer productQuantity,
    // HashSet<String> _keywords, Double _productRating, Integer _productRatersCounter, 
    // CategoryDto category, ProductPolicyDto _productPolicy, Map<String, String> _reviews) {
    //     this._productId = productId;
    //     this._productName = productName;
    //     this._price = price;
    //     this._quantity = productQuantity;
    //     this._keywords = _keywords;
    //     this._productRating = _productRating;
    //     this._productRatersCounter = _productRatersCounter;
    //     this._category = category;
    //     this._productPolicy = _productPolicy;
    //     this._reviews = _reviews;
    // }

    // Constructor without productPolicy
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
        // this._productPolicy = new ProductPolicyDto(product.getProductPolicy());
        this._reviews = product.getReviews();
    }


    public Integer getProductId() {
        return _productId;
    }

    public void setProductId(Integer _productId) {
        this._productId = _productId;
    }

    public String getProductName() {
        return _productName;
    }

    public void setProductName(String _productName) {
        this._productName = _productName;
    }

    public double getPrice() {
        return _price;
    }

    public void setPrice(double _price) {
        this._price = _price;
    }

    public Integer getProductQuantity() {
        return _quantity;
    }

    public void setProductQuantity(Integer _quantity) {
        this._quantity = _quantity;
    }

    public HashSet<String> getKeywords() {
        return _keywords;
    }

    public void setKeywords(HashSet<String> _keywords) {
        this._keywords = _keywords;
    }

    public Double getProductRating() {
        return _productRating;
    }

    public void setProductRating(Double _productRating) {
        this._productRating = _productRating;
    }

    public Integer getProductRatersCounter() {
        return _productRatersCounter;
    }

    public void setProductRatersCounter(Integer _productRatersCounter) {
        this._productRatersCounter = _productRatersCounter;
    }

    public CategoryDto getCategory() {
        return _category;
    }

    public void setCategory(CategoryDto _category) {
        this._category = _category;
    }

    // public ProductPolicyDto getProductPolicy() {
    //     return _productPolicy;
    // }

    // public void setProductPolicy(ProductPolicyDto _productPolicy) {
    //     this._productPolicy = _productPolicy;
    // }

    public List<BasicDiscountDto> getProductDiscounts() {
        return _productDiscounts;
    }

    public void setProductDiscounts(List<BasicDiscountDto> _productDiscounts) {
        this._productDiscounts = _productDiscounts;
    }

    public Map<String, String> getReviews() {
        return _reviews;
    }

    public void setReviews(Map<String, String> _reviews) {
        this._reviews = _reviews;
    }
}
