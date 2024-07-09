package Dtos;

import java.util.List;

import Domain.Entities.Policy;
import Domain.Entities.Policies.ProductPolicy;
import Domain.Entities.Rules.Rule;

public class ProductPolicyDto {
    private List<Rule<UserDto>> _rules;

    public ProductPolicyDto(List<Rule<UserDto>> rules) {
        _rules = rules;
    }

    public ProductPolicyDto(Policy<UserDto> policy) {
        _rules = policy.getUsersRulesDto();
    }

    public ProductPolicyDto(ProductPolicy policy) {
        _rules = policy.getUsersRulesDto();
    }

    public List<Rule<UserDto>> getRules() {
        return _rules;
    }
}
