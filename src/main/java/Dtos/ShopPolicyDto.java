package Dtos;

import java.util.List;

import Domain.Policy;
import Domain.ShoppingBasket;
import Domain.Rules.Rule;

public class ShopPolicyDto {
    private List<Rule<ShoppingBasketDto>> _rules;

    public ShopPolicyDto(List<Rule<ShoppingBasketDto>> rules) {
        _rules = rules;
    }

    public ShopPolicyDto(Policy<ShoppingBasket> policy) {
        _rules = policy.getRulesDto();
    }

    public List<Rule<ShoppingBasketDto>> getRules() {
        return _rules;
    }
}
