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

double getTimeToPrepareOrder()
{/*ALCODESTART::1777049298321*/
ArrayList<MenuItem> items = currentOrder.menuItems;

if (items == null || items.isEmpty()) {
    return 0.0;
}

double total = 0.0;

for (MenuItem item : items) {
    if (item != null) {
        double sampled = normal(0, 999, item.prepTimeMeanMin, item.prepTimeSdMin);

        if (sampled < 0) {
            sampled = 0;
        }

        total += sampled;
    }
}

return total;

/*ALCODEEND*/}

