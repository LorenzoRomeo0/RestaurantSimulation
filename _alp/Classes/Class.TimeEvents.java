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

    public List<TimeEvent> filter(Predicate<TimeEvent> predicate) {
        return events.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public List<TimeEvent> filterByType(TimeEvent.EventType type) {
        return events.stream()
                .filter(e -> e.eventType == type)
                .collect(Collectors.toList());
    }

    public List<TimeEvent> filterByStartDay(int dayOfWeek) {
        return events.stream()
                .filter(e -> e.getStartDayOfWeek() == dayOfWeek)
                .collect(Collectors.toList());
    }

    public List<TimeEvent> filterByEndDay(int dayOfWeek) {
        return events.stream()
                .filter(e -> e.getEndDayOfWeek() == dayOfWeek)
                .collect(Collectors.toList());
    }

    public List<TimeEvent> filterByStartHourGreaterOrEqual(int hour) {
        return events.stream()
                .filter(e -> e.getStartHourOfDay() >= hour)
                .collect(Collectors.toList());
    }

    public List<TimeEvent> filterByEndHourGreaterOrEqual(int hour) {
        return events.stream()
                .filter(e -> e.getEndHourOfDay() >= hour)
                .collect(Collectors.toList());
    }

    public List<TimeEvent> filterByTypeDayAndEndHour(TimeEvent.EventType type, int dayOfWeek, int minHour) {
        return events.stream()
                .filter(e -> e.eventType == type)
                .filter(e -> e.getEndDayOfWeek() == dayOfWeek)
                .filter(e -> e.getEndHourOfDay() >= minHour)
                .collect(Collectors.toList());
    }

    public double getAverageDurationHours() {
        return events.stream()
                .mapToDouble(TimeEvent::getDurationHours)
                .filter(d -> d >= 0)
                .average()
                .orElse(-1);
    }

    public double getTotalDurationHoursByType(TimeEvent.EventType type) {
        return events.stream()
                .filter(e -> e.eventType == type)
                .mapToDouble(TimeEvent::getDurationHours)
                .filter(d -> d >= 0)
                .sum();
    }
        
    
    public List<TimeEvent> filterByTypeAndEndHour(TimeEvent.EventType type, int hour){
    	 return events.stream()
                 .filter(e -> e.eventType == type)
                 .filter(e -> e.getEndHourOfDay() >= hour)
                 .collect(Collectors.toList());
    }

    
    public Map<Integer, List<TimeEvent>> groupByDayAndFilterByEndHourAndEventType(int endHour, TimeEvent.EventType eventType) {
        return events.stream()
                .filter(e -> e.eventType == eventType)
                .filter(e -> e.getEndHourOfDay() >= endHour)
                .collect(Collectors.groupingBy(TimeEvent::getEndDayOfWeek));
    }
    
    public Map<Integer, Long> groupByDayAndFilterByEndHourAndEventTypeCount(int endHour, TimeEvent.EventType eventType) {
        return events.stream()
                .filter(e -> e.eventType == eventType)
                .filter(e -> e.getEndHourOfDay() >= endHour)
                .collect(Collectors.groupingBy(
                        TimeEvent::getStartDayOfWeek,
                        Collectors.counting()
                ));
    }
    
    public Map<Integer, Double> groupByDayAndFilterByEndHourAndEventTypeAvgDuration(int endHour, TimeEvent.EventType eventType) {
        return events.stream()
                .filter(e -> e.eventType == eventType)
                .filter(e -> e.getEndHourOfDay() >= endHour)
                .collect(Collectors.groupingBy(
                        TimeEvent::getStartDayOfWeek,
                        Collectors.averagingDouble(TimeEvent::getDurationHours)
                ));
    }
    
    public Map<Integer, List<TimeEvent>> groupByDayAndfilterDelaysAfterEndHourByEventType(int thresholdHour, TimeEvent.EventType eventType) {
        return events.stream()
                .filter(e -> e != null)
                .filter(e -> e.eventType == eventType)
                .filter(e -> e.getDurationAfterHourMillis(thresholdHour) > 0)
                .collect(Collectors.groupingBy(
                        TimeEvent::getStartDayOfWeek
                ));
    }
    
    public Map<Integer, Double> groupByDayAndFilterDelaysAfterEndHourByEventTypeAvgMinutes(int thresholdHour, TimeEvent.EventType eventType) {
        return events.stream()
                .filter(e -> e != null)
                .filter(e -> e.eventType == eventType)
                .filter(e -> e.getDurationAfterHourMillis(thresholdHour) > 0)
                .collect(Collectors.groupingBy(
                        TimeEvent::getStartDayOfWeek,
                        Collectors.averagingDouble(e -> e.getDurationAfterHourMinutes(thresholdHour))
                ));
    }

    
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