package Domain.Rules;

/**
 * Represents a rule that combines two rules using a logical AND operation.
 * The combined rule will only evaluate to true if both of the underlying rules evaluate to true.
 *
 * @param <T> the type of object that the rule operates on
 */
public class AndRule<T> implements Rule<T> {
    
    Rule<T> _rule1;
    Rule<T> _rule2;

    /**
     * Constructs a new AndRule with the specified rules.
     *
     * @param rule1 the first rule to be combined
     * @param rule2 the second rule to be combined
     */
    public AndRule(Rule<T> rule1, Rule<T> rule2) {
        _rule1 = rule1;
        _rule2 = rule2;
    }

    /**
     * Evaluates the combined rule on the given object.
     * The combined rule will only evaluate to true if both of the underlying rules evaluate to true.
     *
     * @param obj the object to evaluate the rule on
     * @return true if the combined rule evaluates to true, false otherwise
     */
    @Override
    public boolean predicate(T obj) {
        return _rule1.predicate(obj) && _rule2.predicate(obj);
    }
}
