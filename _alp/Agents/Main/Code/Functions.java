int tablesMaxSize()
{/*ALCODESTART::1769810442527*/
int maxSize = 0;

for(Table t : insideTablesPool){
	if(t.size > maxSize)maxSize = t.size;
}

for(Table t : outsideTablesPool){
	if(t.size > maxSize)maxSize = t.size;
}

for(Table t : barTablesPool){
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

double eatingTimeModel(int groupSize)
{/*ALCODESTART::1772007563909*/
//Funzione che modella il tempo impiegato a mangiare secondo la time expansion hypothesis
//Potrebbe essere migliorata in quanto nei pranzi con i colleghi generalmente non si chiacchiera così tanto.
//Di più con amici e parenti (cena e weekends)

double baseMeanTime = eating_time_schedule.getValue();

double minTime = lognormal(baseMeanTime, baseMeanTime*0.2, 5);

double maxIncrease = 0.8; //percentuale di tempo aggiunto massima

double growth = 0.35;

double multiplier = 1 + maxIncrease * (1 - Math.exp(-growth * (groupSize - 1)));

return baseMeanTime * multiplier;


/*ALCODEEND*/}

