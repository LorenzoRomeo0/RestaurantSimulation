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

/*
Order order = new Order();
order.customerGroup = this;
order.date = date();
order.menuItems = new ArrayList<MenuItem>();

for (int i=0; i<size; i++) {
	ArrayList<MenuItem> sampledMenuItems = main.menuItems.generateOrderByDayPart(currentDayPart).menuItems;
	order.menuItems.addAll(sampledMenuItems);
}

*/

Order order = main.menuItems.generateGroupOrderByDayPart(currentDayPart, size);
order.customerGroup = this;
order.date = date();

/*
traceln("--------------------------ORDER----- "+ size);
traceln(order);
traceln("--------------------------REDRO-----");
*/

return order;


/*ALCODEEND*/}

String toString()
{/*ALCODESTART::1778862261565*/
return
	"waiter{id = " + getId() +"\n" +
	"entrance_date = " + entranceDate + "\n" +
	"table_wait_start_date = " + tableWaitStartDate + "\n" +
	"size = " + size + "\n" +
	"table = " + table + "\n" +
	"currentWaiter = " + (currentWaiter == null ? "null" : currentWaiter.getId()) + "\n" +
	"celiacs = " + celiacs + "\n" +
	"entrancePriority = " + entrancePriority + "}";
/*ALCODEEND*/}

