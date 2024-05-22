package Domain;

import java.util.List;

import Domain.Exceptions.ProductOutOfStockExepction;

public class Product {
    private Integer _productId;
    private String _productName;
    private double _price;
    private Integer _quantity;
    //TODO: private List<discount> 

    // Constructor
    public Product(Integer productId, String productName, double price) {
        this._productId = productId;
        this._productName = productName;
        this._price = price;
        this._quantity = 0;
    }

    public int getProductId() {
        return _productId;
    }

    public String getProductName() {
        return _productName;
    }

    public double getPrice() {
        return _price;
    }

    public void purchaseProduct() {
        if (_quantity == 0) {
            throw new ProductOutOfStockExepction("Product is out of stock");
        }
        _quantity--;
    }

    public void cancelPurchase() {
        _quantity++;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + _productId +
                ", name='" + _productName + '\'' +
                ", price=" + _price +
                '}';
    }
}
