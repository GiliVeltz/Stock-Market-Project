package UI.Model;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotBlank;

public class ProductGetterDto {

    @NotBlank(message = "Product Id is required")
    private Integer _productId;

    @NotBlank(message = "Product Name is required")
    private String _productName;

    @NotBlank(message = "Price is required")
    private double _price;

    @NotBlank(message = "Product Quantity is required")
    private Integer _quantity;

    @NotBlank(message = "Keywords are required")
    private Set<String> _keywords;

    @NotBlank(message = "Product Rating is required")
    private Double _productRating;

    @NotBlank(message = "Product Raters Counter is required")
    private Integer _productRatersCounter;

    @NotBlank(message = "Category is required")
    private CategoryDto _category;

    // @NotBlank(message = "Product Policy is required")   
    // private ProductPolicyDto _productPolicy;

    @NotBlank(message = "Product Discounts are required")   
    private List<BasicDiscountDto> _productDiscounts;

    @NotBlank(message = "Reviews are required")
    private Map<String, String> _reviews; // usernames and reviews

    public ProductGetterDto() {
    }

    // Constructor
    // public ProductGetterDto(Integer productId, String productName, double price, Integer productQuantity,
    // HashSet<String> _keywords, Double _productRating, Integer _productRatersCounter, 
    // CategoryDto category, ProductPolicyDto _productPolicy, List<BasicDiscountDto> discountsList, Map<String, String> _reviews) {
    //     this._productId = productId;
    //     this._productName = productName;
    //     this._price = price;
    //     this._quantity = productQuantity;
    //     this._keywords = _keywords;
    //     this._productRating = _productRating;
    //     this._productRatersCounter = _productRatersCounter;
    //     this._category = category;
    //     this._productPolicy = _productPolicy;
    //     this._productDiscounts = discountsList;
    //     this._reviews = _reviews;
    // }

     // Constructor without productPolicy
     public ProductGetterDto(Integer productId, String productName, double price, Integer productQuantity,
     HashSet<String> _keywords, Double _productRating, Integer _productRatersCounter, 
     CategoryDto category, List<BasicDiscountDto> discountsList, Map<String, String> _reviews) {
         this._productId = productId;
         this._productName = productName;
         this._price = price;
         this._quantity = productQuantity;
         this._keywords = _keywords;
         this._productRating = _productRating;
         this._productRatersCounter = _productRatersCounter;
         this._category = category;
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

    public Set<String> getKeywords() {
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

    // public ProductPolicyDto getProductPolicy() {
    //     return _productPolicy;
    // }

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

    // public void setProductPolicy(ProductPolicyDto productPolicy) {
    //     this._productPolicy = productPolicy;
    // }

    public void setProductDiscounts(List<BasicDiscountDto> productDiscounts) {
        this._productDiscounts = productDiscounts;
    }

    public void setReviews(Map<String, String> reviews) {
        this._reviews = reviews;
    }

}