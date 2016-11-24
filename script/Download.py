#!/usr/bin/python
# coding:utf-8

import tushare as ts
import os
import time
from concurrent.futures import ThreadPoolExecutor

project_dir = os.path.dirname(os.path.abspath(__file__))

start = time.clock()

df = ts.get_stock_basics()
df.to_csv("/data/stocks.csv")

end = time.clock()
print("read: %f s" % (end - start))

columns = df.index

ktype = {"5": "5", "15": "15", "30": "30", "60": "60", "D": "day", "W": "week", "M": "month"}

def download(code, type, path):
    file = "/data/" + path + "/" + code + ".csv"
    print(file)
    df = ts.get_k_data(code, ktype=type, autype="qfq")
    df.to_csv(file, header=None)
    return code


start = time.clock()

executor = ThreadPoolExecutor(max_workers=16)

values = []

for code in columns:
    for (type, path) in ktype.items():
        future = executor.submit(download, code, type, path)
        values.append(future)

for result in values:
    result.result()

end = time.clock()
print("read: %f s" % (end - start))
