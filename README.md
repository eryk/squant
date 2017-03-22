# TODO 

1. 指数和个股分钟级别k线数据存储与读取
2. 定时更新任务，每天更新
3. DataEngine支持hbase数据读取

# 功能介绍

### 常用股票数据自动采集与更新

* 股票基本信息数据
* 股票历史逐笔数据
* 股票k线数据（5分钟、15分钟、30分钟、60分钟、日线）
* 股票资金流数据 

### 高性能回测引擎与可视化的图表分析工具

### 丰富的交易接口支持
 
# 快速上手

## 下载股票数据到指定目录

sudo mkdir /data

sudo docker pull registry.cn-hangzhou.aliyuncs.com/eryk/squant

sudo docker run --name squant -v /data:/data -d registry.cn-hangzhou.aliyuncs.com/eryk/squant:1.0

# changelog

## 2017-03-20

1. squant支持HBase存储接口
2. 数据源添加toCSV和toDB方法，数据源可以导出成csv文件（无增量）和写入hbase（增量更新）

## 2017-03-12

1. 生成docker镜像，支持股票数据批量下载

## 2017-03-11

1. 修复分钟和日线数据数据不准的bug
2. DataEngine支持日线和分钟数据读取

## 2017-03-09
1. 增加分钟数据源MinuteKTypeDataSource
2. 增加日线数据DailyKTypeDataSource
3. 增加股票基本信息数据StockBasicsSource
