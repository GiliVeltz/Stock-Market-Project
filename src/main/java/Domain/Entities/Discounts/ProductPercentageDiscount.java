package Domain.Entities.Discounts;

import java.util.Date;
import java.util.SortedMap;

import Domain.Entities.ShoppingBasket;
import Dtos.BasicDiscountDto;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "[product_percentage_discounts]")
@DiscriminatorValue("Product Percentage")
public class ProductPercentageDiscount extends BaseDiscount {

    @Column(name = "percentage")
    private double _percentage;

    @Column(name = "product_id")
    private int _productId;


    public ProductPercentageDiscount() {
        super();
    }
    /**
     * Represents a percentage discount for a specific product.
     */
    public ProductPercentageDiscount(Date expirationDate, double percentage, int productId, int id) {
        super(expirationDate);
        if (percentage < 0 || percentage > 100)
            throw new IllegalArgumentException("Precentage must be between 0 and 100");
        _percentage = percentage;
        _productId = productId;

        _rule = (basket) -> basket.getProductCount(productId) > 0;
        _tempId = id;
    }

    public ProductPercentageDiscount(BasicDiscountDto dto) {
        this(new Date(dto.expirationDate.getTime()), dto.discountAmount, dto.productId, dto.id);
    }

    @Override
    public int getParticipatingProduct() {
        return _productId;
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
    protected void applyDiscountLogic(ShoppingBasket basket) {
        if (!_rule.predicate(basket))
            return;

        SortedMap<Double, Integer> priceToAmount = basket.getProductPriceToAmount(_productId);

        // get most expensive price and amount
        double price = priceToAmount.lastKey();
        int amount = priceToAmount.get(price);

        // calculate discount, and amount of the product at the discounted price
        double discount = price * _percentage / 100;
        int postAmount = priceToAmount.getOrDefault(price - discount, 0);

        // update the price to amount mapping
        priceToAmount.put(price - discount, postAmount + 1);
        priceToAmount.put(price, amount - 1);
        if (amount == 1)
            priceToAmount.remove(price);
    }

    @Override
    public BasicDiscountDto getDto() {
        return new BasicDiscountDto(_productId, true, _percentage, getExpirationDate(), null, getId());
    }

    @Override
    public Integer getDiscountId() {
        return getId();
    }

    @PostLoad
    public void setRule(){
        _rule = (basket) -> basket.getProductCount(_productId) > 0;
    }
}
