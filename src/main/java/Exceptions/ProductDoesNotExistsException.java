package Exceptions;

public class ProductDoesNotExistsException extends StockMarketException{
    public ProductDoesNotExistsException(String message) {
        super(message);
    }
}
