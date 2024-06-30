package Dtos.Rules;

public class OrRuleDto implements ShoppingBasketRuleDto, UserRuleDto {
    public RuleDto rule1;
    public RuleDto rule2;

    public OrRuleDto(RuleDto rule1, RuleDto rule2) {
        this.rule1 = rule1;
        this.rule2 = rule2;
    }
}
