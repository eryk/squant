package com.squant.cheetah.trade

import java.time.LocalDateTime

import com.squant.cheetah.domain.Order

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
                   ) {

  def sub(pos: Position): Position = {
    Position(this.stockName,
      this.code,
      pos.ts,
      this.totalAmount - pos.totalAmount,
      this.closeableAmount - pos.totalAmount,
      this.todayAmount,
      this.sellAmount + pos.totalAmount,
      (this.totalAmount * this.avgCost - pos.totalAmount * pos.totalAmount)
        / (this.totalAmount - pos.totalAmount),
      pos.close,
      (this.totalAmount + pos.totalAmount) * (this.avgCost - pos.close),
      (this.totalAmount + pos.totalAmount) * (this.avgCost - pos.close) / (this.totalAmount * this.avgCost - pos.totalAmount * pos.totalAmount),
      this.totalAmount * this.avgCost - pos.totalAmount * pos.totalAmount
    )
  }

  def add(pos: Position): Position = {
    Position(this.stockName,
      this.code,
      pos.ts,
      this.totalAmount + pos.totalAmount,
      this.closeableAmount,
      this.todayAmount + pos.totalAmount,
      this.sellAmount,
      (this.totalAmount * this.avgCost + pos.totalAmount * pos.totalAmount)
        / (this.totalAmount + pos.totalAmount),
      pos.close,
      (this.totalAmount + pos.totalAmount) * (this.avgCost - pos.close),
      (this.totalAmount + pos.totalAmount) * (this.avgCost - pos.close) / (this.totalAmount * this.avgCost - pos.totalAmount * pos.totalAmount),
      this.totalAmount * this.avgCost + pos.totalAmount * pos.totalAmount
    )
  }
}

object Position {
  def mk(order: Order): Position = {
    Position("", order.code, order.date, order.amount, 0, order.amount, 0, order.price, order.price, 0, 0, order.volume)
  }
}