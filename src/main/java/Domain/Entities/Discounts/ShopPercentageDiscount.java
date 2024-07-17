package Domain.Entities.Discounts;

import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import Domain.Entities.ShoppingBasket;
import Dtos.BasicDiscountDto;
import Exceptions.StockMarketException;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "[shop_percentage_discounts]")
@DiscriminatorValue("Shop Percentage")
public class ShopPercentageDiscount extends BaseDiscount {

    @Column(name = "percentage")
    private double _percentage;

    public ShopPercentageDiscount() {
        super();
    }
    /**
     * Represents a percentage discount for the whole shop.
     */
    public ShopPercentageDiscount(Date expirationDate, double percentage, int id) {
        super(expirationDate);
        if (percentage < 0 || percentage > 100)
            throw new IllegalArgumentException("Precentage must be between 0 and 100");
        _percentage = percentage;

        _rule = (basket) -> true;
        _specialRule = (product) -> true;
        _tempId = id;
    }

    public ShopPercentageDiscount(BasicDiscountDto dto) {
        this(new Date(dto.expirationDate.getTime()), dto.discountAmount, dto.id);
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
     * @throws StockMarketException 
     */
    @Override
    protected void applyDiscountLogic(ShoppingBasket basket) throws StockMarketException {
        if (!_rule.predicate(basket))
            return;
            
        for (int product_id : basket.getProductIdsList()) {
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
        return new BasicDiscountDto(-1, true, _percentage, getExpirationDate(), null, getId());
    }

    @Override
    public Integer getDiscountId() {
        return getId();
    }
}
