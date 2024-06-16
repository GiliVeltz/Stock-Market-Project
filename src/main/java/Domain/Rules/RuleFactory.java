package Domain.Rules;

import Domain.ShoppingBasket;
import Dtos.AllItemsRuleDto;
import Dtos.MinBasketPriceRuleDto;
import Dtos.ShoppingBasketRuleDto;

public class RuleFactory {
    public static Rule<ShoppingBasket> createShoppingBasketRule(ShoppingBasketRuleDto dto) {
        if (dto instanceof MinBasketPriceRuleDto)
            return new MinBasketPriceRule(((MinBasketPriceRuleDto) dto).minPrice);
        if (dto instanceof AllItemsRuleDto)
            return new AllItemsRule(((AllItemsRuleDto) dto).productIds);
        throw new IllegalArgumentException("Unknown rule type");
    }
}
