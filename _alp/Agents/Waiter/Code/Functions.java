double requestShiftEnd()
{/*ALCODESTART::1775045673368*/
endShiftRequested = true;
waiter_statechart.onChange();
/*ALCODEEND*/}

double requestShiftStart()
{/*ALCODESTART::1775045893926*/
startShiftRequested = true;
waiter_statechart.onChange();
/*ALCODEEND*/}

String toString()
{/*ALCODESTART::1778861913555*/
return
	"id = " + getId() +"\n" +
	"shiftStartDate = " + shiftStartDate + "\n" +
	"shiftEndDate = " + shiftEndDate + "\n" +
	"endShiftRequested = " + endShiftRequested + "\n" +
	"startShiftRequested = " + startShiftRequested + "\n" +
	"isOffShift = " + isOffShift + "\n" +
	"wasOvertime = " + wasOvertime + "\n" +
	"lastShiftDay = " + lastShiftDay + "\n" +
	"currentOrder = " + currentOrder + "\n" +
	"isSeized = " + isSeized + "\n" +
	"foundTable = " + foundTable + "\n" +
	"currentCustomers = " + (currentCustomers == null ? "null" : currentCustomers.getId()) + "\n";
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
/*ALCODEEND*/}

