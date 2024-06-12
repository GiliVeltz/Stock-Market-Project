package Dtos;

import Domain.Product;
import enums.Category;

public class ProductDto {

    public String _productName;
    public Category _category;
    public double _price;

    // Constructor
    public ProductDto(String productName, Category category, double price) {
        this._productName = productName;
        this._category = category;
        this._price = price;
    }

    public ProductDto(Product product) {
        this._productName = product.getProductName();
        this._category = product.getCategory();
        this._price = product.getPrice();
    }

}
