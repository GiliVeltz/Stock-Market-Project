package UI.Model;

import java.util.List;

public class ShopPolicyDto {
    private List<Rule<ShoppingBasketDto>> _rules;

    public ShopPolicyDto(List<Rule<ShoppingBasketDto>> rules) {
        _rules = rules;
    }

    public List<Rule<ShoppingBasketDto>> getRules() {
        return _rules;
    }
}