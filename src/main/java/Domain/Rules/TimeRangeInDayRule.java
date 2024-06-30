package Domain.Rules;

import java.util.Calendar;

public class TimeRangeInDayRule<T> implements Rule<T> {
    int _startHours, _startMinutes;
    int _endHours, _endMinutes;

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
