package com.squant.cheetah.trade

import java.time.LocalDateTime

/**
  * 持有一只股票详情
  *
  */
case class Position(stockName: String, //证券名称
                    code: String, //证券代码
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
                   )

object Position {

  def mk(code: String, amount: Int, price: Double, ts: LocalDateTime): Position = {
    Position("", code, ts, amount, 0, amount, 0, price, price, 0, 0, amount * price)
  }

  def sub(hold: Position, newPos: Position): Position = {
    Position(hold.stockName,
      hold.code,
      newPos.ts,
      hold.totalAmount - newPos.totalAmount,
      hold.closeableAmount - newPos.totalAmount,
      hold.todayAmount,
      hold.sellAmount + newPos.totalAmount,
      (hold.totalAmount * hold.avgCost - newPos.totalAmount * newPos.totalAmount)
        / (hold.totalAmount - newPos.totalAmount),
      newPos.close,
      (hold.totalAmount + newPos.totalAmount) * (hold.avgCost - newPos.close),
      (hold.totalAmount + newPos.totalAmount) * (hold.avgCost - newPos.close) / (hold.totalAmount * hold.avgCost - newPos.totalAmount * newPos.totalAmount),
      hold.totalAmount * hold.avgCost - newPos.totalAmount * newPos.totalAmount
    )
  }

  def add(hold: Position, newPos: Position): Position = {
    Position(hold.stockName,
      hold.code,
      newPos.ts,
      hold.totalAmount + newPos.totalAmount,
      hold.closeableAmount,
      hold.todayAmount + newPos.totalAmount,
      hold.sellAmount,
      (hold.totalAmount * hold.avgCost + newPos.totalAmount * newPos.totalAmount)
        / (hold.totalAmount + newPos.totalAmount),
      newPos.close,
      (hold.totalAmount + newPos.totalAmount) * (hold.avgCost - newPos.close),
      (hold.totalAmount + newPos.totalAmount) * (hold.avgCost - newPos.close) / (hold.totalAmount * hold.avgCost - newPos.totalAmount * newPos.totalAmount),
      hold.totalAmount * hold.avgCost + newPos.totalAmount * newPos.totalAmount
    )
  }
}