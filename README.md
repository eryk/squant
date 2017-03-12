# TODO 
* 数据清洗规则模块,支持建立清洗规则机制，对数据源进行规则过滤清洗
* 股票数据建模与存储
* 常用指标计算

# 快速上手

sudo docker pull registry.cn-hangzhou.aliyuncs.com/eryk/squant
sudo docker run --name squant -v /data:/data -d registry.cn-hangzhou.aliyuncs.com/eryk/squant:1.0

# changelog

## 2017-03-12

1. 生成docker镜像，支持股票数据批量下载

## 2017-03-11

1. 修复分钟和日线数据数据不准的bug
2. DataEngine支持日线和分钟数据读取

## 2017-03-09
1. 增加分钟数据源MinuteKTypeDataSource
2. 增加日线数据DailyKTypeDataSource
3. 增加股票基本信息数据StockBasicsSource
