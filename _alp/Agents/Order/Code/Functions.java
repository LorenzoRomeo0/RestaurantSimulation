String toString()
{/*ALCODESTART::1777022491572*/
return "menuItems = \n" + menuItems + " \n";
/*ALCODEEND*/}

double getTotalPrice()
{/*ALCODESTART::1777058537593*/
double total = 0.0;

if (menuItems == null) {
    return total;
}

for (MenuItem item : menuItems) {
    if (item != null) {
        total += item.price;
    }
}

return total + main.coverCharge;
/*ALCODEEND*/}

