package Domain.Rules;

import Domain.User;

public class MinAgeRule implements Rule<User> {
    int _minAge;

    public MinAgeRule(int minAge) {
        _minAge = minAge;
    }

    @Override
    public boolean predicate(User user) {
        return user.getAge() >= _minAge;
    }
}
