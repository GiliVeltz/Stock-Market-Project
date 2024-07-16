package Domain.Entities.Discounts;

import java.util.List;

import Domain.Entities.ShoppingBasket;
import Dtos.BasicDiscountDto;
import Dtos.ConditionalDiscountDto;
import Exceptions.StockMarketException;

/**
 * Represents a conditional discount that applies a base discount to a shopping
 * basket
 * if certain products are present in the basket.
 */
public class ConditionalDiscount extends Discount {
    private BaseDiscount _discount;

    /**
     * Constructs a new ConditionalDiscount object with the specified must-have
     * products
     * and base discount.
     * 
     * @param mustHaveProducts the list of product IDs that must be present in the
     *                         basket
     * @param discount         the base discount to apply if the must-have products
     *                         are present
     */
    public ConditionalDiscount(List<Integer> mustHaveProducts, BaseDiscount discount) {
        super(discount.getExpirationDate());
        _discount = discount;
        _rule = (basket) -> mustHaveProducts.stream().allMatch((productId) -> basket.getProductCount(productId) > 0);
    }

    public ConditionalDiscount(ConditionalDiscountDto dto) {
        this(dto.mustHaveProducts, dto.isPrecentage ? new ProductPercentageDiscount(dto) : new ProductFixedDiscount(dto));
    }

    /**
     * Returns the product ID that participates in the discount.
     * 
     * @return the product ID that participates in the discount
     */
    @Override
    public int getParticipatingProduct() {
        return _discount.getParticipatingProduct();
    }

    /**
     * Applies the discount to the given shopping basket if the rule is satisfied.
     * 
     * @param basket the shopping basket to apply the discount to
     * @throws StockMarketException if the discount has expired
     */
    @Override
    protected void applyDiscountLogic(ShoppingBasket basket) throws StockMarketException {
        if (_rule.predicate(basket))
            _discount.applyDiscount(basket);
    }

    @Override
    public BasicDiscountDto getDto() {
        return _discount.getDto();
    }

    @Override
    public int getDiscountId() {
        return _discount.getId();
    }

}
