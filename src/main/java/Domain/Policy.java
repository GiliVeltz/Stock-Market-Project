package Domain;

import java.util.ArrayList;
import java.util.List;

import Domain.Rules.Rule;

/**
 * The base class for policies.
 * A policy is a set of rules that can be evaluated against a given context.
 *
 * @param <T> the type of the rules that define the policy
 */
public abstract class Policy<T> {
    private List<Rule<T>> _rules;

    /**
     * Constructs a new Policy object.
     * Initializes an empty list of rules.
     */
    public Policy() {
        _rules = new ArrayList<>();
    }

    /**
     * Adds a rule to the policy.
     *
     * @param rule the rule to be added
     */
    public void addRule(Rule<T> rule) {
        if(!_rules.contains(rule))
            _rules.add(rule);
    }

     /**
     * Removes a rule from the policy.
     *
     * @param rule the rule to remove
     */
    public void deleteRule(Rule<T> rule) {
        if(_rules.contains(rule))
            _rules.remove(rule);
    }

    /**
     * Evaluates the policy against the given context.
     * The policy is evaluated by checking each rule against the context.
     * If any rule returns false, the policy evaluation fails.
     *
     * @param context the context on which the policy is evaluated
     * @return true if the policy evaluation succeeds, false otherwise
     */
    public boolean evaluate(T context) {
        for (Rule<T> rule : _rules)
            if (!rule.predicate(context))
                return false;
        return true;
    }
    
    // Getters
    public List<Rule<T>> getRules() {
        return _rules;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Policy{").append("\n");
        for (Rule<T> rule : _rules)
            sb.append(rule.toString()).append(",\n ");
        sb.append('}');
        return sb.toString();
    }
}
