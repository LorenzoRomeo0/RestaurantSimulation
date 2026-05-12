double menuCopulaGeneratorTextUpdater()
{/*ALCODESTART::1777449595901*/
CurrentCupulasTextBREAKFAST.setText(
	menuCopulaGenerator.modelPrettyString(
		DayPartUtil.DayPart.BREAKFAST, 
		menuCopulaGenerator.getCopulaModel(
			DayPartUtil.DayPart.BREAKFAST
		)
	)
);
CurrentCupulasTextLUNCH.setText(
	menuCopulaGenerator.modelPrettyString(
		DayPartUtil.DayPart.LUNCH, 
		menuCopulaGenerator.getCopulaModel(
			DayPartUtil.DayPart.LUNCH
		)
	)
);
CurrentCupulasTextBREAK.setText(
	menuCopulaGenerator.modelPrettyString(
		DayPartUtil.DayPart.BREAK, 
		menuCopulaGenerator.getCopulaModel(
			DayPartUtil.DayPart.BREAK
		)
	)
);
CurrentCupulasTextDINNER.setText(
	menuCopulaGenerator.modelPrettyString(
		DayPartUtil.DayPart.DINNER, 
		menuCopulaGenerator.getCopulaModel(
			DayPartUtil.DayPart.DINNER
		)
	)
);

/*ALCODEEND*/}

double init()
{/*ALCODESTART::1777450118608*/
menuCopulaGeneratorTextUpdater();

traceln(java.nio.file.Paths.get("orders_history/synthetic_orders.csv").toAbsolutePath());
readOrdersDebugTextUpdater();
/*ALCODEEND*/}

double readOrdersDebugTextUpdater()
{/*ALCODESTART::1777454493180*/
readOrdersDebugText.setText(
	"Loading..."
);

List<MenuCopulaUpdater.SyntheticOrder> orders = menuCopulaUpdater.loadOrdersFromCsv("orders_history/synthetic_orders.csv");

readOrdersDebugText.setText(
	orders.subList(0,20).stream()
          .map(MenuCopulaUpdater.SyntheticOrder::toString)
          .collect(java.util.stream.Collectors.joining(System.lineSeparator()))
    +"\n... (+other " + (orders.size()-20) +")"
);


traceln(menuCopulaUpdater.loadOrdersFromCsv(csvFile));

/*
Map<String, MenuCopulaUpdater.CopulaPreview> preview = menuCopulaUpdater.dryRunCopulaTables(orders);
CopulaDryRunBREAKFAST.setText(
	menuCopulaUpdater.dryRunPrettyStringForDayPart("BREAKFAST", orders)
);
*/

MenuCopulaGenerator previewGenerator = new MenuCopulaGenerator(this, false);

CopulaDryRunTextBREAKFAST.setText(
    menuCopulaUpdater.dryRunPrettyString(
        DayPartUtil.DayPart.BREAKFAST,
        orders,
        previewGenerator
    )
);

CopulaDryRunTextLUNCH.setText(
    menuCopulaUpdater.dryRunPrettyString(
        DayPartUtil.DayPart.LUNCH,
        orders,
        previewGenerator
    )
);

CopulaDryRunTextBREAK.setText(
    menuCopulaUpdater.dryRunPrettyString(
        DayPartUtil.DayPart.BREAK,
        orders,
        previewGenerator
    )
);

CopulaDryRunTextDINNER.setText(
    menuCopulaUpdater.dryRunPrettyString(
        DayPartUtil.DayPart.DINNER,
        orders,
        previewGenerator
    )
);

traceln("Finished loading from CSV");


/*ALCODEEND*/}

