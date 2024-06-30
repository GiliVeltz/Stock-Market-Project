package Dtos.Rules;

public class MinAgeRuleDto implements UserRuleDto {
    public int minAge;

    public MinAgeRuleDto(int minAge) {
        this.minAge = minAge;
    }
}
