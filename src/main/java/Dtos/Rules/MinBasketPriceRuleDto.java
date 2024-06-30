package Dtos.Rules;

public class MinBasketPriceRuleDto implements ShoppingBasketRuleDto {
    public double minPrice;

    public MinBasketPriceRuleDto(double minPrice) {
        this.minPrice = minPrice;
    }
}
