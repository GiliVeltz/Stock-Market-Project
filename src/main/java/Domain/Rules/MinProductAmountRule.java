package Domain.Rules;

import Domain.ShoppingBasket;

/**
 * Represents a rule that checks if a shopping basket contains a minimum amount of a specific product.
 */
public class MinProductAmountRule implements Rule<ShoppingBasket> {
    
    int _productId;
    int _minAmount;

    /**
     * Constructs a new MinProductAmountRule with the specified product ID and minimum amount.
     * 
     * @param productId the ID of the product to check
     * @param minAmount the minimum amount of the product required in the shopping basket
     */
    public MinProductAmountRule(int productId, int minAmount) {
        _productId = productId;
        _minAmount = minAmount;
    }

    @Override
    public boolean predicate(ShoppingBasket basket) {
        return basket.getProductCount(_productId) >= _minAmount;
    }
}
