package Domain.Entities.Discounts;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import Domain.Entities.Product;
import Domain.Entities.ShoppingBasket;
import Domain.Entities.enums.Category;
import Dtos.BasicDiscountDto;
import Exceptions.StockMarketException;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;

@Entity
@Table(name = "[category_fixed_discounts]")
@DiscriminatorValue("Category Fixed")
public class CategoryFixedDiscount extends BaseDiscount {

    @Column(name = "discount_total")
    private double _discountTotal;

    @Column(name = "category")
    private Category _category;

    public CategoryFixedDiscount() {
        super();
    }

    /**
     * Represents a fixed discount for the a specific category.
     */
    public CategoryFixedDiscount(Date expirationDate, double discountTotal, Category category, int id) {
        super(expirationDate);
        if (discountTotal <= 0)
            throw new IllegalArgumentException("Discount must be higher than 0.");
            _discountTotal = discountTotal;
        _category = category;
        _rule = (basket) -> {
            try {
                return basket.getProductsList().stream().anyMatch((product) -> product.getCategory().equals(_category));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        };
        _specialRule = (product) -> product.getCategory().equals(_category);
        _tempId = id;
    }

    public CategoryFixedDiscount(BasicDiscountDto dto) {
        this(new Date(dto.expirationDate.getTime()), dto.discountAmount, dto.category, dto.id);
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
        for (Product product : new HashSet<>(basket.getProductsList())) {
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
        return new BasicDiscountDto(-1, false, _discountTotal, getExpirationDate(), _category, getId());
    }

    @Override
    public Integer getDiscountId() {
        return getId();
    }

    @PostLoad
    public void setRules(){
        _rule = (basket) -> {
            try {
                return basket.getProductsList().stream().anyMatch((product) -> product.getCategory().equals(_category));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        };
        _specialRule = (product) -> product.getCategory().equals(_category);
    }

}
