package Dtos.Rules;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("MinAgeRuleDto")
public class MinAgeRuleDto implements UserRuleDto {
    public int minAge;

    public MinAgeRuleDto() {
        minAge = -1;
    }
    public MinAgeRuleDto(int minAge) {
        this.minAge = minAge;
    }

    public int getMinAge() {
        return minAge;
    }
}
