package Domain.Discounts;

import java.util.Date;
import java.util.SortedMap;

import Domain.ShoppingBasket;
import Dtos.BasicDiscountDto;

public class ProductFixedDiscount extends BaseDiscount {
    private double _discountTotal;
    private int _productId;

    /**
     * Represents a fixed discount for a specific product.
     */
    public ProductFixedDiscount(Date expirationDate, double discountTotal, int productId) {
        super(expirationDate);
        _discountTotal = discountTotal;
        _productId = productId;

        _rule = (basket) -> basket.getProductCount(productId) > 0;
    }

    public ProductFixedDiscount(BasicDiscountDto dto) {
        this(new Date(dto.expirationDate.getTime()), dto.discountAmount, dto.productId);
    }

    @Override
    public int getParticipatingProduct() {
        return _productId;
    }

    /**
     * Applies the fixed discount to the products in the shopping basket.
     * If the product is not in the basket, the discount is not applied.
     * The discount is applied to the most expensive product in the basket.
     * The price and amount of the product are updated based on the discount in the
     * productToPriceToAmount mapping.
     *
     * @param basket The shopping basket to apply the discount to.
     */
    @Override
    protected void applyDiscountLogic(ShoppingBasket basket) {
        if (!_rule.predicate(basket))
            return;

        SortedMap<Double, Integer> priceToAmount = basket.getProductPriceToAmount(_productId);

        // get most expensive price and amount
        double price = priceToAmount.lastKey();
        int amount = priceToAmount.get(price);

        // calculate discount, and amount of the product at the discounted price
        double postPrice = Math.max(price - _discountTotal, 0.0);
        int postAmount = priceToAmount.getOrDefault(postPrice, 0);

        // update the price to amount mapping
        priceToAmount.put(postPrice, postAmount + 1);
        priceToAmount.put(price, amount - 1);
        if (amount == 1)
            priceToAmount.remove(price);
    }
}
