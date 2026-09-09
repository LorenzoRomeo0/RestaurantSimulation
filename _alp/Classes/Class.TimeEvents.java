import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.Map;

/**
 * TimeEvents
 */
public class TimeEvents {

    private ArrayList<TimeEvent> events = new ArrayList<>();

    public TimeEvents() {
    }

    public void add(TimeEvent event) {
        if (event != null) {
            events.add(event);
        }
    }

    public List<TimeEvent> getAll() {
        return new ArrayList<>(events);
    }

    public int size() {
        return events.size();
    }
    
    public double getValueSumByWeekdayAndDayPart(int dayOfWeek, DayPartUtil.DayPart dayPart, TimeEvent.EventType event, int openingHour) {
        return events.stream()
                .filter(e -> e != null)
                .filter(e -> e.eventType == event)
                .filter(e -> e.getBusinessStartDayOfWeek(openingHour) == dayOfWeek)
                .filter(e -> e.getBusinessStartDayPart(openingHour) == dayPart)
                .mapToDouble(e -> e.value)
                .sum();
    }
    
    public double getValueAvgByWeekdayAndDayPart(int dayOfWeek, DayPartUtil.DayPart dayPart, TimeEvent.EventType event, int openingHour) {
        return events.stream()
                .filter(e -> e != null)
                .filter(e -> e.eventType == event)
                .filter(e -> e.getBusinessStartDayOfWeek(openingHour) == dayOfWeek)
                .filter(e -> e.getBusinessStartDayPart(openingHour) == dayPart)
                .mapToDouble(e -> e.value)
                .average()
                .orElse(0.0);
    }

    public double getAverageTimeByWeekdayAndDayPart(int dayOfWeek, DayPartUtil.DayPart dayPart, TimeEvent.EventType event, int openingHour) {
        return events.stream()
                .filter(e -> e != null)
                .filter(e -> e.eventType == event)
                .filter(e -> e.getBusinessStartDayOfWeek(openingHour) == dayOfWeek)
                .filter(e -> e.getBusinessStartDayPart(openingHour) == dayPart)
                .mapToDouble(TimeEvent::getDurationMinutes)
                .filter(d -> d >= 0)
                .average()
                .orElse(0);
    }
    
    public TimeEvent getLastEventByWeekdayAndDayPart(int dayOfWeek, DayPartUtil.DayPart dayPart, TimeEvent.EventType eventType, int openingHour) {
        return events.stream()
                .filter(e -> e != null)
                .filter(e -> e.startTime != null)
                .filter(e -> e.eventType == eventType)
                .filter(e -> e.getBusinessStartDayOfWeek(openingHour) == dayOfWeek)
                .filter(e -> e.getBusinessStartDayPart(openingHour) == dayPart)
                .max(Comparator.comparing(e -> e.startTime))
                .orElse(null);
    }
    
    /*public double getTotalDurationHoursByWeekdayDayPartAndType(int dayOfWeek, DayPartUtil.DayPart dayPart, TimeEvent.EventType eventType, int openingHour) {
        return (events.stream()
                .filter(e -> e != null)
                .filter(e -> e.eventType == eventType)
                .filter(e -> e.getBusinessStartDayOfWeek(openingHour) == dayOfWeek)
                .filter(e -> e.getBusinessStartDayPart(openingHour) == dayPart)
                .mapToDouble(e -> Math.round(e.getDurationMinutes()))
                .filter(m -> m >= 0)
                .sum()) / 60.0;
    }
    */
    
    
    
    
    ////
    
    @Override
    public String toString() {
        return "TimeEvents{size=" + events.size() + "}";
    }
    
    public String toStringFullText() {
        if (events.isEmpty()) {
            return "TimeEvents{size=0, events=[]}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("TimeEvents{size=").append(events.size()).append(", events=\n");

        for (int i = 0; i < events.size(); i++) {
            sb.append("  [").append(i).append("] ")
              .append(events.get(i))
              .append("\n");
        }

        sb.append("}");
        return sb.toString();
    }
    
    
    public String prettyPrint(List<TimeEvent> events) {
    	StringBuilder sb = new StringBuilder();
        sb.append("TimeEvents{")
          .append("size=").append(events.size())
          .append(", events=\n");
        for (int i = 0; i < events.size(); i++) {
            sb.append("  [").append(i).append("] ")
              .append(events.get(i))
              .append("\n");
        }

        sb.append("}");
        return sb.toString();
    }
    
    public String prettyPrint(Map<Integer, List<TimeEvent>> groupedEvents) {
        StringBuilder sb = new StringBuilder();

        if (groupedEvents == null || groupedEvents.isEmpty()) {
            return "TimeEventsGrouped{size=0, groups=[]}";
        }

        sb.append("TimeEventsGrouped{")
          .append("groups=").append(groupedEvents.size())
          .append(", events=\n");

        for (Map.Entry<Integer, List<TimeEvent>> entry : groupedEvents.entrySet()) {
            sb.append("  Day ").append(entry.getKey()).append(":\n");
            sb.append(prettyPrint(entry.getValue())).append("\n");
        }

        sb.append("}");
        return sb.toString();
    }
    
    public String prettyPrintLong(Map<Integer, Long> countsByDay) {
        StringBuilder sb = new StringBuilder();

        if (countsByDay == null || countsByDay.isEmpty()) {
            return "TimeEvents{size=0, counts=[]}";
        }

        sb.append("TimeEvents{size=").append(countsByDay.size()).append(", counts=\n");

        for (Map.Entry<Integer, Long> entry : countsByDay.entrySet()) {
            sb.append("  Day ")
              .append(entry.getKey())
              .append(" -> ")
              .append(entry.getValue())
              .append("\n");
        }

        sb.append("}");
        return sb.toString();
    }
    
    public String prettyPrintDouble(Map<Integer, Double> avgByDay) {
        StringBuilder sb = new StringBuilder();

        if (avgByDay == null || avgByDay.isEmpty()) {
            return "TimeEventsAvg{size=0, values=[]}";
        }

        sb.append("TimeEventsAvg{size=").append(avgByDay.size()).append(", values=\n");

        for (Map.Entry<Integer, Double> entry : avgByDay.entrySet()) {
            sb.append("  Day ")
              .append(entry.getKey())
              .append(" -> ")
              .append(entry.getValue())
              .append("\n");
        }

        sb.append("}");
        return sb.toString();
    }
    
}