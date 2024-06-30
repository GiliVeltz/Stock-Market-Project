package Domain.Rules;

public class OrRule<T> implements Rule<T> {
    Rule<T> _rule1;
    Rule<T> _rule2;

    public OrRule(Rule<T> rule1, Rule<T> rule2) {
        _rule1 = rule1;
        _rule2 = rule2;
    }

    @Override
    public boolean predicate(T obj) {
        return _rule1.predicate(obj) || _rule2.predicate(obj);
    }
}
