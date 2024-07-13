package Dtos;

import java.util.List;

import Domain.Entities.Policy;
import Domain.Entities.ShoppingBasket;
import Domain.Entities.Rules.Rule;

public class ShopPolicyDto {
    private List<Rule<ShoppingBasketDto>> _rules;

    public ShopPolicyDto(List<Rule<ShoppingBasketDto>> rules) {
        _rules = rules;
    }

    public ShopPolicyDto(Policy<ShoppingBasket> policy) {
        _rules = policy.getShoppingBasketRulesDto();
    }

    public List<Rule<ShoppingBasketDto>> getRules() {
        return _rules;
    }
}
