package com.squant.cheetah.trade

import java.time.LocalDateTime

/**
  * 持有一只股票详情
  *
  */
case class Position(stockName: String, //证券名称
                    symbol: String, //证券代码
                    ts: LocalDateTime, //更新时间戳
                    totalAmount: Int, //证券数量
                    closeableAmount: Int, //可卖数量
                    todayAmount: Double, //今买数量
                    sellAmount: Double, //今卖数量
                    avgCost: Double, //成本价
                    close: Double, //当前价
                    floatPnl: Double, //浮动盈亏
                    pnlRatio: Double, //盈亏比例
                    latestValue: Double //最新市值
                   ) {

}
