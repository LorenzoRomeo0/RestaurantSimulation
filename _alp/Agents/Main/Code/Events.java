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
/*ALCODEEND*/}

