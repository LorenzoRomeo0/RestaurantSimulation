#%%
from pathlib import Path

import matplotlib.pyplot as plt
import pandas as pd


SUMMARY_DIR = Path("./experiment3/kpi_summary_outputs")
PLOTS_DIR = SUMMARY_DIR / "plots"
PLOTS_DIR.mkdir(exist_ok=True)

DAY_ORDER = [
    "MONDAY",
    "TUESDAY",
    "WEDNESDAY",
    "THURSDAY",
    "FRIDAY",
    "SATURDAY",
    "SUNDAY",
]

DAYPART_ORDER = [
    "BREAKFAST",
    "LUNCH",
    "BREAK",
    "DINNER",
    "CLOSED",
]

IGNORE_METRICS = [
    "waitersSalary",
    "cooksSalary",

]


#%%
for file_path in SUMMARY_DIR.glob("*_summary*.csv"):
    df = pd.read_csv(file_path)

    df["weekday"] = pd.Categorical(
        df["weekday"],
        categories=DAY_ORDER,
        ordered=True,
    )

    df["daypart"] = pd.Categorical(
        df["daypart"],
        categories=DAYPART_ORDER,
        ordered=True,
    )

    for metric, data in df.groupby("metric"):

        if metric == "lastCustomerHour":
            data = data[data["mean"] > 0].copy()


        plt.figure(figsize=(10, 5))

        for daypart, group in data.groupby("daypart", observed=True):
            group = group.sort_values("weekday")

            plt.errorbar(
                group["weekday"].astype(str),
                group["mean"],
                yerr=group["ci"],
                marker="o",
                capsize=3,
                label=daypart,
            )

            # if metric == "lastCustomerHour":
            #     plt.axhline(
            #         y=23,
            #         color="gray",
            #         linestyle="--",
            #         linewidth=1.2,
            #         alpha=0.8,
            #         label="23:00"
            #     )

        plt.title(f"{metric} by weekday and daypart")
        plt.xlabel("Weekday")
        plt.ylabel(metric)
        plt.legend(title="Daypart")
        plt.grid(axis="y", alpha=0.3)
        plt.tight_layout()

        safe_metric = str(metric).replace(" ", "_")
        plt.savefig(PLOTS_DIR / f"{file_path.stem}_{safe_metric}.png", dpi=200)

        #plt.show()
        plt.close()

