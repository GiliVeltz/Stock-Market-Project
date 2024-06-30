package Domain.Rules;

import Domain.ShoppingBasket;
import Dtos.Rules.AllItemsRuleDto;
import Dtos.Rules.MinBasketPriceRuleDto;
import Dtos.Rules.MinProductAmountRuleDto;
import Dtos.Rules.ShoppingBasketRuleDto;

public class RuleFactory {
    public static Rule<ShoppingBasket> createShoppingBasketRule(ShoppingBasketRuleDto dto) {
        if (dto instanceof MinBasketPriceRuleDto)
            return createShoppingBasketRule((MinBasketPriceRuleDto) dto);
        if (dto instanceof AllItemsRuleDto)
            return createShoppingBasketRule((AllItemsRuleDto) dto);
        if (dto instanceof MinProductAmountRuleDto)
            return createShoppingBasketRule((MinProductAmountRuleDto) dto);
        
        throw new IllegalArgumentException("Unknown rule type");
    }

    private static Rule<ShoppingBasket> createShoppingBasketRule(MinBasketPriceRuleDto dto) {
        return new MinBasketPriceRule(dto.minPrice);
    }

    private static Rule<ShoppingBasket> createShoppingBasketRule(AllItemsRuleDto dto) {
        return new AllItemsRule(dto.productIds);
    }

    private static Rule<ShoppingBasket> createShoppingBasketRule(MinProductAmountRuleDto dto) {
        return new MinProductAmountRule(dto.productId, dto.minAmount);
    }
}
