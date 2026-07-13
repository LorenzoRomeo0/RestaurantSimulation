double requestShiftEnd()
{/*ALCODESTART::1776170435234*/
endShiftRequested = true;
cook_statechart.onChange();
/*ALCODEEND*/}

double requestShiftStart()
{/*ALCODESTART::1776170435239*/
startShiftRequested = true;
cook_statechart.onChange();
/*ALCODEEND*/}

double getTimeToPrepareOrder(ArrayList<MenuItem> orderItems)
{/*ALCODESTART::1777049298321*/
double min = 0;
double max = Double.POSITIVE_INFINITY;

if (orderItems == null || orderItems.isEmpty()) {
    return 0.0;
}

double total = 0.0;

for (MenuItem item : orderItems) {
    if (item != null) {
        double sampled = normal(min, max, item.prepTimeMeanMin, item.prepTimeSdMin);
        
        total += sampled;
    }
}

return total;

/*ALCODEEND*/}

String toString()
{/*ALCODESTART::1781009491189*/
return
	"{\nid = " + getId() + "\n" +

	"isOffShift = " + isOffShift + "\n" +
	"}";
/*ALCODEEND*/}

