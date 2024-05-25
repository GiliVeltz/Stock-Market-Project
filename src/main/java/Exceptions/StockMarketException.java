package Exceptions;

public class StockMarketException extends Exception {
    // This class represents an error in the stock market.
    public StockMarketException() {
        super();
    }

    public StockMarketException(String message) {
        super(message);
    }
}
