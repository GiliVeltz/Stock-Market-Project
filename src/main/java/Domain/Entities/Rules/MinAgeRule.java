package Domain.Entities.Rules;

import Domain.Entities.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Represents a rule that checks if a user's age is greater than or equal to a minimum age.
 */
@Entity
@Table(name = "[min_age_rule]")
public class MinAgeRule  extends AbstractRule<User> {
    
    @Column(name = "minAge", nullable = false)
    int _minAge;

    public MinAgeRule() {
        _minAge = -1;
    }
    /**
     * Constructs a new MinAgeRule with the specified minimum age.
     * 
     * @param minAge the minimum age required for the rule to pass
     */
    public MinAgeRule(int minAge) {
        _minAge = minAge;
    }

    public int getMinAge() {
        return _minAge;
    }

    @Override
    public boolean predicate(User user) {
        return user.getAge() >= _minAge;
    }
}
