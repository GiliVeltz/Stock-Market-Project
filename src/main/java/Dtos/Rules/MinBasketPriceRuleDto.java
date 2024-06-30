package Dtos.Rules;

public class MinBasketPriceRuleDto extends ShoppingBasketRuleDto {
    public double minPrice;

    public MinBasketPriceRuleDto(double minPrice) {
        this.minPrice = minPrice;
    }
}
