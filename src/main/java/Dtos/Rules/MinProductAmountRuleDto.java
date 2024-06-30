package Dtos.Rules;

public class MinProductAmountRuleDto extends ShoppingBasketRuleDto {
    int productId;
    int minAmount;

    public MinProductAmountRuleDto(int productId, int minAmount) {
        this.productId = productId;
        this.minAmount = minAmount;
    }
}
