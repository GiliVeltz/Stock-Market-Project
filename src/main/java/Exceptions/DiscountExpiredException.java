package Exceptions;

public class DiscountExpiredException extends StockMarketException {
    public DiscountExpiredException() {
        super();
    }

    public DiscountExpiredException(String message) {
        super(message);
    }
}
