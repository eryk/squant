# TODO 
1. 回测引擎设计,调研vnpy和rqalpha回测功能设计
2. 更新wiki，介绍数据更新及使用方法

# 功能介绍

### 常用股票数据自动采集与更新

* 股票基本信息数据
* 股票历史逐笔数据
* 股票和指数k线数据（5分钟、15分钟、30分钟、60分钟、日线）
* 股票和板块资金流数据
* 股票分类数据（行业、概念、地区）
* 财务报表数据
* 龙虎榜数据（开发中）

### 高性能回测引擎与可视化的图表分析工具

### 丰富的交易接口支持
 
# 快速上手

## 下载股票数据到指定目录

sudo mkdir /data

sudo docker pull registry.cn-hangzhou.aliyuncs.com/eryk/squant

sudo docker run --name squant -v /data:/data -d registry.cn-hangzhou.aliyuncs.com/eryk/squant:1.1
