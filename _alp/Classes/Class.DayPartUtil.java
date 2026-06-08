import com.anylogic.engine.Agent;

/**
 * DayPart utility
 */
public class DayPartUtil {

    public enum DayPart {
        BREAKFAST,
        LUNCH,
        BREAK,
        DINNER,
        CLOSED
    }

    public static DayPart getDayPart(int hour, int minute, int openingHour) {
        int totalMinutes = hour * 60 + minute;
        int openMinutes = openingHour * 60;

        if (totalMinutes >= 7 * 60 && totalMinutes < 11 * 60) {
            return DayPart.BREAKFAST;
        } else if (totalMinutes >= 11 * 60 && totalMinutes < 15 * 60) {
            return DayPart.LUNCH;
        } else if (totalMinutes >= 15 * 60 && totalMinutes < 19 * 60) {
            return DayPart.BREAK;
        } else if (totalMinutes >= 19 * 60 && totalMinutes < 23 * 60) {
            return DayPart.DINNER;
        } else {
            return DayPart.CLOSED;
        }
    }
    public static DayPart getCurrentDayPart(Agent agent, int openingHour) {
        int hour = agent.getHourOfDay();
        int minute = agent.getMinute();
        return getDayPart(hour, minute, openingHour);
    }
    
    public static int getBusinessDayOfWeek(Date date, int openingHour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int totalMinutes = hour * 60 + minute;
        int openMinutes = openingHour * 60;

        if (totalMinutes < openMinutes) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }

        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static DayPart getDayPart(Date date, int openingHour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getDayPart(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), openingHour);
    }
}