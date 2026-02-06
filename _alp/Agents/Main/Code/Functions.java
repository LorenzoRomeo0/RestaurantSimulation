int insideTableMaxSize()
{/*ALCODESTART::1769810442527*/
int maxSize = 0;

for(Table t : insideTablesPool){
	if(t.size > maxSize)maxSize = t.size;
}
return maxSize;
/*ALCODEEND*/}

Table tableSelection(CustomerGroup customerGroup)
{/*ALCODESTART::1770025342170*/
/*
int n = 0;
for (Table t : insideTablesPool){
	if (t.isFree) n++;
}
for (Table t : outsideTablesPool){
	if (t.isFree) n++;
}
for (Table t : barTablesPool){
	if (t.isFree) n++;
}
System.out.println("Available tables: " + n);
*/

//usare resourcepool?

for(Table t : insideTablesPool){
	if(t.isFree && t.size >= customerGroup.size){
		t.isFree = false;
		return t;
	}
}

for(Table t : outsideTablesPool){
	if(t.isFree && t.size >= customerGroup.size){
		t.isFree = false;
		return t;
	}
}

for(Table t : barTablesPool){
	if(t.isFree && t.size >= customerGroup.size){
		t.isFree = false;
		return t;
	}
}

return null;
/*ALCODEEND*/}

