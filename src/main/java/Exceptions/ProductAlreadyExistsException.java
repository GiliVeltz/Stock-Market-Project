package Exceptions;

public class ProductAlreadyExistsException extends StockMarketException {
    public ProductAlreadyExistsException(String message) {
        super(message);
    }
}
