package UI.Model.ProductPolicy;

import UI.Model.RuleDto;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(name = "MinAgeRuleDto", value = MinAgeRuleDto.class),
})
public interface UserRuleDto extends RuleDto {
    public UserRuleDto createCopy(UserRuleDto toCopy);
}
