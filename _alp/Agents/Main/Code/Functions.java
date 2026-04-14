int tablesMaxSize()
{/*ALCODESTART::1769810442527*/
int maxSize = 0;

for(Table t : insideTablesPool){
	if(t.size > maxSize)maxSize = t.size;
}

for(Table t : outsideTablesPool){
	if(t.size > maxSize)maxSize = t.size;
}

for(Table t : barTablesPool){
	if(t.size > maxSize)maxSize = t.size;
}

return maxSize;
/*ALCODEEND*/}

Table tableSelection(CustomerGroup customerGroup)
{/*ALCODESTART::1770025342170*/
/*
int n = 0;
for (Table t : insideTablesPool){
	if (t.isFree) n++;
}
for (Table t : outsideTablesPool){
	if (t.isFree) n++;
}
for (Table t : barTablesPool){
	if (t.isFree) n++;
}
System.out.println("Available tables: " + n);
*/

//usare resourcepool?

for(Table t : insideTablesPool){
	if(t.isFree && t.size >= customerGroup.size){
		t.isFree = false;
		return t;
	}
}

for(Table t : outsideTablesPool){
	if(t.isFree && t.size >= customerGroup.size){
		t.isFree = false;
		return t;
	}
}

for(Table t : barTablesPool){
	if(t.isFree && t.size >= customerGroup.size){
		t.isFree = false;
		return t;
	}
}

return null;
/*ALCODEEND*/}

double eatingTimeModel(int groupSize)
{/*ALCODESTART::1772007563909*/
//Funzione che modella il tempo impiegato a mangiare secondo la time expansion hypothesis
//Potrebbe essere migliorata in quanto nei pranzi con i colleghi generalmente non si chiacchiera così tanto.
//Di più con amici e parenti (cena e weekends)

double baseMeanTime = eating_time_schedule.getValue();

double minTime = lognormal(baseMeanTime, baseMeanTime*0.2, 5);

double maxIncrease = 0.8; //percentuale di tempo aggiunto massima

double growth = 0.35;

double multiplier = 1 + maxIncrease * (1 - Math.exp(-growth * (groupSize - 1)));

return baseMeanTime * multiplier;


/*ALCODEEND*/}

double init_statistics()
{/*ALCODESTART::1774982011623*/

/*ALCODEEND*/}

double register_customer_wait(Date start,Date end)
{/*ALCODESTART::1774982475261*/
TimeEvent ev = new TimeEvent(start, end, TimeEvent.EventType.CUSTOMER_WAIT);
timeEvents.add(ev);

int d = ev.getEndDayOfWeek() - 1;
int h = ev.getEndHourOfDay();

double waitMin = ev.getDurationMinutes();

if (d >= 0 && d < 7 && h >= 0 && h < 24 && waitMin >= 0) {
    customer_wait_sum[d][h] += waitMin;
    customer_wait_count[d][h] += 1;
}
/*ALCODEEND*/}

String day_to_string(int day)
{/*ALCODESTART::1775044645264*/
switch (day) {
    case MONDAY: return "MONDAY";
    case TUESDAY: return "TUESDAY";
    case WEDNESDAY: return "WEDNESDAY";
    case THURSDAY: return "THURSDAY";
    case FRIDAY: return "FRIDAY";
    case SATURDAY: return "SATURDAY";
    case SUNDAY: return "SUNDAY";
    default: return "UNKNOWN";
}
/*ALCODEEND*/}

double register_waiter_shift(Date start,Date end)
{/*ALCODESTART::1775044771309*/
TimeEvent ev = new TimeEvent(start, end, TimeEvent.EventType.WAITER_SHIFT);
timeEvents.add(ev);
/*ALCODEEND*/}

double waitersDebugTextUpdater()
{/*ALCODESTART::1775232593178*/
StringBuilder sb = new StringBuilder();

for (int i = 0; i < waiters.size(); i++) {
    Waiter w = waiters.get(i);

    sb.append(w.getId()).append(" [").append(w.getIndex()).append("] ")
      .append(w.waiter_statechart.getActiveSimpleState().name())
      .append("\n")
      .append(w.isOffShift ? "offShift" : "onShift")
      .append("\n")
      .append("start ").append(w.shiftStartDate)
      .append("\n")
      .append("end ").append(w.shiftEndDate)
      .append("\n")
      .append("lastShift: ").append(w.lastShiftDay)
      .append("\n")
      .append(w.isMoving() ? "moving" : "")
      .append("\n")
      .append(w.endShiftRequested ? "endRequested" : "")
      .append("\n")
      .append(w.startShiftRequested ? "startRequested" : "")
      .append("\n")
      .append(w.wasOvertime ? "wasOvertime" : "");

    if (i < waiters.size() - 1) {
        sb.append("\n\n--------------------\n\n");
    }
}

txtWaitersDebug.setText(sb.toString());

/*ALCODEEND*/}

double timeEventsDebugTextUpdater()
{/*ALCODESTART::1776067186397*/
//timeEventsDebugText.setText(timeEvents.toStringFullText());
//timeEventsCustomerStayDebugText.setText(timeEvents.toStringByTypeAfter23(TimeEvent.EventType.CUSTOMER_STAY));

	
timeEventsCustomerStayDebugText.setText(
	"CUSTOMER EVENTS: "+
	"counts =  \n "+
	timeEvents.prettyPrintLong(
		timeEvents.groupByDayAndFilterByEndHourAndEventTypeCount(
			closingTime, TimeEvent.EventType.CUSTOMER_STAY
		)
	)
	+"\n" +
	
	"avgs = \n "+
	timeEvents.prettyPrintDouble(
		timeEvents.groupByDayAndFilterByEndHourAndEventTypeAvgDuration(
			closingTime, TimeEvent.EventType.CUSTOMER_STAY
		)
	)
	+"\n" +
	"avg delays \n"+
	timeEvents.prettyPrintDouble(
		timeEvents.groupByDayAndFilterDelaysAfterEndHourByEventTypeAvgMinutes(
			closingTime, TimeEvent.EventType.CUSTOMER_STAY
		)
		
	)
	
	+ "\ngroups = { \n" +
	timeEvents.prettyPrint(
		timeEvents.groupByDayAndFilterByEndHourAndEventType(
			closingTime, TimeEvent.EventType.CUSTOMER_STAY
		)
	)
);

timeEventsWaitersDebugText.setText(
	"WAITER EVENTS: "+
	"counts =  \n "+
	timeEvents.prettyPrintLong(
		timeEvents.groupByDayAndFilterByEndHourAndEventTypeCount(
			closingTime, TimeEvent.EventType.WAITER_SHIFT
		)
	)
	+"\n" +
	
	"avgs shift length = \n "+
	timeEvents.prettyPrintDouble(
		timeEvents.groupByDayAndFilterByEndHourAndEventTypeAvgDuration(
			closingTime, TimeEvent.EventType.WAITER_SHIFT
		)
	)
	+"\n" +
	"avg overtimes \n"+
	timeEvents.prettyPrintDouble(
		timeEvents.groupByDayAndFilterDelaysAfterEndHourByEventTypeAvgMinutes(
			closingTime, TimeEvent.EventType.WAITER_SHIFT
		)
		
	)
	
	+ "\ndays = { \n" +
	timeEvents.prettyPrint(
		timeEvents.groupByDayAndFilterByEndHourAndEventType(
			closingTime, TimeEvent.EventType.WAITER_SHIFT
		)
	)
);

























/*ALCODEEND*/}

double register_customer_stay(Date start,Date end)
{/*ALCODESTART::1776067684596*/
TimeEvent ev = new TimeEvent(start, end, TimeEvent.EventType.CUSTOMER_STAY);
timeEvents.add(ev);

/*
int d = ev.getEndDayOfWeek() - 1;
int h = ev.getEndHourOfDay();

double waitMin = ev.getDurationMinutes();

if (d >= 0 && d < 7 && h >= 0 && h < 24 && waitMin >= 0) {
    customer_wait_sum[d][h] += waitMin;
    customer_wait_count[d][h] += 1;
}

*/
/*ALCODEEND*/}

double register_cook_shift(Date start,Date end)
{/*ALCODESTART::1776172006330*/
TimeEvent ev = new TimeEvent(start, end, TimeEvent.EventType.COOK_SHIFT);
timeEvents.add(ev);
/*ALCODEEND*/}

