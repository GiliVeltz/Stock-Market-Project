package Domain.Exceptions;

public class ItemOutOfStockExepction extends RuntimeException {
    
    public ItemOutOfStockExepction() {
        super();
    }
    public ItemOutOfStockExepction(String message) {
        super(message);
    }
}