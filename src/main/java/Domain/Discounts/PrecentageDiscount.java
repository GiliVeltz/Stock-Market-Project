package Domain.Discounts;

import java.sql.Date;
import java.util.SortedMap;

import Domain.ShoppingBasket;
import Domain.Rules.Rule;

public class PrecentageDiscount extends BaseDiscount {
    private double _precentage;
    private Rule<ShoppingBasket> _rule;
    private int _productId;

    /**
     * Represents a percentage discount for a specific product.
     */
    public PrecentageDiscount(java.util.Date expirationDate, double precentage, int productId) {
        super(expirationDate);
        if (precentage < 0 || precentage > 100)
            throw new IllegalArgumentException("Precentage must be between 0 and 100");
        _precentage = precentage;
        _productId = productId;

        _rule = (basket) -> basket.getProductCount(productId) > 0;
    }

    /**
     * Applies the percentage discount to the products in the shopping basket.
     * If the product is not in the basket, the discount is not applied.
     * The discount is applied to the most expensive product in the basket.
     * The price and amount of the product are updated based on the discount in the
     * productToPriceToAmount mapping.
     *
     * @param basket The shopping basket to apply the discount to.
     */
    @Override
    public void applyDiscountLogic(ShoppingBasket basket) {
        if (!_rule.predicate(basket))
            return;

        SortedMap<Double, Integer> priceToAmount = basket.productToPriceToAmount.get(_productId);

        // get most expensive price and amount
        double price = priceToAmount.firstKey();
        int amount = priceToAmount.get(price);

        // calculate discount, and amount of the product at the discounted price
        double discount = price * _precentage / 100;
        int postAmount = priceToAmount.get(price - discount);

        // update the price to amount mapping
        priceToAmount.put(price - discount, postAmount + 1);
        priceToAmount.put(price, amount - 1);
    }
}
