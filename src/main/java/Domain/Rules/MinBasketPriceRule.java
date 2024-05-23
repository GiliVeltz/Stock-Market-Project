package Domain.Rules;

import Domain.ShoppingBasket;

/**
 * Represents a rule that checks if the total price of a shopping basket is
 * greater than or equal to a minimum price.
 */
public class MinBasketPriceRule implements Rule<ShoppingBasket> {

    private double _minPrice;

    /**
     * Initializes a new instance of the MinBasketPriceRule class with the specified
     * minimum price.
     * 
     * @param minPrice The minimum price that the shopping basket must meet.
     */
    public MinBasketPriceRule(double minPrice) {
        _minPrice = minPrice;
    }

    /**
     * Determines whether the given shopping basket meets the minimum price
     * requirement.
     * 
     * @param basket The shopping basket to evaluate.
     * @return true if the shopping basket's total price is greater than or equal to
     *         the minimum price, otherwise false.
     */
    @Override
    public boolean predicate(ShoppingBasket basket) {
        return basket.getShoppingBasketPrice() >= _minPrice;
    }
}
