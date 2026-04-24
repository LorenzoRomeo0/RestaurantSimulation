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

    public static DayPart getDayPart(int hour, int minute) {
        int totalMinutes = hour * 60 + minute;

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

    public static DayPart getCurrentDayPart(Agent agent) {
        int hour = agent.getHourOfDay();
        int minute = agent.getMinute();
        return getDayPart(hour, minute);
    }
}