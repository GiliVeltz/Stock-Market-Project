package UI.Model.ProductPolicy;

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
    @Override
    public String getRuleString() {
        return "Min Age is " + minAge + " years";
    }
}
