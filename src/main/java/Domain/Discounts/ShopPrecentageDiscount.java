package Domain.Discounts;

import java.util.Date;
import java.util.SortedMap;

import Domain.ShoppingBasket;
import Dtos.BasicDiscountDto;

public class ShopPrecentageDiscount extends BaseDiscount {
    private double _precentage;

    /**
     * Represents a percentage discount for the whole shop.
     */
    public ShopPrecentageDiscount(Date expirationDate, double precentage) {
        super(expirationDate);
        if (precentage < 0 || precentage > 100)
            throw new IllegalArgumentException("Precentage must be between 0 and 100");
        _precentage = precentage;

        _rule = (basket) -> true;
    }

    public ShopPrecentageDiscount(BasicDiscountDto dto) {
        this(new Date(dto.expirationDate.getTime()), dto.discountAmount);
    }

    @Override
    public int getParticipatingProduct() {
        return -1;
    }

    /**
     * Applies the percentage discount to the products in the shopping basket.
     * The price and amount of the product are updated based on the discount in the
     * productToPriceToAmount mapping.
     *
     * @param basket The shopping basket to apply the discount to.
     */
    @Override
    protected void applyDiscountLogic(ShoppingBasket basket) {
        if (!_rule.predicate(basket))
            return;
        for (int product_id : basket.getProductIdList()) {
            SortedMap<Double, Integer> priceToAmount = basket.getProductPriceToAmount(product_id);
            for (double price : priceToAmount.keySet()) {
                int amount = priceToAmount.get(price);
            // calculate discount, and amount of the product at the discounted price
            double discount = price * _precentage / 100;
            int postAmount = priceToAmount.getOrDefault(price - discount, 0);

            // update the price to amount mapping
            priceToAmount.put(price - discount, postAmount + 1);
            priceToAmount.put(price, amount - 1);
            if (amount == 1)
                priceToAmount.remove(price);
        }
        

        
    }
}
