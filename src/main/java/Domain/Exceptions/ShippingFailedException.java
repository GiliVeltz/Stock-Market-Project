package Domain.Exceptions;

public class ShippingFailedException extends RuntimeException{
    
    public ShippingFailedException() {
        super();
    }

    public ShippingFailedException(String message) {
        super(message);
    }
}
