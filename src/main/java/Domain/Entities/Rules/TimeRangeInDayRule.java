package Domain.Entities.Rules;

import java.util.Calendar;

/**
 * Represents a rule that checks if the current time falls within a specified time range in a day.
 * @param <T> the type of object - ignored by the rule
 */
public class TimeRangeInDayRule<T> extends AbstractRule<T> {
    
    int _startHours, _startMinutes;
    int _endHours, _endMinutes;

    /**
     * Constructs a TimeRangeInDayRule object with the specified start and end time.
     * @param startHours the starting hour of the time range
     * @param startMinutes the starting minute of the time range
     * @param endHours the ending hour of the time range
     * @param endMinutes the ending minute of the time range
     */
    public TimeRangeInDayRule(int startHours, int startMinutes, int endHours, int endMinutes) {
        _startHours = startHours;
        _startMinutes = startMinutes;
        _endHours = endHours;
        _endMinutes = endMinutes;
    }

    @Override
    public boolean predicate(T ignored) {
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        if (hours > _startHours && hours < _endHours) {
            return true;
        } else if (hours == _startHours && minutes >= _startMinutes) {
            return true;
        } else if (hours == _endHours && minutes <= _endMinutes) {
            return true;
        }
        return false;
    }
}
