package Dtos.Rules;

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
}
