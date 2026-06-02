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

int insidePrio = inside_prio_schedule.getValue();
int outsidePrio = outside_prio_schedule.getValue();
int barPrio = bar_prio_schedule.getValue();

for (int prio = 1; prio <= 3; prio++) {

    if (insidePrio == prio) {
        for (Table t : insideTablesPool) {
            if (t.isFree && t.size >= customerGroup.size) {
                t.isFree = false;
                return t;
            }
        }
    }

    if (outsidePrio == prio) {
        for (Table t : outsideTablesPool) {
            if (t.isFree && t.size >= customerGroup.size) {
                t.isFree = false;
                return t;
            }
        }
    }

    if (barPrio == prio) {
        for (Table t : barTablesPool) {
            if (t.isFree && t.size >= customerGroup.size) {
                t.isFree = false;
                return t;
            }
        }
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

double maxIncrease = maxIncreaseEatingTimeModel; //percentuale di tempo aggiunto massima
double growth = growthEatingTimeModel;

double multiplier = 1 + maxIncrease * (1 - Math.exp(-growth * (groupSize - 1)));

return baseMeanTime * multiplier;



/*ALCODEEND*/}

double init()
{/*ALCODESTART::1774982011623*/
menuItems = new MenuItems(this);

traceln("INIT END-------------");
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

timeEventsCooksDebugText.setText(
	"WAITER EVENTS: "+
	"counts =  \n "+
	timeEvents.prettyPrintLong(
		timeEvents.groupByDayAndFilterByEndHourAndEventTypeCount(
			closingTime, TimeEvent.EventType.COOK_SHIFT
		)
	)
	+"\n" +
	
	"avgs shift length = \n "+
	timeEvents.prettyPrintDouble(
		timeEvents.groupByDayAndFilterByEndHourAndEventTypeAvgDuration(
			closingTime, TimeEvent.EventType.COOK_SHIFT
		)
	)
	+"\n" +
	"avg overtimes \n"+
	timeEvents.prettyPrintDouble(
		timeEvents.groupByDayAndFilterDelaysAfterEndHourByEventTypeAvgMinutes(
			closingTime, TimeEvent.EventType.COOK_SHIFT
		)
		
	)
	
	+ "\ndays = { \n" +
	timeEvents.prettyPrint(
		timeEvents.groupByDayAndFilterByEndHourAndEventType(
			closingTime, TimeEvent.EventType.COOK_SHIFT
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

double menuItemsDebugTextUpdater()
{/*ALCODESTART::1776499860026*/
DayPartUtil.DayPart currentDayPart = DayPartUtil.getCurrentDayPart(this);

MenuItems items = new MenuItems(this);
ArrayList<MenuItem> currentItems = items.getItemsByDayPart(
    DayPartUtil.getCurrentDayPart(this)
);

currentMenuItemsDebugText.setText(MenuItems.prettyPrintMenuItems(currentItems));
/*ALCODEEND*/}

double moneyDebugTextUpdater()
{/*ALCODESTART::1777058084938*/
double total = 0.0;

for (Order order : orders) {
    if (order != null) {
        total += order.getTotalPrice();
    }
}

moneySpentText.setText("€" + total);
/*ALCODEEND*/}

double menuCopulaGeneratorDebugTextUpdater()
{/*ALCODESTART::1777316827810*/
menuCopulaGeneratorDebugText.setText(
	menuCopulaGenerator.modelPrettyString(
		DayPartUtil.getCurrentDayPart(this), 
		menuCopulaGenerator.getCopulaModel(
			DayPartUtil.getCurrentDayPart(this)
		)
	)
);
/*ALCODEEND*/}

double waitersPoolDebugTextUpdater()
{/*ALCODESTART::1778769341205*/
StringBuilder sb = new StringBuilder();

for (Waiter w : waiters) {
    Agent owner = w.getServicedEntity();   // null se il waiter è libero

    if (waitersPool.containsUnit(w)) {
        sb.append("[ \n")
        .append(w)
          .append(" -> ")
          .append(owner == null ? "no owner" : owner.getClass().getName() + " " + owner.getId())
          .append("\n ] \n");
    }
}


waitersPoolDebugText.setText(sb.toString());
/*ALCODEEND*/}

double customerGroupDebugTextUpdater()
{/*ALCODESTART::1778862300472*/
StringBuilder sb = new StringBuilder();

for (CustomerGroup c : customerGroup) {
        sb.append("[ \n")
        .append(c)
          .append("\n ] \n");
}


customerGroupDebugText.setText(sb.toString());
/*ALCODEEND*/}

int getMaxWaiters()
{/*ALCODESTART::1779227005235*/
int maxWaiters = 0;
for(double t = 0; t < 7 * 24; t += 1) { 
    int val = waiters_schedule.getValue(t, HOUR); 
    if(val > maxWaiters) {
        maxWaiters = val;
    }
}

return maxWaiters;
/*ALCODEEND*/}

double menuReadingModel(int groupSize)
{/*ALCODESTART::1779701422068*/
double min = 0;
double max = Double.POSITIVE_INFINITY;
double mu = muMenuReading;
double sigma = sigmaMenuReading;


double baseTime = normal(min, max, mu, sigma);

double k = kMenuReadingModel; //% of time an additional person adds
return baseTime * (1 + k * (groupSize - 1));
/*ALCODEEND*/}

double payTimeModel(int groupSize)
{/*ALCODESTART::1779873697186*/
// Returned value is in SECONDS

double p = pPayTimeModel;
int onePaysAll = bernoulli(p);

double min = 5;
double max = Double.POSITIVE_INFINITY;
double mu = muPayTime;
double sigma = sigmaPayTime;

double payTime = 
	(onePaysAll) * 
		(normal(min, max, mu, sigma) * 1.05)
	 + 
	(1-onePaysAll) * 
		(normal(min, max, mu * groupSize , sigma * sqrt(groupSize))); 

return payTime;

//normal(0.5, 5, 2, 1) + agent.size/5
/*ALCODEEND*/}

int getWaitersNumber()
{/*ALCODESTART::1780067259114*/
int value = waiters_schedule.getValue();
int absent = binomial(pWaiterSickness, value);
value = value - absent;


/*ALCODEEND*/}

int getCooksNumber()
{/*ALCODESTART::1780067334032*/
int value = waiters_schedule.getValue();
int absent = binomial(pCookSickness, value);
return value - absent;

/*ALCODEEND*/}

