package Dtos;

import java.util.Set;

import Domain.Entities.Product;
import enums.Category;

public class ProductDto {

    public String productName;
    public Category category;
    public double price;
    public int productQuantity;
    public double productRating;
    public Set<String> keywords;

    // Constructor
    public ProductDto(String productName, Category category, double price, int productQuantity) {
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.productQuantity = productQuantity;
    }

    public ProductDto(Product product) {
        this.productName = product.getProductName();
        this.category = product.getCategory();
        this.price = product.getPrice();
        this.productQuantity = product.getProductQuantity();
        this.productRating = product.getProductRating();
        this.keywords = product.getKeywords();
    }
}
