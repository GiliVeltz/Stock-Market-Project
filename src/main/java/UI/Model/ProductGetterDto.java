package UI.Model;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
    private List<BasicDiscountDto> _productDiscounts;
    private Map<String, String> _reviews; // usernames and reviews

    // Constructor
    public ProductGetterDto(Integer productId, String productName, double price, Integer productQuantity,
    HashSet<String> _keywords, Double _productRating, Integer _productRatersCounter, 
    CategoryDto category, ProductPolicyDto _productPolicy, List<BasicDiscountDto> discountsList, Map<String, String> _reviews) {
        this._productId = productId;
        this._productName = productName;
        this._price = price;
        this._quantity = productQuantity;
        this._keywords = _keywords;
        this._productRating = _productRating;
        this._productRatersCounter = _productRatersCounter;
        this._category = category;
        this._productPolicy = _productPolicy;
        this._productDiscounts = discountsList;
        this._reviews = _reviews;
    }

    public Integer getProductId() {
        return _productId;
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

    public CategoryDto getCategory() {
        return _category;
    }

    public ProductPolicyDto getProductPolicy() {
        return _productPolicy;
    }

    public List<BasicDiscountDto> getProductDiscounts() {
        return _productDiscounts;
    }

    public Map<String, String> getReviews() {
        return _reviews;
    }

    public void setProductId(Integer productId) {
        this._productId = productId;
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

    public void setCategory(CategoryDto category) {
        this._category = category;
    }

    public void setProductPolicy(ProductPolicyDto productPolicy) {
        this._productPolicy = productPolicy;
    }

    public void setProductDiscounts(List<BasicDiscountDto> productDiscounts) {
        this._productDiscounts = productDiscounts;
    }

    public void setReviews(Map<String, String> reviews) {
        this._reviews = reviews;
    }

}