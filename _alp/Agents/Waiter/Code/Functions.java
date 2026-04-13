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

