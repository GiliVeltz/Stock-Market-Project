package UI.Model;

import java.util.List;
import java.util.Locale;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import Domain.Entities.enums.Category;

@JsonIgnoreProperties(ignoreUnknown = true)

public class ProductSearchDto {
    @NotBlank(message = "Shop id is required (or null)")
    private Integer shopId;

    @NotBlank(message = "Product name is required (or null)")
    private String productName;
    
    @NotBlank(message = "Category is required (or null)")
    private Category category;

    @NotBlank(message = "Keywords are required (or null)")
    private List<String> keywords;

    // Constructor
    public ProductSearchDto(){}

    public ProductSearchDto(Integer shopId, String productName, String category, List<String> keywords) {
        this.shopId = shopId;
        this.productName = productName;
        if (category == null || category.isEmpty()) {
            this.category = null;
        }
        else {
         this.category = Category.valueOf(category.toUpperCase(Locale.ROOT));
        }
        this.keywords = keywords;
    }

    // Getters and setters

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer newShopId) {
        this.shopId = newShopId;
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

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> newKeywords) {
        this.keywords = newKeywords;
    }
}