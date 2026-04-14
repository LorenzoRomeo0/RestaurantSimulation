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

