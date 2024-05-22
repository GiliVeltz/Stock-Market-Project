package Domain.Exceptions;

public class ProductOutOfStockExepction extends RuntimeException {
    
    public ProductOutOfStockExepction() {
        super();
    }
    
    public ProductOutOfStockExepction(String message) {
        super(message);
    }
}