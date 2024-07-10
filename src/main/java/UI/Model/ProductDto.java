package UI.Model;

import java.util.Set;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDto {
    private int productId;

    @NotBlank(message = "Product name is required")
    private String productName;
    
    @NotBlank(message = "Category is required")
    private Category category;

    @NotBlank(message = "Price is required")
    private double price;

    @NotBlank(message = "Product quantity is required")
    private int productQuantity;

    // @NotBlank(message = "Product rating is required")
    private double productRating;

    // @NotBlank(message = "Keywords are required")
    private Set<String> keywords;

    // Constructor
    public ProductDto(){}

    public ProductDto(String productName, Category category, double price, int productQuantity) {
        this.productId = -1;
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.productQuantity = productQuantity;
    }

    public ProductDto(int productId, String productName, Category category, double price, int productQuantity, double productRating, Set<String> keywords) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.productQuantity = productQuantity;
        this.productRating = productRating;
        this.keywords = keywords;
    }

    // Getters and setters
    public int getProductId() {
        return this.productId;
    }

    public void setProductId(int newProductId) {
        this.productId = newProductId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String newProductName) {
        this.productName = newProductName;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category newCategory) {
        this.category = newCategory;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double newPrice) {
        this.price = newPrice;
    }

    public double getProductRating() {
        return this.productRating;
    }

    public void setProductRating(double newRating) {
        this.productRating = newRating;
    }

    public Set<String> getKeywords() {
        return this.keywords;
    }

    public void setKeywords(Set<String> newKeywords) {
        this.keywords = newKeywords;
    }

    // public Integer getProductQuantity() { // Update getter name
    //     return this.productQuantity;
    // }

    // public void setProductQuantity(int newQuantity) { // Update setter name
    //     this.productQuantity = newQuantity;
    // }
}
