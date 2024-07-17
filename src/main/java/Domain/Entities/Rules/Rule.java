package Domain.Entities.Rules;

import jakarta.persistence.Entity;

/**
 * The Rule interface represents a rule that can be checked given a context of
 * type T. It defines a predicate that must be satisfied.
 */
public interface Rule<T> {
    /**
     * Evaluates the rule against the given context.
     *
     * @param context the context object to evaluate the rule against
     * @return true if the rule is satisfied, false otherwise
     */
    boolean predicate(T context);
}
