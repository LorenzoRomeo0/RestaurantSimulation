import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * TimeEvent
 */	
public class TimeEvent {
	
	public enum EventType {
        COOK_END,
        COOK_SHIFT,
        COOK_IDLE,
        WAITER_END,
        WAITER_SHIFT,
        WAITER_IDLE,
        CUSTOMER_WAIT,
        CUSTOMER_WAIT_ENTRANCE,
        CUSTOMER_WAIT_TABLE,
        CUSTOMER_WAIT_PAY,
        CUSTOMER_STAY,
        MONEY,
        TABLES_USAGE_OUTSIDE,
        TABLES_USAGE_INSIDE,
        TABLES_USAGE_BAR
    }

	public Date startTime;
    public Date endTime;
    public EventType eventType;
    public double value;
    

    public TimeEvent(Date startTime, Date endTime, EventType eventType) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.eventType = eventType;
    }
    
    public TimeEvent(Date startTime, Date endTime, EventType eventType, double value) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.eventType = eventType;
		this.value = value;
    }
    
    
    public int getStartDayOfWeek() {
        return startTime != null ? getDayOfWeek(startTime) : -1;
    }

    public int getEndDayOfWeek() {
        return endTime != null ? getDayOfWeek(endTime) : -1;
    }

    public int getStartHourOfDay() {
        return startTime != null ? getHourOfDay(startTime) : -1;
    }
    
    public int getStartMinuteOfHour() {
        return startTime != null ? getMinuteOfHour(startTime) : -1;
    }

    public int getEndHourOfDay() {
        return endTime != null ? getHourOfDay(endTime) : -1;
    }
    
    public long getDurationMillis() {
        if (startTime == null || endTime == null) {
            return -1;
        }
        return endTime.getTime() - startTime.getTime();
    }
    
    
    private int getMinuteOfHour(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MINUTE);
    }
    
    public double getDurationSeconds() {
        long ms = getDurationMillis();
        return ms >= 0 ? ms / 1000.0 : -1;
    }

    public double getDurationMinutes() {
        long ms = getDurationMillis();
        return ms >= 0 ? ms / 60000.0 : -1;
    }

    public double getDurationHours() {
        long ms = getDurationMillis();
        return ms >= 0 ? ms / 3600000.0 : -1;
    }
    
    public long getDurationAfterHourMillis(int thresholdHour) {
        if (startTime == null || endTime == null) {
            return -1;
        }

        if (endTime.before(startTime)) {
            return -1;
        }

        Calendar thresholdCal = Calendar.getInstance();
        thresholdCal.setTime(endTime);
        thresholdCal.set(Calendar.HOUR_OF_DAY, thresholdHour);
        thresholdCal.set(Calendar.MINUTE, 0);
        thresholdCal.set(Calendar.SECOND, 0);
        thresholdCal.set(Calendar.MILLISECOND, 0);

        Date thresholdTime = thresholdCal.getTime();

        long effectiveStart = Math.max(startTime.getTime(), thresholdTime.getTime());
        long effectiveEnd = endTime.getTime();

        long diff = effectiveEnd - effectiveStart;
        return Math.max(diff, 0);
    }
    
    public double getDurationAfterHourMinutes(int thresholdHour) {
        long ms = getDurationAfterHourMillis(thresholdHour);
        return ms >= 0 ? ms / 60000.0 : -1;
    }

    public double getDurationAfterHourHours(int thresholdHour) {
        long ms = getDurationAfterHourMillis(thresholdHour);
        return ms >= 0 ? ms / 3600000.0 : -1;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String s = (startTime == null) ? "null" : sdf.format(startTime);
        String e = (endTime == null) ? "null" : sdf.format(endTime);
        String t = (eventType == null) ? "null" : eventType.name();

        return "TimeEvent{eventType=" + t
            + ", startTime=" + s
            + ", endTime=" + e
            + ", durationHours=" + getDurationHours()
            + "}";
    }

}