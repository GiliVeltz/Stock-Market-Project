package Dtos.Rules;

public class AndRuleDto implements GenericRuleDto {
    public RuleDto rule1;
    public RuleDto rule2;

    public AndRuleDto(RuleDto rule1, RuleDto rule2) {
        this.rule1 = rule1;
        this.rule2 = rule2;
    }
}
