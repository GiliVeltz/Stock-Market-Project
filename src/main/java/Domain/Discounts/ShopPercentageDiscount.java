package Domain.Discounts;

import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import Domain.ShoppingBasket;
import Dtos.BasicDiscountDto;

public class ShopPercentageDiscount extends BaseDiscount {
    private double _percentage;

    /**
     * Represents a percentage discount for the whole shop.
     */
    public ShopPercentageDiscount(Date expirationDate, double percentage) {
        super(expirationDate);
        if (percentage < 0 || percentage > 100)
            throw new IllegalArgumentException("Precentage must be between 0 and 100");
        _percentage = percentage;

        _rule = (basket) -> true;
        _specialRule = (product) -> true;
    }

    public ShopPercentageDiscount(BasicDiscountDto dto) {
        this(new Date(dto.expirationDate.getTime()), dto.discountAmount);
    }

    @Override
    public int getParticipatingProduct() {
        return -1;
    }


    /**
     * Applies the percentage discount to the products in the shopping basket.
     * The price and amount of the product are updated based on the discount.
     *
     * @param basket The shopping basket to apply the discount to.
     */
    @Override
    protected void applyDiscountLogic(ShoppingBasket basket) {
        if (!_rule.predicate(basket))
            return;
        for (int product_id : basket.getProductIdList()) {
            SortedMap<Double, Integer> newpriceToAmount = new TreeMap<>();
            SortedMap<Double, Integer> priceToAmount = basket.getProductPriceToAmount(product_id);
            for (Map.Entry<Double, Integer> entry : priceToAmount.entrySet()) {
                double new_price = entry.getKey() * _percentage / 100;
                newpriceToAmount.put(new_price, entry.getValue());
            }
            basket.setProductPriceToAmount(newpriceToAmount, product_id);
        }
    }

    @Override
    public BasicDiscountDto getDto() {
        return new BasicDiscountDto(-1, true, _percentage, getExpirationDate(), null);
    }
}
