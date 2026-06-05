void ds_updater()
{/*ALCODESTART::1774958293584*/
int d = getDayOfWeek() - 1;
int h = getHourOfDay();

double avg = 0;
if (customer_wait_count[d][h] > 0) {
    avg = customer_wait_sum[d][h] / customer_wait_count[d][h];
}

customerWaitAvgByDay[d].add(h, avg);
/*ALCODEEND*/}

void debugTextUpdater()
{/*ALCODESTART::1775232668713*/
waitersDebugTextUpdater();
timeEventsDebugTextUpdater();
menuItemsDebugTextUpdater();
moneyDebugTextUpdater();
menuCopulaGeneratorDebugTextUpdater();
waitersPoolDebugTextUpdater();
customerGroupDebugTextUpdater();
/*ALCODEEND*/}

void createTables()
{/*ALCODESTART::1780693738790*/
// create tables

/*
for(int i: barTablesSizes){
	Table t = add_barTables();
	t.size = i;
	t.setLocationRandomInside(bar_tables_node);
    traceln("inside table " + t.getId() + " created at " + t.getX() + ", " + t.getY());
	traceln("created table of size " + i);
}

for(int i: insideTablesSizes){
	Table t = add_insideTables();
	t.size = i;

}

for(int i: outsideTablesSizes){
	Table t = add_outsideTables();
	t.size = i;
}
*/

/*ALCODEEND*/}

