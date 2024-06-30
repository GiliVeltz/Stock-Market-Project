package Domain.Rules;

public class TimeRangeInMonthRule<T> implements Rule<T> {
    int _startDay;
    int _endDay;

    public TimeRangeInMonthRule(int startDay, int endDay) {
        _startDay = startDay;
        _endDay = endDay;
    }

    @Override
    public boolean predicate(T ignored) {
        // get the current day of the month
        int day = java.time.LocalDate.now().getDayOfMonth();
        return day >= _startDay && day <= _endDay;
    }
}
