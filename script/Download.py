#!/usr/bin/python
# coding:utf-8

import tushare as ts
import os

project_dir = os.path.dirname(os.path.abspath(__file__))

df = ts.get_stock_basics()
df.to_csv(project_dir + "/../data/stocks.csv")