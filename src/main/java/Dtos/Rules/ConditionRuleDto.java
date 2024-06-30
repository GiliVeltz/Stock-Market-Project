package Dtos.Rules;

public class ConditionRuleDto implements ShoppingBasketRuleDto, UserRuleDto {
    public RuleDto conditionRule;
    public RuleDto thenRule;

    public ConditionRuleDto(RuleDto conditionRule, RuleDto thenRule) {
        this.conditionRule = conditionRule;
        this.thenRule = thenRule;
    }
}
