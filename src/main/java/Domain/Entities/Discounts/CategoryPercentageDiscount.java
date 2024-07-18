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
@Table(name = "[category_percentage_discounts]")
@DiscriminatorValue("Category Percentage")
public class CategoryPercentageDiscount extends BaseDiscount {

    @Column(name = "percentage")
    private double _percentage;

    @Column(name = "category")
    private Category _category;

    public CategoryPercentageDiscount() {
        super();
    }

    /**
     * Represents a percentage discount for the a specific category.
     */
    public CategoryPercentageDiscount(Date expirationDate, double percentage, Category category, int id) {
        super(expirationDate);
        if (percentage < 0 || percentage > 100)
            throw new IllegalArgumentException("Precentage must be between 0 and 100");
        _percentage = percentage;
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

    public CategoryPercentageDiscount(BasicDiscountDto dto) {
        this(new Date(dto.expirationDate.getTime()), dto.discountAmount, dto.category, dto.id);
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
        for (Product product : new HashSet<>(basket.getProductsList())) {
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
        return new BasicDiscountDto(-1, true, _percentage, getExpirationDate(), _category, getId());
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