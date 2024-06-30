package Dtos.Rules;

public class MinAgeRuleDto extends UserRuleDto {
    public int minAge;

    public MinAgeRuleDto(int minAge) {
        this.minAge = minAge;
    }
}
