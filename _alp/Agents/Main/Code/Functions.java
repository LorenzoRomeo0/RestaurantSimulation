int insideTableMaxSize()
{/*ALCODESTART::1769810442527*/
int maxSize = 0;

for(Table t : insideTablesPool){
	if(t.size > maxSize)maxSize = t.size;
}
return maxSize;
/*ALCODEEND*/}

