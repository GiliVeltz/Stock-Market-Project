package Dtos;

import java.util.List;

import Domain.Entities.Policies.Policy;
import Domain.Entities.Policies.ProductPolicy;
import Domain.Entities.Rules.AbstractRule;

public class ProductPolicyDto {
    private List<AbstractRule<UserDto>> _rules;

    public ProductPolicyDto(List<AbstractRule<UserDto>> rules) {
        _rules = rules;
    }

    public ProductPolicyDto(Policy<UserDto> policy) {
        _rules = policy.getUsersRulesDto();
    }

    public ProductPolicyDto(ProductPolicy policy) {
        _rules = policy.getUsersRulesDto();
    }

    public List<AbstractRule<UserDto>> getRules() {
        return _rules;
    }
}
