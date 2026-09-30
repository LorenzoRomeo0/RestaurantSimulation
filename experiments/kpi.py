#%%
from __future__ import annotations

from pathlib import Path
import re

import numpy as np
import pandas as pd
from statistics import NormalDist


DATA_DIR = Path("./experiment3")
OUTPUT_DIR = DATA_DIR / "kpi_summary_outputs"
OUTPUT_DIR.mkdir(exist_ok=True)

CONFIDENCE_LEVEL = 0.95
GROUP_COLS = ["weekday", "daypart"]


#%%
def confidence_interval(values: pd.Series, confidence: float = CONFIDENCE_LEVEL) -> dict[str, float]:
    values = pd.to_numeric(values, errors="coerce").dropna()
    n = len(values)

    if n == 0:
        return {
            "n": 0,
            "mean": np.nan,
            "std": np.nan,
            "se": np.nan,
            "ci": np.nan,
            "ci_low": np.nan,
            "ci_high": np.nan,
        }

    mean = values.mean()

    if n == 1:
        return {
            "n": 1,
            "mean": mean,
            "std": np.nan,
            "se": np.nan,
            "ci": np.nan,
            "ci_low": np.nan,
            "ci_high": np.nan,
        }

    std = values.std()

    # standard error
    se = std / np.sqrt(n)

    # 100(1-alpha), errore desiderato = 1 - confidence
    alpha = 1.0 - confidence 

    # z_alpha/2
    # alpha / 2.0 = errore per entrambi i lati della distribuzione
    # 1 - alpha / 2.0 = probabilità cumulativa per il lato destro della distribuzione
    critical = float(NormalDist().inv_cdf(1.0 - alpha / 2.0))

    margin = critical * se

    return {
        "n": n,
        "mean": mean,
        "std": std,
        "se": se,
        "ci": margin,
        "ci_low": mean - margin,
        "ci_high": mean + margin,
    }


def load_kpi_runs(kpi_name: str, data_dir: Path = DATA_DIR) -> pd.DataFrame:
    """
    Esempio:
        cookIdle_0.csv
        cookIdle_1.csv
        ...
    """
    pattern = re.compile(rf"^{re.escape(kpi_name)}_(\d+)\.csv$")
    frames = []

    for path in sorted(data_dir.glob(f"{kpi_name}_*.csv")):
        match = pattern.match(path.name)

        if match is None:
            print(f"File ignorato: {path.name}")
            continue

        run_id = int(match.group(1))
        frame = pd.read_csv(path)

        frame.insert(0, "run_id", run_id)
        frames.append(frame)

    if not frames:
        raise FileNotFoundError(
            f"Nessun file trovato per KPI '{kpi_name}' in: {data_dir.resolve()}"
        )

    return pd.concat(frames, ignore_index=True)


def summarize_kpi(
    kpi_name: str,
    data_dir: Path = DATA_DIR,
    group_cols: list[str] = GROUP_COLS,
    confidence: float = CONFIDENCE_LEVEL,
) -> tuple[pd.DataFrame, pd.DataFrame]:
    """
    Restituisce:
      - overall: una riga per metrica numerica;
      - grouped: una riga per metrica × weekday × daypart.
    """
    data = load_kpi_runs(kpi_name, data_dir)

    required = {"run_id", *group_cols}
    missing = required - set(data.columns)

    if missing:
        raise ValueError(
            f"Nel KPI '{kpi_name}' mancano le colonne richieste: {sorted(missing)}"
        )

    metric_cols = [
        column
        for column in data.select_dtypes(include="number").columns
        if column != "run_id"
    ]

    if not metric_cols:
        raise ValueError(f"Nessuna colonna numerica trovata per KPI '{kpi_name}'.")

    overall_rows = []
    grouped_rows = []

    for metric in metric_cols:

        per_run_overall = data.groupby("run_id")[metric].mean()

        overall_rows.append({
            "kpi": kpi_name,
            "metric": metric,
            **confidence_interval(per_run_overall, confidence),
        })

        run_level = (
            data
            .groupby(["run_id", *group_cols], as_index=False, dropna=False)[metric]
            .mean()
        )

        for group_values, subset in run_level.groupby(group_cols, dropna=False):
            if not isinstance(group_values, tuple):
                group_values = (group_values,)

            grouped_rows.append({
                "kpi": kpi_name,
                "metric": metric,
                **dict(zip(group_cols, group_values)),
                **confidence_interval(subset[metric], confidence),
            })

    overall = pd.DataFrame(overall_rows)
    grouped = pd.DataFrame(grouped_rows)

    return overall, grouped


#%%
KPI_NAMES = [
    "cookIdle",
    "cookOvertime",
    "customersServed",
    "customerStay",
    "customerWaitEntrance",
    "customerWaitPay",
    "customerWaitTable",
    "money",
    "payment",
    "tablesUsage",
    "waiterIdle",
    "waiterOvertime",
]

for kpi_name in KPI_NAMES:
    _, grouped = summarize_kpi(kpi_name)

    # overall.to_csv(
    #     OUTPUT_DIR / f"{kpi_name}_overall_summary.csv",
    #     index=False,
    # )

    grouped.to_csv(
        OUTPUT_DIR / f"{kpi_name}_summary.csv",
        index=False,
    )

    print(
        f"{kpi_name}: "
        #f"{len(overall)} metriche, "
        f"{len(grouped)} gruppi salvati."
    )