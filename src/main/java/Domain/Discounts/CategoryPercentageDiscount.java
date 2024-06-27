package Domain.Discounts;

import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import Domain.Product;
import Domain.ShoppingBasket;
import Dtos.BasicDiscountDto;
import Exceptions.StockMarketException;
import enums.Category;

public class CategoryPercentageDiscount extends BaseDiscount {
    private double _percentage;
    private Category _category;

    /**
     * Represents a percentage discount for the a specific category.
     */
    public CategoryPercentageDiscount(Date expirationDate, double percentage, Category category) {
        super(expirationDate);
        if (percentage < 0 || percentage > 100)
            throw new IllegalArgumentException("Precentage must be between 0 and 100");
        _percentage = percentage;
        _category = category;
        _rule = (basket) -> {
            try {
                return basket.getProductsList().stream().anyMatch((product) -> product.getCategory().equals(_category));
            } catch (StockMarketException e) {
                e.printStackTrace();
                return false;
            }
        };
        _specialRule = (product) -> product.getCategory().equals(_category);
    }

    public CategoryPercentageDiscount(BasicDiscountDto dto) {
        this(new Date(dto.expirationDate.getTime()), dto.discountAmount, dto.category);
    }

    @Override
    public int getParticipatingProduct() {
        return -1;
    }
    /**
     * Applies the percentage discount to the products with the specific category in the shopping basket.
     * The price and amount of the product are updated based on the discount.
     *
     * @param basket The shopping basket to apply the discount to.
     * @throws StockMarketException 
     */
    @Override
    protected void applyDiscountLogic(ShoppingBasket basket) throws StockMarketException {
        if (!_rule.predicate(basket))
            return;
        for (Product product : basket.getProductsList()) {
            if(!product.getCategory().equals(_category)){
                continue;
            }
            int product_id = product.getProductId();
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
        return new BasicDiscountDto(-1, true, _percentage, getExpirationDate(), _category);
    }
}