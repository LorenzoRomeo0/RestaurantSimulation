import pandas as pd
import numpy as np

# ============================================================
# Build AnyLogic-ready copula CSVs starting from synthetic_orders_copula.csv
# ============================================================
# Input expected:
#   synthetic_orders_copula.csv
# Output generated:
#   copula_<daypart>_marginals_from_synth.csv
#   copula_<daypart>_correlation_from_synth.csv
#   copula_menu_marginals_from_synth.csv
#
# Notes:
# - Correlations are Pearson correlations on binary variables (phi coefficients).
# - CSV format is aligned with the files you already used in AnyLogic.
# ============================================================

INPUT_FILE = 'synthetic_orders.csv'
DAYPART_ORDER = ['BREAKFAST', 'BREAK', 'LUNCH', 'DINNER']
OUTPUT_PREFIX = '.'


def detect_item_columns(df):
    meta_cols = {'order_id', 'dayPart'}
    return [c for c in df.columns if c not in meta_cols]


def correlation_matrix_binary(df_day, cols):
    valid_cols = [c for c in cols if c in df_day.columns]
    corr = df_day[valid_cols].astype(float).corr(method='pearson')
    corr = corr.fillna(0.0)
    for c in valid_cols:
        corr.loc[c, c] = 1.0
    return corr


def save_with_blank_index_header(df, path):
    out = df.copy()
    out.index.name = ''
    out.to_csv(path)


def main():
    df = pd.read_csv(INPUT_FILE)
    item_cols = detect_item_columns(df)

    all_marginals = []

    for daypart in DAYPART_ORDER:
        df_day = df[df['dayPart'] == daypart].copy()
        cols = [c for c in item_cols if c in df_day.columns and df_day[c].notna().any()]

        # Keep only columns that have at least one positive observation or were originally present
        cols = [c for c in cols if c in df_day.columns]
        if not cols or df_day.empty:
            continue

        marg = pd.DataFrame({
            'dayPart': daypart,
            'variable': cols,
            'marginal_probability': [df_day[c].mean() for c in cols]
        })
        all_marginals.append(marg)

        corr = correlation_matrix_binary(df_day, cols)

        dp_low = daypart.lower()
        #marg_path = f'{OUTPUT_PREFIX}/copula_{dp_low}_marginals_from_synth.csv'
        corr_path = f'{OUTPUT_PREFIX}/copula_{dp_low}_correlation_from_synth.csv'

        #marg[['variable', 'marginal_probability']].to_csv(marg_path, index=False)
        save_with_blank_index_header(corr.round(6), corr_path)

    if all_marginals:
        all_marg = pd.concat(all_marginals, ignore_index=True)
        all_marg.to_csv(f'{OUTPUT_PREFIX}/copula_menu_marginals_from_synth.csv', index=False)


if __name__ == '__main__':
    main()
