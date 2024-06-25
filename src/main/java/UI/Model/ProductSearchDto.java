package UI.Model;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import enums.Category;

@JsonIgnoreProperties(ignoreUnknown = true)

public class ProductSearchDto {
    @NotBlank(message = "Shop name is required (or null)")
    private String shopName;

    @NotBlank(message = "Product name is required (or null)")
    private String productName;
    
    @NotBlank(message = "Category is required (or null)")
    private String category;

    @NotBlank(message = "Keywords are required (or null)")
    private List<String> keywords;

    // Constructor
    public ProductSearchDto(){}

    public ProductSearchDto(String shopName, String productName, String category, List<String> keywords) {
        this.shopName = shopName;
        this.productName = productName;
        this.category = category;
        this.keywords = keywords;
    }

    // Getters and setters

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String newShopName) {
        this.shopName = newShopName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String newProductName) {
        this.productName = newProductName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String newCategory) {
        this.category = newCategory;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> newKeywords) {
        this.keywords = newKeywords;
    }
}