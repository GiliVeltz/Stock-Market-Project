package Domain;
import java.util.concurrent.atomic.AtomicInteger;

public class IDGenerator {
    // A single instance of the class
    private static IDGenerator instance;
    
    // Atomic integer to ensure thread-safe unique IDs
    private AtomicInteger counter;

    // Private constructor to prevent instantiation
    private IDGenerator() {
        counter = new AtomicInteger(0);
    }

    // Method to get the single instance of the class
    public static synchronized IDGenerator getInstance() {
        if (instance == null) {
            instance = new IDGenerator();
        }
        return instance;
    }

    // Method to get the next unique ID
    public int getNextID() {
        return counter.incrementAndGet();
    }
}