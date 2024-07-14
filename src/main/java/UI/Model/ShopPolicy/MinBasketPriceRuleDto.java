package UI.Model.ShopPolicy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("MinBasketPriceRuleDto")
public class MinBasketPriceRuleDto implements ShoppingBasketRuleDto {
    public double minPrice;

    public MinBasketPriceRuleDto() {
        minPrice = -1;
    }

    public MinBasketPriceRuleDto(double minPrice) {
        this.minPrice = minPrice;
    }

    public double getMinPrice() {
        return minPrice;
    }

    @JsonIgnore
    @Override
    public String getRuleString() {
        return "Min Basket Price is " + minPrice;
    }

    @Override
    public ShoppingBasketRuleDto createCopy(ShoppingBasketRuleDto toCopy) {
        return new MinBasketPriceRuleDto(getMinPrice());
    }

}
