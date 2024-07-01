package Dtos.Rules;

public class MinProductAmountRuleDto implements ShoppingBasketRuleDto {
    public int productId;
    public int minAmount;

    public MinProductAmountRuleDto(int productId, int minAmount) {
        this.productId = productId;
        this.minAmount = minAmount;
    }
}
