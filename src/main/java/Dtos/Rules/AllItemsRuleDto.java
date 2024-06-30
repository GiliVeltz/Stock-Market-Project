package Dtos.Rules;

import java.util.List;

public class AllItemsRuleDto implements ShoppingBasketRuleDto {
    public List<Integer> productIds;

    public AllItemsRuleDto(List<Integer> productIds) {
        this.productIds = productIds;
    }
}
