int tablesMaxSize()
{/*ALCODESTART::1769810442527*/
int maxSize = 0;

for(Table t : insideTables){
	if(t.size > maxSize)maxSize = t.size;
}

for(Table t : outsideTables){
	if(t.size > maxSize)maxSize = t.size;
}

for(Table t : barTables){
	if(t.size > maxSize)maxSize = t.size;
}

return maxSize;
/*ALCODEEND*/}

Table tableSelection(CustomerGroup customerGroup)
{/*ALCODESTART::1770025342170*/
int insidePrio = insidePrioSchedule.getValue();
int outsidePrio = outsidePrioSchedule.getValue();
int barPrio = barPrioSchedule.getValue();

for (int prio = 1; prio <= 3; prio++) {

    if (insidePrio == prio) {
        for (Table t : insideTables) {
            if (t.isFree && t.size >= customerGroup.size) {
                t.isFree = false;
                return t;
            }
        }
    }

    if (outsidePrio == prio) {
        for (Table t : outsideTables) {
            if (t.isFree && t.size >= customerGroup.size) {
                t.isFree = false;
                return t;
            }
        }
    }

    if (barPrio == prio) {
        for (Table t : barTables) {
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

double baseMeanTime = eatingTimeSchedule.getValue();

double minTime = lognormal(baseMeanTime, baseMeanTime*0.2, 5);

double maxIncrease = maxIncreaseEatingTimeModel; //percentuale di tempo aggiunto massima
double growth = growthEatingTimeModel;

double multiplier = 1 + maxIncrease * (1 - Math.exp(-growth * (groupSize - 1)));

return baseMeanTime * multiplier;



/*ALCODEEND*/}

double init()
{/*ALCODESTART::1774982011623*/
menuItems = new MenuItems(this);

//tablesInit();

traceln("INIT END-------------");
/*ALCODEEND*/}

String dayToString(int day)
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

/*
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

*/
/*ALCODEEND*/}

double menuItemsDebugTextUpdater()
{/*ALCODESTART::1776499860026*/
DayPartUtil.DayPart currentDayPart = DayPartUtil.getCurrentDayPart(this, openingTime);

MenuItems items = new MenuItems(this);
ArrayList<MenuItem> currentItems = items.getItemsByDayPart(
    DayPartUtil.getCurrentDayPart(this, openingTime)
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
		DayPartUtil.getCurrentDayPart(this, openingTime), 
		menuCopulaGenerator.getCopulaModel(
			DayPartUtil.getCurrentDayPart(this, openingTime)
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
    int val = waitersSchedule.getValue(t, HOUR); 
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
		(normal(min, max, mu, sigma))
	 + 
	(1-onePaysAll) * 
		(normal(min, max, mu * groupSize , sigma * sqrt(groupSize))); 

return payTime;

//normal(0.5, 5, 2, 1) + agent.size/5
/*ALCODEEND*/}

int groupSizeModel()
{/*ALCODESTART::1780666597930*/
double min = minGroupSize;
double max = maxGroupsize;
double mu = groupsizeSchedule.getValue();
double sigma = sigmaGroupSize;

return (int) normal(min, max, mu, sigma);
/*ALCODEEND*/}

double registerPaymentEvent(Date start,Date end,double amount)
{/*ALCODESTART::1780734091041*/
TimeEvent ev = new TimeEvent(start, end, TimeEvent.EventType.MONEY, amount);
timeEvents.add(ev);
/*ALCODEEND*/}

double paymentEventsUpdater()
{/*ALCODESTART::1780735636798*/
StringBuilder sb = new StringBuilder();

sb.append("weekday,daypart,total")
  .append(System.lineSeparator());

for (int day = Calendar.SUNDAY; day <= Calendar.SATURDAY; day++) {
    for (DayPartUtil.DayPart dayPart : DayPartUtil.DayPart.values()) {
        double total = timeEvents.getValueSumByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.MONEY, openingTime);

		String dayName = dayToString(day);
        			String key = dayName + "-" + dayPart;

        sb.append(dayName)
          .append(",")
          .append(dayPart)
          .append(",")
          .append(total)
          .append(System.lineSeparator());
          
        DataItem item = paymentChartItems.get(key);

        if (item == null) {
            item = new DataItem();
            item.setValue(total);
            paymentChartItems.put(key, item);

            paymentEventsChart.addDataItem(item, key, dayPartColors.get(dayPart));
        } else {
            item.setValue(total);
        }
    }
}

paymentCsv = sb.toString();
paymentEventsText.setText(sb.toString());
/*ALCODEEND*/}

double registerCustomerWaitEntranceEvent(Date start,Date end)
{/*ALCODESTART::1780759689915*/
TimeEvent ev = new TimeEvent(start, end, TimeEvent.EventType.CUSTOMER_WAIT_ENTRANCE);
timeEvents.add(ev);
/*ALCODEEND*/}

double registerCustomerWaitTableEvent(Date start,Date end)
{/*ALCODESTART::1780760523532*/
TimeEvent ev = new TimeEvent(start, end, TimeEvent.EventType.CUSTOMER_WAIT_TABLE);
timeEvents.add(ev);
/*ALCODEEND*/}

double registerCustomerWaitPayEvent(Date start,Date end)
{/*ALCODESTART::1780761019562*/
TimeEvent ev = new TimeEvent(start, end, TimeEvent.EventType.CUSTOMER_WAIT_PAY);
timeEvents.add(ev);
/*ALCODEEND*/}

double customerEventsUpdater()
{/*ALCODESTART::1780762327897*/
//wait entrance

StringBuilder sbEntrance = new StringBuilder();
StringBuilder sbTable = new StringBuilder();
StringBuilder sbPay = new StringBuilder();
StringBuilder sbStay = new StringBuilder();
StringBuilder sbServed = new StringBuilder();

sbEntrance.append("weekday,daypart,waitTimeEntrance")
  .append(System.lineSeparator());

sbTable.append("weekday,daypart,waitTimeTable")
  .append(System.lineSeparator());
  
sbPay.append("weekday,daypart,waitTimePay")
  .append(System.lineSeparator());
  
sbStay.append("weekday,daypart,stayTime,lastCustomerHour")
  .append(System.lineSeparator());
    
sbServed.append("weekday,daypart,totalServed")
  .append(System.lineSeparator());
  

for (int day = Calendar.SUNDAY; day <= Calendar.SATURDAY; day++) {
    for (DayPartUtil.DayPart dayPart : DayPartUtil.DayPart.values()) {
        
        double averageWaitEntrance = timeEvents.getAverageTimeByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.CUSTOMER_WAIT_ENTRANCE, openingTime);
		double averageWaitTable = timeEvents.getAverageTimeByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.CUSTOMER_WAIT_TABLE, openingTime);
		double averageWaitPay = timeEvents.getAverageTimeByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.CUSTOMER_WAIT_PAY, openingTime);
		double averageStay = timeEvents.getAverageTimeByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.CUSTOMER_STAY, openingTime);
		double totalServed = timeEvents.getValueSumByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.CUSTOMERS_SERVED, openingTime);
		
		TimeEvent lastCustomer = (dayPart == dayPart.CLOSED) ?
			timeEvents.getLastEventByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.CUSTOMERS_SERVED, openingTime)
				:
			null;
		double lastPaymentHour = -1;

		if (lastCustomer != null) {
		    lastPaymentHour = DayPartUtil.getBusinessHour(lastCustomer.endTime,openingTime);
		}
		
		String dayName = dayToString(day);
        			String key = dayName + "-" + dayPart;

        sbEntrance.append(dayName)
          .append(",")
          .append(dayPart)
          .append(",")
          .append(averageWaitEntrance)
          .append(System.lineSeparator());
          
        sbTable.append(dayName)
          .append(",")
          .append(dayPart)
          .append(",")
          .append(averageWaitTable)
          .append(System.lineSeparator());
          
        sbPay.append(dayName)
          .append(",")
          .append(dayPart)
          .append(",")
          .append(averageWaitPay)
          .append(System.lineSeparator());
          
        sbStay.append(dayName)
          .append(",")
          .append(dayPart)
          .append(",")
          .append(averageStay)
          .append(",")
          .append(lastPaymentHour)
          .append(System.lineSeparator());
          
        sbServed.append(dayName)
          .append(",")
          .append(dayPart)
          .append(",")
          .append(totalServed)
          .append(System.lineSeparator());
         
        
        DataItem item = customerWaitEntranceChartItems.get(key);

        if (item == null) {
            item = new DataItem();
            item.setValue(averageWaitEntrance);
            customerWaitEntranceChartItems.put(key, item);

            customerWaitEntranceChart.addDataItem(item, key, dayPartColors.get(dayPart));
        } else {
            item.setValue(averageWaitEntrance);
        }
        
        
        item = customerWaitTableChartItems.get(key);
        if (item == null) {
            item = new DataItem();
            item.setValue(averageWaitTable);
            customerWaitTableChartItems.put(key, item);

            customerWaitTableChart.addDataItem(item, key, dayPartColors.get(dayPart));
        } else {
            item.setValue(averageWaitTable);
        }
        
        
        item = customerWaitPayChartItems.get(key);
        if (item == null) {
            item = new DataItem();
            item.setValue(averageWaitPay);
            customerWaitPayChartItems.put(key, item);

            customerWaitPayChart.addDataItem(item, key, dayPartColors.get(dayPart));
        } else {
            item.setValue(averageWaitPay);
        }
        
        item = customerStayChartItems.get(key);
        if (item == null) {
            item = new DataItem();
            item.setValue(averageStay);
            customerStayChartItems.put(key, item);

            customerStayChart.addDataItem(item, key, dayPartColors.get(dayPart));
        } else {
            item.setValue(averageStay);
        }
        
        item = customersServedChartItems.get(key);
        if (item == null) {
            item = new DataItem();
            item.setValue(totalServed);
            customersServedChartItems.put(key, item);

            customersServedChart.addDataItem(item, key, dayPartColors.get(dayPart));
        } else {
            item.setValue(totalServed);
        }
        
        
    }
}

customersServedCsv = sbServed.toString();
customerWaitPayCsv = sbPay.toString();
customerWaitTableCsv = sbTable.toString();
customerWaitEntranceCsv = sbEntrance.toString();
customerStayCsv = sbStay.toString();

customerWaitEntranceText.setText(sbEntrance.toString());
customerWaitTableText.setText(sbTable.toString());
customerWaitPayText.setText(sbPay.toString());
customerStayText.setText(sbStay.toString());
customersServedText.setText(sbServed.toString());
/*ALCODEEND*/}

double registerCustomerStayEvent(Date start,Date end)
{/*ALCODESTART::1780765479315*/
TimeEvent ev = new TimeEvent(start, end, TimeEvent.EventType.CUSTOMER_STAY);
timeEvents.add(ev);
/*ALCODEEND*/}

double registerWaiterIdlevent(Date start,Date end)
{/*ALCODESTART::1780776464516*/
TimeEvent ev = new TimeEvent(start, end, TimeEvent.EventType.WAITER_IDLE);
timeEvents.add(ev);
/*ALCODEEND*/}

double waiterEventsUpdater()
{/*ALCODESTART::1780776729342*/
//wait entrance

StringBuilder sbIdle = new StringBuilder();
StringBuilder sbOvertime = new StringBuilder();


sbIdle.append("weekday,daypart,meanIdleTime")
  .append(System.lineSeparator());


sbOvertime.append("weekday,daypart,overtimeHours,avgOvertime")
  .append(System.lineSeparator());
  


for (int day = Calendar.SUNDAY; day <= Calendar.SATURDAY; day++) {
    for (DayPartUtil.DayPart dayPart : DayPartUtil.DayPart.values()) {
        
        double averageIdle = timeEvents.getAverageTimeByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.WAITER_IDLE, openingTime);
		double totalOvertime = timeEvents.getValueSumByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.WAITER_OVERTIME, openingTime);
		double avgOvertime = timeEvents.getValueAvgByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.WAITER_OVERTIME, openingTime);
		
		
		String dayName = dayToString(day);
        String key = dayName + "-" + dayPart;

        sbIdle.append(dayName)
          .append(",")
          .append(dayPart)
          .append(",")
          .append(averageIdle)
          .append(System.lineSeparator());
          
        
          
        
        DataItem item = waiterIdleChartItems.get(key);

        if (item == null) {
            item = new DataItem();
            item.setValue(averageIdle);
            waiterIdleChartItems.put(key, item);

            waiterIdleChart.addDataItem(item, key, dayPartColors.get(dayPart));
        } else {
            item.setValue(averageIdle);
        }
        
        if(dayPart == DayPartUtil.DayPart.CLOSED){
	        sbOvertime.append(dayName)
	          .append(",")
	          .append(dayPart)
	          .append(",")
	          .append(totalOvertime)
	          .append(",")
	          .append(avgOvertime)
	          .append(System.lineSeparator());
	          
	          item = waiterOvertimeChartItems.get(key);
	
	        if (item == null) {
	            item = new DataItem();
	            item.setValue(totalOvertime);
	            waiterOvertimeChartItems.put(key, item);
	
	            waiterOvertimeChart.addDataItem(item, key, dayPartColors.get(dayPart));
	        } else {
	            item.setValue(totalOvertime);
	        }
        }
    }
}


waiterIdleCsv = sbIdle.toString();
waiterOvertimeCsv = sbOvertime.toString();

waitersIdleText.setText(sbIdle.toString());
waitersOvertimeText.setText(sbOvertime.toString());


/*ALCODEEND*/}

double registerCookIdlevent(Date start,Date end)
{/*ALCODESTART::1780905019206*/
TimeEvent ev = new TimeEvent(start, end, TimeEvent.EventType.COOK_IDLE);
timeEvents.add(ev);
/*ALCODEEND*/}

double cooksEventsUpdater()
{/*ALCODESTART::1780905104134*/
StringBuilder sbIdle = new StringBuilder();
StringBuilder sbOvertime = new StringBuilder();
StringBuilder sbShift = new StringBuilder();


sbIdle.append("weekday,daypart,meanIdleTime")
  .append(System.lineSeparator());

sbOvertime.append("weekday,daypart,overtimeHours,avgOvertime")
  .append(System.lineSeparator());

sbShift.append("weekday,daypart,cooksInShift,hours,salary,overtimeSalary,totalSalary")
  .append(System.lineSeparator());


for (int day = Calendar.SUNDAY; day <= Calendar.SATURDAY; day++) {
    for (DayPartUtil.DayPart dayPart : DayPartUtil.DayPart.values()) {
        
        double averageIdle = timeEvents.getAverageTimeByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.COOK_IDLE, openingTime);
		double totalOvertime = timeEvents.getValueSumByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.COOK_OVERTIME, openingTime);
		double avgOvertime = timeEvents.getValueAvgByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.COOK_OVERTIME, openingTime);
		
		String dayName = dayToString(day);
        String key = dayName + "-" + dayPart;

        sbIdle.append(dayName)
          .append(",")
          .append(dayPart)
          .append(",")
          .append(averageIdle)
          .append(System.lineSeparator());
        
         /*
         sbShift.append(dayName)
          .append(",")
          .append(dayPart)
          .append(",")
          .append(totalInShift)
          .append(",")
          .append(shiftLength)
          .append(",")
          .append(totalInShift*shiftLength*cooksHourlyRate)
          .append(",")
          .append(totalOvertime*cooksHourlyRateOvertime)
          .append(",")
          .append(totalInShift*shiftLength*cooksHourlyRate + totalOvertime*cooksHourlyRateOvertime)
          .append(System.lineSeparator());
        
        */
        DataItem item = cookIdleChartItems.get(key);

        if (item == null) {
            item = new DataItem();
            item.setValue(averageIdle);
            cookIdleChartItems.put(key, item);

            cookIdleChart.addDataItem(item, key, dayPartColors.get(dayPart));
        } else {
            item.setValue(averageIdle);
        }
        
        if(dayPart == DayPartUtil.DayPart.CLOSED){
	        sbOvertime.append(dayName)
	          .append(",")
	          .append(dayPart)
	          .append(",")
	          .append(totalOvertime)
	          .append(",")
	          .append(avgOvertime)
	          .append(System.lineSeparator());
	          
	          item = cookOvertimeChartItems.get(key);
	
	        if (item == null) {
	            item = new DataItem();
	            item.setValue(totalOvertime);
	            cookOvertimeChartItems.put(key, item);
	
	            cookOvertimeChart.addDataItem(item, key, dayPartColors.get(dayPart));
	        } else {
	            item.setValue(totalOvertime);
	        }
        }
        
        
    }
}


cookOvertimeCsv = sbOvertime.toString();
cookIdleCsv = sbIdle.toString();
cookIdleText.setText(sbIdle.toString());
cookOvertimeText.setText(sbOvertime.toString());
//cooksShiftText.setText(sbShift.toString());

/*ALCODEEND*/}

double registerTablesUsage()
{/*ALCODESTART::1780906745364*/
int insideUsage = 0;
int outsideUsage = 0;
int barUsage = 0;

for (Table t : insideTables) {
    if (!t.isFree) {
        insideUsage++;
    }
}
for (Table t : outsideTables) {
    if (!t.isFree) {
        outsideUsage++;
    }
}
for (Table t : barTables) {
    if (!t.isFree) {
        barUsage++;
    }
}

TimeEvent ev = new TimeEvent(date(), date(), TimeEvent.EventType.TABLES_USAGE_OUTSIDE, outsideUsage);
timeEvents.add(ev);

ev = new TimeEvent(date(), date(), TimeEvent.EventType.TABLES_USAGE_INSIDE, insideUsage);
timeEvents.add(ev);

ev = new TimeEvent(date(), date(), TimeEvent.EventType.TABLES_USAGE_BAR, barUsage);
timeEvents.add(ev);
/*ALCODEEND*/}

double tablesEventsUpdater()
{/*ALCODESTART::1780907226546*/
StringBuilder sb = new StringBuilder();

sb.append("weekday,daypart,inside,outside,bar")
  .append(System.lineSeparator());

for (int day = Calendar.SUNDAY; day <= Calendar.SATURDAY; day++) {
	
	String dayName = dayToString(day);

    for (DayPartUtil.DayPart dayPart : DayPartUtil.DayPart.values()) {
        Double totalInside = timeEvents.getValueAvgByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.TABLES_USAGE_INSIDE, openingTime) / insideTables.size() * 100;
        Double totalOutside = timeEvents.getValueAvgByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.TABLES_USAGE_OUTSIDE, openingTime)/ outsideTables.size() *100;
        Double totalBar = timeEvents.getValueAvgByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.TABLES_USAGE_BAR, openingTime)/ barTables.size() * 100;
		
		if(totalInside.isNaN() || totalInside == 0.0) totalInside = -1.0;
		if(totalOutside.isNaN() || totalOutside== 0.0) totalOutside = -1.0;
		if(totalBar.isNaN() || totalBar == 0.0 ) totalBar = -1.0;
		
		
        String key = dayName + "-" + dayPart;

        sb.append(dayName)
          .append(",")
          .append(dayPart)
          .append(",")
          .append(totalInside)
          .append(",")
          .append(totalOutside)
          .append(",")
          .append(totalBar)
          .append(System.lineSeparator());
          
        /* 
        DataItem item = tablesChartItems.get(key);

        if (item == null) {
            item = new DataItem();
            item.setValue(total);
            paymentChartItems.put(key, item);

            paymentEventsChart.addDataItem(item, key, dayPartColors.get(dayPart));
        } else {
            item.setValue(total);
        }
        */
        
        
        DataItem insideTablesItem = insideTablesChartItems.get(key);
        if (insideTablesItem == null) {
            insideTablesItem = new DataItem();
            insideTablesItem.setValue(totalInside);
            insideTablesChartItems.put(key, insideTablesItem);
            tablesUsageChart.addDataItem(insideTablesItem, key + " - Inside", tablesAreaColors.get(TimeEvent.EventType.TABLES_USAGE_INSIDE));
        } else {
            insideTablesItem.setValue(totalInside);
        }

        DataItem outsideTablesItem = outsideTablesChartItems.get(key);
        if (outsideTablesItem == null) {
            outsideTablesItem = new DataItem();
            outsideTablesItem.setValue(totalOutside);
            outsideTablesChartItems.put(key, outsideTablesItem);
            tablesUsageChart.addDataItem(outsideTablesItem, key + " - Outside", tablesAreaColors.get(TimeEvent.EventType.TABLES_USAGE_OUTSIDE));
        } else {
            outsideTablesItem.setValue(totalOutside);
        }

        DataItem barTablesItem = barTablesChartItems.get(key);
        if (barTablesItem == null) {
            barTablesItem = new DataItem();
            barTablesItem.setValue(totalBar);
            barTablesChartItems.put(key, barTablesItem);
            tablesUsageChart.addDataItem(barTablesItem, key + " - Bar", tablesAreaColors.get(TimeEvent.EventType.TABLES_USAGE_BAR));
        } else {
            barTablesItem.setValue(totalBar);
        } 
    }
    
    String spacerKey = dayName +"-SPACER";
        if (day < Calendar.SATURDAY) {
		
		    DataItem spacerItem = spacerTablesChartItems.get(spacerKey);
		
		    if (spacerItem == null) {
		        spacerItem = new DataItem();
		        spacerItem.setValue(100);
		        spacerTablesChartItems.put(spacerKey, spacerItem);
		
		        tablesUsageChart.addDataItem(spacerItem, spacerKey, Color.BLACK);
		    } else {
		        spacerItem.setValue(100);
		    }
		}
}

tablesUsageCsv = sb.toString();
tableEventsText.setText(sb.toString());
/*ALCODEEND*/}

double registerCustomersServedEvent(Date start,Date end,double amount)
{/*ALCODESTART::1780937675928*/
TimeEvent ev = new TimeEvent(start, end, TimeEvent.EventType.CUSTOMERS_SERVED, amount);
timeEvents.add(ev);
/*ALCODEEND*/}

double registerWaiterOvertimeEvent(Date start,Date end,double amount)
{/*ALCODESTART::1780995917002*/
TimeEvent ev = new TimeEvent(start, end, TimeEvent.EventType.WAITER_OVERTIME, amount);
timeEvents.add(ev);
/*ALCODEEND*/}

double registerCookOvertimeEvent(Date start,Date end,double amount)
{/*ALCODESTART::1781000084410*/
TimeEvent ev = new TimeEvent(start, end, TimeEvent.EventType.COOK_OVERTIME, amount);
timeEvents.add(ev);
/*ALCODEEND*/}

int getMaxCooks()
{/*ALCODESTART::1781003606894*/
int maxCooks = 0;
for(double t = 0; t < 7 * 24; t += 1) { 
    int val = cooksSchedule.getValue(t, HOUR); 
    if(val > maxCooks) {
        maxCooks = val;
    }
}

return maxCooks;
/*ALCODEEND*/}

double tablesInit()
{/*ALCODESTART::1781278926240*/

/*
for(int size : insideTablesSizes){
	Table t = add_insideTables();
	t.size = size;
}

for(int size : outsideTablesSizes){
	Table t = add_outsideTables();
	t.size = size;
}

for(int size : barTablesSizes){
	Table t = add_barTables();
	t.size = size;
}
*/
/*
Table t = (Table) unit;
int idx = nextInsideTableIdx;

if (idx >= 0 && idx < insideTablesSizes.length) {
    t.size = insideTablesSizes[idx];
}

nextInsideTableIdx++;


/*
Table t = (Table) unit;
int idx = nextOutsideTableIdx;

if (idx >= 0 && idx < outsideTablesSizes.length) {
    t.size = outsideTablesSizes[idx];
}

nextOutsideTableIdx++;


Table t = (Table) unit;
int idx = nextBarTableIdx;

if (idx >= 0 && idx < barTablesSizes.length) {
    t.size = barTablesSizes[idx];
}

nextBarTableIdx++;

*/
/*ALCODEEND*/}

double registerWaitersShiftEvent(Date start,Date end,double amount)
{/*ALCODESTART::1781292445708*/
TimeEvent ev = new TimeEvent(start, end, TimeEvent.EventType.WAITERS_IN_SHIFT, amount);
timeEvents.add(ev);
/*ALCODEEND*/}

double registerCooksShiftEvent(Date start,Date end,double amount)
{/*ALCODESTART::1781292549595*/
TimeEvent ev = new TimeEvent(start, end, TimeEvent.EventType.COOKS_IN_SHIFT, amount);
timeEvents.add(ev);
/*ALCODEEND*/}

double moneyEventsUpdater()
{/*ALCODESTART::1781357683305*/
StringBuilder sbShift = new StringBuilder();

sbShift.append("weekday,daypart,waitersInShift,waitersHours,waitersSalary,waitersOvertimeSalary,waitersTotalSalary,cooksInShift,cooksHours,cooksSalary,cooksOvertimeSalary,cooksTotalSalary,earned,netProfit,cumulativeNetProfit")
  .append(System.lineSeparator());

double totalWaiters = 0.0;
double totalCooks = 0.0;
double totalEarned = 0.0;
double totalSpent = 0.0;

int[] weekDays = {
    Calendar.MONDAY,
    Calendar.TUESDAY,
    Calendar.WEDNESDAY,
    Calendar.THURSDAY,
    Calendar.FRIDAY,
    Calendar.SATURDAY,
    Calendar.SUNDAY
};

for (int day : weekDays) {
    for (DayPartUtil.DayPart dayPart : DayPartUtil.DayPart.values()) {
    
        double totalInShiftWaiters = timeEvents.getValueSumByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.WAITERS_IN_SHIFT, openingTime);
		double shiftLengthWaiters = timeEvents.getAverageTimeByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.WAITERS_IN_SHIFT, openingTime) / 60;
		double totalOvertimeWaiters = timeEvents.getValueSumByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.WAITER_OVERTIME, openingTime);
		
		double totalOvertimeCooks = timeEvents.getValueSumByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.COOK_OVERTIME, openingTime);
		double totalInShiftCooks = timeEvents.getValueSumByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.COOKS_IN_SHIFT, openingTime);
		double shiftLengthCooks = timeEvents.getAverageTimeByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.COOKS_IN_SHIFT, openingTime) / 60;
		
		double earned = timeEvents.getValueSumByWeekdayAndDayPart(day, dayPart, TimeEvent.EventType.MONEY, openingTime);
		
		double shiftPayWaiters = totalInShiftWaiters*shiftLengthWaiters*waitersHourlyRate;
		double overtimePayWaiters = totalOvertimeWaiters*waitersHourlyRateOvertime;
		
		double shiftPayCooks = totalInShiftCooks*shiftLengthCooks*cooksHourlyRate;
		double overtimePayCooks = totalOvertimeCooks*cooksHourlyRateOvertime;
		
		double spent = shiftPayWaiters + overtimePayWaiters + shiftPayCooks + overtimePayCooks;
		
		
		
		totalEarned += earned;
        totalSpent += spent;
        totalWaiters += shiftPayWaiters + overtimePayWaiters;
        totalCooks += shiftPayCooks + overtimePayCooks;
        
        
		String dayName = dayToString(day);
        			String key = dayName + "-" + dayPart;

        sbShift.append(dayName)
          .append(",")
          .append(dayPart)
          .append(",")
          .append(totalInShiftWaiters)
          .append(",")
          .append(shiftLengthWaiters)
          .append(",")
          .append(shiftPayWaiters)
          .append(",")
          .append(overtimePayWaiters)
          .append(",")
          .append(shiftPayWaiters + overtimePayWaiters)
          .append(",")
          
          .append(totalInShiftCooks)
          .append(",")
          .append(shiftLengthCooks)
          .append(",")
          .append(shiftPayCooks)
          .append(",")
          .append(overtimePayCooks)
          .append(",")
          .append(shiftPayCooks + overtimePayCooks)
          
          .append(",")
          .append(earned)
          
          .append(",")
          .append(earned - spent)
          
          .append(",")
          .append(totalEarned - totalSpent - fixedCosts)
          
          .append(System.lineSeparator());
        
        
    }
}


moneyCsv = sbShift.toString();
shiftText.setText(sbShift.toString());


shiftTotalText.setText(
	"Waiters total: " + totalWaiters
	+ "€\nCooks total: "+ totalCooks
	+ "€\nSalaries total: "+ (totalWaiters + totalCooks) 
	+ "€\nTotal expenses: "+ (totalWaiters + totalCooks + fixedCosts) 
	+ "€\nTotal earned: " + totalEarned 
	+ "€\nNet profit: "+ (totalEarned - (totalWaiters + totalCooks + fixedCosts))+ "€"
);
/*ALCODEEND*/}

double saveCurrentResults(int currentRunCount)
{/*ALCODESTART::1781700898927*/
statisticsUpdater();

java.nio.file.Path baseFolder = java.nio.file.Paths.get(simulationFolder, simulationName);

traceln("Results folder: " + baseFolder.toString());
traceln("Run: " + currentRunCount);
try {
        java.nio.file.Files.createDirectories(baseFolder);


		// Customers 
        java.nio.file.Files.writeString(
            baseFolder.resolve("customersServed_"+currentRunCount+".csv"),
            customersServedCsv,
            java.nio.file.StandardOpenOption.CREATE,
            java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
        );

        java.nio.file.Files.writeString(
            baseFolder.resolve("customerWaitPay_"+currentRunCount+".csv"),
            customerWaitPayCsv,
            java.nio.file.StandardOpenOption.CREATE,
            java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
        );
        
        java.nio.file.Files.writeString(
            baseFolder.resolve("customerWaitTable_"+currentRunCount+".csv"),
            customerWaitTableCsv,
            java.nio.file.StandardOpenOption.CREATE,
            java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
        );
        
        java.nio.file.Files.writeString(
            baseFolder.resolve("customerWaitEntrance_"+currentRunCount+".csv"),
            customerWaitEntranceCsv,
            java.nio.file.StandardOpenOption.CREATE,
            java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
        );
        
        java.nio.file.Files.writeString(
            baseFolder.resolve("customerStay_"+currentRunCount+".csv"),
            customerStayCsv,
            java.nio.file.StandardOpenOption.CREATE,
            java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
        );

        // Tables
        
        java.nio.file.Files.writeString(
            baseFolder.resolve("tablesUsage_"+currentRunCount+".csv"),
            tablesUsageCsv,
            java.nio.file.StandardOpenOption.CREATE,
            java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
        );

        // Payments
        
        java.nio.file.Files.writeString(
            baseFolder.resolve("payment_"+currentRunCount+".csv"),
            paymentCsv,
            java.nio.file.StandardOpenOption.CREATE,
            java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
        );

        // Waiters
        java.nio.file.Files.writeString(
            baseFolder.resolve("waiterOvertime_"+currentRunCount+".csv"),
            waiterOvertimeCsv,
            java.nio.file.StandardOpenOption.CREATE,
            java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
        );
        
        java.nio.file.Files.writeString(
            baseFolder.resolve("waiterIdle_"+currentRunCount+".csv"),
            waiterIdleCsv,
            java.nio.file.StandardOpenOption.CREATE,
            java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
        );


        // Cooks

        java.nio.file.Files.writeString(
            baseFolder.resolve("cookOvertime_"+currentRunCount+".csv"),
            cookOvertimeCsv,
            java.nio.file.StandardOpenOption.CREATE,
            java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
        );
        
        java.nio.file.Files.writeString(
            baseFolder.resolve("cookIdle_"+currentRunCount+".csv"),
            cookIdleCsv,
            java.nio.file.StandardOpenOption.CREATE,
            java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
        );

        // Money

        java.nio.file.Files.writeString(
            baseFolder.resolve("money_"+currentRunCount+".csv"),
            moneyCsv,
            java.nio.file.StandardOpenOption.CREATE,
            java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
        );
        
    } catch (Exception e) {
        error("Errore init file: " + e.getMessage());
    }

    
    
/*ALCODEEND*/}

double statisticsUpdater()
{/*ALCODESTART::1781785081576*/
paymentEventsUpdater();
customerEventsUpdater();
waiterEventsUpdater();
cooksEventsUpdater();
tablesEventsUpdater();
moneyEventsUpdater();
/*ALCODEEND*/}

