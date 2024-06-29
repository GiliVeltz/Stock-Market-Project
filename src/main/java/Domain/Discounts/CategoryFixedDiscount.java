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

public class CategoryFixedDiscount extends BaseDiscount {
    private double _discountTotal;
    private Category _category;

    /**
     * Represents a fixed discount for the a specific category.
     */
    public CategoryFixedDiscount(Date expirationDate, double discountTotal, Category category) {
        super(expirationDate);
        if (discountTotal <= 0)
            throw new IllegalArgumentException("Discount must be higher than 0.");
            _discountTotal = discountTotal;
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

    public CategoryFixedDiscount(BasicDiscountDto dto) {
        this(new Date(dto.expirationDate.getTime()), dto.discountAmount, dto.category);
    }

    @Override
    public int getParticipatingProduct() {
        return -1;
    }

    /**
     * Applies the fixed discount to the products of the specific in the shopping basket.
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
                double new_price = Math.max(entry.getKey() - _discountTotal, 0);
                newpriceToAmount.put(new_price, entry.getValue());
            }
            basket.setProductPriceToAmount(newpriceToAmount, product_id);
        }
    }

    @Override
    public BasicDiscountDto getDto() {
        return new BasicDiscountDto(-1, false, _discountTotal, getExpirationDate(), _category);
    }
}
