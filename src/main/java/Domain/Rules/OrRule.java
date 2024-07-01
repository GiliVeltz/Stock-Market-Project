package Domain.Rules;

/**
 * Represents a rule that combines two rules using the logical OR operator.
 * The combined rule will evaluate to true if either of the two rules evaluates to true.
 *
 * @param <T> the type of object that the rule operates on
 */
public class OrRule<T> implements Rule<T> {
    
    Rule<T> _rule1;
    Rule<T> _rule2;

    /**
     * Constructs a new OrRule with the specified rules.
     *
     * @param rule1 the first rule to be combined
     * @param rule2 the second rule to be combined
     */
    public OrRule(Rule<T> rule1, Rule<T> rule2) {
        _rule1 = rule1;
        _rule2 = rule2;
    }

    /**
     * Evaluates the combined rule on the given object.
     *
     * @param obj the object to be evaluated
     * @return true if either of the two rules evaluates to true, false otherwise
     */
    @Override
    public boolean predicate(T obj) {
        return _rule1.predicate(obj) || _rule2.predicate(obj);
    }
}
