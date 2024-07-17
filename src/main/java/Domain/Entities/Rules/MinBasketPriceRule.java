package Domain.Entities.Rules;

import Domain.Entities.ShoppingBasket;
import Exceptions.StockMarketException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Represents a rule that checks if the total price of a shopping basket is
 * greater than or equal to a minimum price.
 */
@Entity
@Table(name = "[min_basket_price_rule]")
public class MinBasketPriceRule extends AbstractRule<ShoppingBasket> {

    @Column(name = "minPrice", nullable = false)
    private double _minPrice;

    public MinBasketPriceRule() {
        _minPrice = -1;
    }
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
        try {
            return basket.getShoppingBasketPrice() >= _minPrice;
        } catch (StockMarketException e) {
            return false;
        }
    }

    public double getMinPrice() {
        return _minPrice;
    }
}
