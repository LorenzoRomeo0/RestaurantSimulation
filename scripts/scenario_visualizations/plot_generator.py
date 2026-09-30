from pathlib import Path
import re

import matplotlib.pyplot as plt
import pandas as pd

ROOT_DIR = Path(__file__).resolve().parent

DAY_ORDER = [
    "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY",
    "FRIDAY", "SATURDAY", "SUNDAY",
]
DAYPART_ORDER = ["BREAKFAST", "LUNCH", "BREAK", "DINNER", "CLOSED"]


def scenario_number(path: Path) :
    match = re.search(r"scenario(\d+)", path.parent.name, re.IGNORECASE)
    return int(match.group(1)) if match else 0

def read_csv(path: Path):
    df = pd.read_csv(path, sep=";", decimal=",", encoding="utf-8-sig")
    df.columns = [str(column).strip().lower() for column in df.columns]
    return df

def prepare_data(df: pd.DataFrame):
    df = df.copy()

    if "start_timestamp" in df.columns:
        start = pd.to_datetime(df["start_timestamp"], dayfirst=True, errors="coerce")
        if "weekday" not in df.columns:
            df["weekday"] = start.dt.day_name()

    if "period" in df.columns and "daypart" not in df.columns:
        df["daypart"] = df["period"]

    if "weekday" in df.columns:
        df["weekday"] = df["weekday"].astype(str).str.upper()

    if "daypart" in df.columns:
        df["daypart"] = df["daypart"].astype(str).str.upper()
        df["daypart"] = df["daypart"].replace({"MORNING": "BREAKFAST"})

    return df

def numeric_columns(df: pd.DataFrame):
    excluded = {
        "start_timestamp", "end_timestamp", "weekday", "daypart", "period",
        "hour", "scenario", "source_file",
    }

    columns = []
    for column in df.columns:
        if column in excluded:
            continue
        values = pd.to_numeric(df[column], errors="coerce")
        if values.notna().any():
            df[column] = values
            columns.append(column)

    return columns

def plot_by_period(df: pd.DataFrame, metric: str, scenario: str, stem: str, output_dir: Path):
    if "daypart" not in df.columns:
        return

    data = df[["daypart", metric]].dropna().copy()
    if data.empty:
        return

    data["daypart"] = pd.Categorical(data["daypart"], DAYPART_ORDER, ordered=True)
    data = data.sort_values("daypart")

    plt.figure(figsize=(8, 5))
    plt.plot(
        data["daypart"].astype(str),
        data[metric],
        marker="o",
    )

    plt.title(f"{metric} - {scenario}")
    plt.xlabel("Daypart")
    plt.ylabel(metric)
    plt.grid(axis="y", alpha=0.3)
    plt.xticks(rotation=35)
    plt.tight_layout()
    plt.savefig(output_dir / f"{stem}_{metric}_by_period.png", dpi=200)
    plt.close()


def plot_by_weekday_and_daypart(
    df: pd.DataFrame, metric: str, scenario: str, stem: str, output_dir: Path
) -> None:
    if not {"weekday", "daypart"}.issubset(df.columns):
        return

    data = df[["weekday", "daypart", metric]].dropna().copy()
    if data.empty:
        return

    data["weekday"] = pd.Categorical(data["weekday"], DAY_ORDER, ordered=True)
    data["daypart"] = pd.Categorical(data["daypart"], DAYPART_ORDER, ordered=True)

    plt.figure(figsize=(10, 5))

    for daypart, group in data.groupby("daypart", observed=True):
        group = group.sort_values("weekday")
        plt.plot(
            group["weekday"].astype(str),
            group[metric],
            marker="o",
            label=daypart.title(),
        )

    plt.title(f"{metric} - {scenario}")
    plt.xlabel("Weekday")
    plt.ylabel(metric)
    plt.legend(title="Daypart")
    plt.grid(axis="y", alpha=0.3)
    plt.tight_layout()
    plt.savefig(output_dir / f"{stem}_{metric}.png", dpi=200)
    plt.close()


def plot_by_hour(df: pd.DataFrame, metric: str, scenario: str, stem: str, output_dir: Path):
    if not {"weekday", "hour"}.issubset(df.columns):
        return

    # Salta cook/waiters by hour
    if metric in {"cooks", "waiters"}:
        return

    data = df[["weekday", "hour", metric]].dropna().copy()
    if data.empty:
        return

    data["weekday"] = pd.Categorical(data["weekday"], DAY_ORDER, ordered=True)
    data["hour"] = data["hour"].astype(str)

    plt.figure(figsize=(11, 5))

    for weekday, group in data.groupby("weekday", observed=True):
        group = group.sort_values("hour")
        plt.plot(group["hour"], group[metric], marker="o", label=weekday.title())

    plt.title(f"{metric} by hour - {scenario}")
    plt.xlabel("Hour")
    plt.ylabel(metric)
    plt.legend(title="Weekday", ncol=2)
    plt.grid(axis="y", alpha=0.3)
    plt.xticks(rotation=45)
    plt.tight_layout()
    plt.savefig(output_dir / f"{stem}_{metric}_by_hour.png", dpi=200)
    plt.close()


def main():
    scenario_dirs = sorted(
        (path for path in ROOT_DIR.glob("scenario*") if path.is_dir()),
        key=scenario_number,
    )

    if not scenario_dirs:
        raise FileNotFoundError("No folders found: ./scenario1, ./scenario2, ...")

    for scenario_dir in scenario_dirs:
        output_dir = scenario_dir / "plots"
        output_dir.mkdir(exist_ok=True)

        for path in scenario_dir.glob("*.csv"):
            df = prepare_data(read_csv(path))
            metrics = numeric_columns(df)

            for metric in metrics:
                if "eating" in path.stem.lower():
                    plot_by_period(df, metric, scenario_dir.name, path.stem, output_dir)
                else:
                    plot_by_weekday_and_daypart(
                        df, metric, scenario_dir.name, path.stem, output_dir
                    )
                    plot_by_hour(df, metric, scenario_dir.name, path.stem, output_dir)

        print(f"Plots for {scenario_dir.name} saved in: {output_dir}")


if __name__ == "__main__":
    main()