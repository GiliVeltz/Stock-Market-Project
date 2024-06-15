package Dtos;

public class MinBasketPriceRuleDto extends RuleDto {
    public double minPrice;

    public MinBasketPriceRuleDto(double minPrice) {
        this.minPrice = minPrice;
    }
}
