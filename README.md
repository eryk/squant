# TODO 
1. 计算和展示portfolio
2. 接入外汇数据
3. 验证在外汇上做回测
4. 更新wiki，介绍数据更新及使用方法

# 功能介绍

### 数据自动采集与更新

#### 股票数据:

* 股票基本信息数据
* 股票历史逐笔数据
* 股票和指数k线数据（5分钟、15分钟、30分钟、60分钟、日线）
* 股票和板块资金流数据
* 股票分类数据（行业、概念、地区）
* 财务报表数据
* 龙虎榜数据（开发中）

#### 外汇数据:

* EUR/USD
* GBP/USD
* USD/JPA
* USD/CHF
* AUD/USD
* USD/CAD
* NZD/USD
* EUR/GBP

> 注意：数据更新支持全量更新好增量更新，具体命令参考 **快速上手**

### 回测引擎

通过继承策略类 **com.squant.cheetah.strategy.Strategy** 并实现 **handle()** 函数来开发策略
```scala
abstract class Strategy(context: Context, broker: Broker) extends LazyLogging with Broker {
  def init()

  def handle()
```

常用策略示例请参考[examples目录](https://github.com/eryk/squant/tree/master/src/main/scala/com/squant/cheetah/examples)

#### 可视化的图表分析工具

TODO

### 丰富的交易接口支持
 
# 快速上手

## 定时下载股票数据到指定目录

1. 创建数据保存的目录
> sudo mkdir /data

2. 拉取squant的docker镜像
> sudo docker pull registry.cn-hangzhou.aliyuncs.com/eryk/squant:1.1

3. 启动squant镜像，任务会定时下载各类股票数据
> sudo docker run --name squant -v /data:/data -d registry.cn-hangzhou.aliyuncs.com/eryk/squant:1.1

注意：数据更新任务配置文件[点这里](https://github.com/eryk/squant/blob/master/src/main/resources/application.conf)

4. 手动执行下载任务
```
sudo docker exec -t -i **CONTAINER_ID** /bin/bash
cd /home/squant/
java -cp squant-assembly-1.1.jar:conf com.squant.cheetah.Main

squant 1.x
Usage: squant [source|test|run] [options]

  -h, --help               display this help and exit
Command: source [options]
init or download data source
  -t, --type <value>       set datasource to be downloaded,
							support: 
								all: all kind of datasource,
								basic: stock list with basic info,
								category: stock category data,
								daily: stock day bar,
								finance: Financial Statements,
								minute: stock minute bar,include 5/15/30/60 minutes,
								moneyflow: stock money flow data,
								tick: stock tick data
  --init <value>           if true,init data from source, default:false
  --start <value>          format:yyyyMMdd, set download start time
  --stop <value>           format:yyyyMMdd, set download stop time
  --clear <value>          if true,clean data dir before download, default:true
Command: test

Command: run
```

例如：

1. 下载股票基本信息数据
> java -cp squant-assembly-1.1.jar:conf com.squant.cheetah.Main source -t basic --init true

2. 下载日线级别股票数据可以执行如下命令
> java -cp squant-assembly-1.1.jar:conf com.squant.cheetah.Main source -t daily -init true
