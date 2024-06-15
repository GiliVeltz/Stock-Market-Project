package Dtos;

import java.util.List;

public class AllItemsRuleDto extends ShoppingBasketRuleDto {
    public List<Integer> productIds;

    public AllItemsRuleDto(List<Integer> productIds) {
        this.productIds = productIds;
    }
}
