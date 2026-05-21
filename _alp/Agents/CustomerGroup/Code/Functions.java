Order makeOrder()
{/*ALCODESTART::1776869263596*/
/*
OrderNew order = new OrderNew();

DayPartUtil.DayPart currentDayPart = DayPartUtil.getCurrentDayPart(this);

MenuItems items = new MenuItems(this);
ArrayList<MenuItem> currentItems = items.getItemsByDayPart(
    DayPartUtil.getCurrentDayPart(this)
);

return order;
*/




DayPartUtil.DayPart currentDayPart = DayPartUtil.getCurrentDayPart(this);

//MenuItems items = new MenuItems(this);
Order order = main.menuItems.generateOrderByDayPart(currentDayPart);
order.customerGroup = this;
order.date = date();

return order;


/*ALCODEEND*/}

String toString()
{/*ALCODESTART::1778862261565*/
return
	"id = " + getId() +"\n" +
	"entrance_time = " + entrance_time + "\n" +
	"exit_time = " + exit_time + "\n" +
	"table_wait_start_time = " + table_wait_start_time + "\n" +
	"table_wait_end_time = " + table_wait_end_time + "\n" +
	"entrance_date = " + entrance_date + "\n" +
	"exit_date = " + exit_date + "\n" +
	"table_wait_start_date = " + table_wait_start_date + "\n" +
	"table_wait_end_date = " + table_wait_end_date + "\n" +
	"size = " + size + "\n" +
	"table = " + table + "\n" +
	"currentWaiter = " + (currentWaiter == null ? "null" : currentWaiter.getId()) + "\n" +
	"celiacs = " + celiacs + "\n" +
	"entrancePriority = " + entrancePriority;
/*ALCODEEND*/}

