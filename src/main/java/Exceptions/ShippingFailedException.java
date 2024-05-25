package Exceptions;

public class ShippingFailedException extends StockMarketException {
    // This class represents an error when shipping has been failed.
    public ShippingFailedException() {
        super();
    }

    public ShippingFailedException(String message) {
        super(message);
    }
}
