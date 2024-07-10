package Dtos.Rules;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("MinProductAmountRuleDto")
public class MinProductAmountRuleDto implements ShoppingBasketRuleDto {
    public int productId;
    public int minAmount;

    public MinProductAmountRuleDto() {
        productId = -1;
        minAmount = -1;
    }
    
    public MinProductAmountRuleDto(int productId, int minAmount) {
        this.productId = productId;
        this.minAmount = minAmount;
    }

    public int getProductId() {
        return productId;
    }

    public int getMinAmount() {
        return minAmount;
    }
}
