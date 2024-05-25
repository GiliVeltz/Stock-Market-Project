package Domain;

import java.util.logging.Level;
import java.util.logging.Logger;

import Exceptions.ProductOutOfStockExepction;

public class Product implements Cloneable {
    private Integer _productId;
    private String _productName;
    private double _price;
    private Integer _quantity;
    private static final Logger logger = Logger.getLogger(Product.class.getName());
    // TODO: private List<discount>

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

    public void purchaseProduct() throws ProductOutOfStockExepction {
        if (_quantity == 0) {
            logger.log(Level.SEVERE, "Product - purchaseProduct - Product " + _productName + " with id: " + _productId
                    + " out of stock -- thorwing ProductOutOfStockExepction.");
            throw new ProductOutOfStockExepction("Product is out of stock");
        }
        _quantity--;
        logger.log(Level.FINE, "Product - purchaseProduct - Product " + _productName + " with id: " + _productId
                + " had been purchased -- -1 to stock.");
    }

    public void cancelPurchase() {
        _quantity++;
        logger.log(Level.FINE, "Product - cancelPurchase - Product " + _productName + " with id: " + _productId
                + " had been purchased cancel -- +1 to stock.");
    }

        @Override
        public Product clone() {
            try {
                return (Product) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(); // Can't happen as we implement Cloneable
            }
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
