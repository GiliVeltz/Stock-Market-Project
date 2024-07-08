package UI.Model.ShopPolicy;

import UI.Model.RuleDto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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
    public ShoppingBasketRuleDto createCopy(ShoppingBasketRuleDto toCopy);
}
