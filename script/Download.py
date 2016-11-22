#!/usr/bin/python
# coding:utf-8

import tushare as ts

df = ts.get_stock_basics()

df.to_csv("/home/eryk/IdeaProjects/squant/data/stocks.csv")