package Domain.Rules;

public class ConditionRule<T> implements Rule<T> {
    Rule<T> _condRule;
    Rule<T> _followupRule;

    public ConditionRule(Rule<T> rule1, Rule<T> rule2) {
        _condRule = rule1;
        _followupRule = rule2;
    }

    @Override
    public boolean predicate(T obj) {
        if (_condRule.predicate(obj)) {
            return _followupRule.predicate(obj);
        } else {
            return true;
        }
    }
}
