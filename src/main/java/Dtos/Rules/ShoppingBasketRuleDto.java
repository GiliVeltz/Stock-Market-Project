package Dtos.Rules;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(name = "MinProductAmountRuleDto", value = MinProductAmountRuleDto.class),
    @JsonSubTypes.Type(name = "MinBasketPriceRuleDto", value = MinBasketPriceRuleDto.class)
})
public interface ShoppingBasketRuleDto extends RuleDto {
    
}
