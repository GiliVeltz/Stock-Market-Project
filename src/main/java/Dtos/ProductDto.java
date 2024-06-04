package Dtos;

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

}
