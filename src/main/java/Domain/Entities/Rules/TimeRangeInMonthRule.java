package Domain.Entities.Rules;

/**
 * A rule that checks if the current day of the month falls within a specified time range.
 *
 * @param <T> the type of object to be ignored by the rule
 */
public class TimeRangeInMonthRule<T> extends AbstractRule<T> {
    
    int _startDay;
    int _endDay;

    /**
     * Constructs a TimeRangeInMonthRule with the specified start and end days of the month.
     *
     * @param startDay the start day of the month (inclusive)
     * @param endDay the end day of the month (inclusive)
     */
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
