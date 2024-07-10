package Domain.Rules;

import Domain.Entities.User;

/**
 * Represents a rule that checks if a user's age is greater than or equal to a minimum age.
 */
public class MinAgeRule implements Rule<User> {
    
    int _minAge;

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
