package UI.Model.ProductPolicy;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @Override
    public String getRuleString() {
        return "Min Age is " + minAge + " years";
    }
    
    @JsonIgnore
    @Override
    public UserRuleDto createCopy(UserRuleDto toCopy) {
        return new MinAgeRuleDto(getMinAge());
    }
}
