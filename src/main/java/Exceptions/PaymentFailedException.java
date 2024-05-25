package Exceptions;

public class PaymentFailedException extends StockMarketException {
    // This class represents an error when payment has been failed.
    public PaymentFailedException() {
        super();
    }
    
    public PaymentFailedException(String message) {
        super(message);
    }
}
