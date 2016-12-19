package com.squant.cheetah.trade

import java.time.LocalDateTime

/**
  * 持有一只股票详情
  *
  */
case class Position(stockName: String, //证券名称
                    symbol: String, //证券代码
                    ts: LocalDateTime, //更新时间戳
                    amount: Int, //证券数量
                    canSell: Int, //可卖数量
                    costPrice: Double, //成本价
                    floatPnl: Double, //浮动盈亏
                    pnlRatio: Double, //盈亏比例
                    latestValue: Double, //最新市值
                    close: Double, //当前价
                    buyAmount: Double, //今买数量
                    sellAmount: Double //今卖数量
                   ) {

}
