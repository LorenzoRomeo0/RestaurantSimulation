#%%
import numpy as np
import pandas as pd
from datetime import datetime
import locale

try:
    locale.setlocale(locale.LC_TIME, 'en_US')
except locale.Error:
    print("Locale not found. Using default system locale.")

data = pd.read_csv("./arrival_rate_complete.csv")
# Convert your timestamp data to hours offset
start_time = datetime(1970, 1, 5, 7, 0, 0)
for index, row in data.iterrows():
    # data.at[index, 'hours_start'] = (datetime.strptime(row['start_timestamp'], '%Y-%m-%d %H:%M:%S') - start_time).total_seconds() / 3600
    # data.at[index, 'hours_end'] = (datetime.strptime(row['end_timestamp'], '%Y-%m-%d %H:%M:%S') - start_time).total_seconds() / 3600
    data.at[index, 'weekday'] = datetime.strptime(row['start_timestamp'], '%Y-%m-%d %H:%M:%S').strftime('%A')
    data.at[index, 'hour'] = datetime.strptime(row['start_timestamp'], '%Y-%m-%d %H:%M:%S').strftime('%H:%M:%S')

# data['hours_start'] = data['hours_start'].astype(int)
# data['hours_end'] = data['hours_end'].astype(int)

data.to_csv("./arrival_rate_week_converted.csv", index=False)
data
# %%
