package UI.Model;

import java.util.List;

public class ProductPolicyDto {
    private List<Rule<UserDto>> _rules;

    public ProductPolicyDto(List<Rule<UserDto>> rules) {
        _rules = rules;
    }

    public List<Rule<UserDto>> getRules() {
        return _rules;
    }
}
