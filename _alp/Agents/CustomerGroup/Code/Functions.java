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

return order;


/*ALCODEEND*/}

