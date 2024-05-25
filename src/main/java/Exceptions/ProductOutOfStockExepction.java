package Exceptions;

public class ProductOutOfStockExepction extends StockMarketException {
    // This class represents an error when a product is out of stock.
    
    public ProductOutOfStockExepction() {
        super();
    }
    
    public ProductOutOfStockExepction(String message) {
        super(message);
    }
}