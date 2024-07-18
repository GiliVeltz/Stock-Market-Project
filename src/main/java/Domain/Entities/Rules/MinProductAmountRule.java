package Domain.Entities.Rules;

import Domain.Entities.ShoppingBasket;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Represents a rule that checks if a shopping basket contains a minimum amount of a specific product.
 */
@Entity
@Table(name = "[min_product_amount_rule]")
public class MinProductAmountRule  extends AbstractRule<ShoppingBasket> {
    
    @Column(name = "productId", nullable = false)
    int _productId;

    @Column(name = "minAmount", nullable = false)
    int _minAmount;

    public MinProductAmountRule() {
        _productId = -1;
        _minAmount = -1;
    }
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

    public int getProductId() {
        return _productId;
    }

    public int getMinAmount() {
        return _minAmount;
    }
}
