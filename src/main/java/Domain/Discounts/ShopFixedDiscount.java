package Domain.Discounts;

import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import Domain.Entities.ShoppingBasket;
import Dtos.BasicDiscountDto;

public class ShopFixedDiscount extends BaseDiscount {
    private double _discountTotal;

    /**
     * Represents a fixed discount for the whole shop.
     */
    public ShopFixedDiscount(Date expirationDate, double discountTotal, int id) {
        super(expirationDate, id);
        if (discountTotal <= 0)
            throw new IllegalArgumentException("Discount must be higher than 0.");
            _discountTotal = discountTotal;

        _rule = (basket) -> true;
        _specialRule = (product) -> true;
    }

    public ShopFixedDiscount(BasicDiscountDto dto) {
        this(new Date(dto.expirationDate.getTime()), dto.discountAmount, dto.id);
    }

    @Override
    public int getParticipatingProduct() {
        return -1;
    }

    /**
     * Applies the fixed discount to the products in the shopping basket.
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
                double new_price = Math.max(entry.getKey() - _discountTotal, 0);
                newpriceToAmount.put(new_price, entry.getValue());
            }
            basket.setProductPriceToAmount(newpriceToAmount, product_id);
        }
    }

    @Override
    public BasicDiscountDto getDto() {
        return new BasicDiscountDto(-1, false, _discountTotal, getExpirationDate(), null, getId());
    }
}

