package Domain.Entities.enums;
import jakarta.persistence.Entity;

//TODO >> remove to service shop
public enum Category {
    ELECTRONICS,
    BOOKS,
    CLOTHING,
    HOME,
    KITCHEN,
    SPORTS,
    GROCERY,
    PHARMACY,
    // Add more categories as needed

    DEFAULT_VAL //should be last!
}