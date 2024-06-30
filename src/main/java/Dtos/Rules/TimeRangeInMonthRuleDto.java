package Dtos.Rules;

public class TimeRangeInMonthRuleDto implements GenericRuleDto, ShoppingBasketRuleDto, UserRuleDto {
    public int startDay;
    public int endDay;

    public TimeRangeInMonthRuleDto(int startDay, int endDay) {
        this.startDay = startDay;
        this.endDay = endDay;
    }
}
