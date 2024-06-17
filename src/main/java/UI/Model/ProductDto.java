package UI.Model;

import enums.Category;

public class ProductDto {

    private String _productName;
    private Category _category;
    private double _price;
    private int _productQuantity;

    // Constructor
    public ProductDto(String productName, Category category, double price, int productQuantity) {
        this._productName = productName;
        this._category = category;
        this._price = price;
        this._productQuantity = productQuantity;
    }

    // Getters and setters
    public String getProductName() {
        return _productName;
    }

    public void setProductName(String newProductName) {
        _productName = newProductName;
    }

    public Category getCategory() {
        return _category;
    }

    public void setCategory(Category newCategory) {
        _category = newCategory;
    }

    public double getPrice() {
        return _price;
    }

    public void setPrice(double newPrice) {
        _price = newPrice;
    }

    public int getQuantity() {
        return _productQuantity;
    }

    public void setQuantity(int newQuantity) {
        _productQuantity = newQuantity;
    }

}
