package Domain.Rules;

import Domain.ShoppingBasket;

public class MinProductAmountRule implements Rule<ShoppingBasket> {
    int _productId;
    int _minAmount;

    public MinProductAmountRule(int productId, int minAmount) {
        _productId = productId;
        _minAmount = minAmount;
    }

    @Override
    public boolean predicate(ShoppingBasket basket) {
        return basket.getProductCount(_productId) >= _minAmount;
    }
}
