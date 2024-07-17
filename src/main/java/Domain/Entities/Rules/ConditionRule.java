package Domain.Entities.Rules;

/**
 * Represents a condition rule that combines two rules.
 * If the first rule evaluates to true, the second rule is evaluated.
 * If the first rule evaluates to false, the condition is considered true.
 * (logical implication)
 *
 * @param <T> the type of object the rule operates on
 */
public class ConditionRule<T>  extends AbstractRule<T> {
    
    Rule<T> _condRule;
    Rule<T> _followupRule;

    /**
     * Constructs a new ConditionRule with the specified rules.
     *
     * @param rule1 the first rule to evaluate
     * @param rule2 the follow-up rule to evaluate if the first rule is true
     */
    public ConditionRule(Rule<T> rule1, Rule<T> rule2) {
        _condRule = rule1;
        _followupRule = rule2;
    }

    /**
     * Evaluates the condition rule for the given object.
     * If the first rule evaluates to true, the second rule is evaluated.
     * If the first rule evaluates to false, the condition is considered true.
     *
     * @param obj the object to evaluate the rule on
     * @return true if the condition is met, false otherwise
     */
    @Override
    public boolean predicate(T obj) {
        if (_condRule.predicate(obj)) {
            return _followupRule.predicate(obj);
        } else {
            return true;
        }
    }
}
