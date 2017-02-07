#!/usr/bin/python3.5
# coding:utf-8

import tushare as ts
import os
import time
import datetime
from concurrent.futures import ThreadPoolExecutor
import getopt
import sys

project_dir = os.path.dirname(os.path.abspath(__file__))
executor = ThreadPoolExecutor(max_workers=16)


def download_stocks():
    start = time.clock()
    df = ts.get_stock_basics()
    df.to_csv("/data/stocks.csv")
    end = time.clock()
    print("read: %f s" % (end - start))


def download_ktype_data(ktype="D"):
    """
    date    日期和时间低频数据时为：YYYY-MM-DD,高频数为：YYYY-MM-DD HH:MM
    open    开盘价
    close   收盘价
    high    最高价
    low     最低价
    volume  成交量
    code    证券代码
    """
    ktype_map = {"5": "5", "15": "15", "30": "30", "60": "60", "D": "day", "W": "week", "M": "month"}

    columns = ts.get_stock_basics().index
    start = time.clock()
    values = []
    for code in columns:
        future = executor.submit(__download, code, ktype, ktype_map[ktype])
        values.append(future)
    for result in values:
        result.result()
    end = time.clock()
    print("read: %f s" % (end - start))


def download_index_ktype_data():
    symbols = ts.get_index().code
    values = []
    start = time.clock()
    for code in symbols:
        future = executor.submit(__download, code, "D", "index")
        values.append(future)
    for result in values:
        result.result()
    end = time.clock()
    print("save index: %f s" % (end - start))


def __download(code, type, path, start, end):
    file = "/data/" + path + "/" + code + ".csv"
    print(file)
    if path == "index":
        df = ts.get_k_data(code, ktype=type, index="true", autype="qfq", start=start, end=end)
        __write_csv(df, file)
    else:
        df = ts.get_k_data(code, ktype=type, autype="qfq", start=start, end=end)
        __write_csv(df, file)
    return code


def __write_csv(df, file):
    if os.path.exists(file):
        df.to_csv(file, index=False, header=None, mode='a')
    else:
        df.to_csv(file, index=False)


def main():
    try:
        opts, args = getopt.getopt(sys.argv[1:], "h", ["help"])
    except getopt.error:
        print(getopt.error.msg)
        print("for help use --help")
    for o, a in opts:
        if o in ("-h", "--help"):
            print(__doc__)
    for arg in args:
        if arg == "stocks":
            download_stocks()
        if arg == "ktype":
            download_ktype_data()
        if arg == "index":
            download_index_ktype_data()
        if arg == "all":
            download_stocks()
            download_ktype_data()


if __name__ == "__main__":
    main()
