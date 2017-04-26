package com.squant.cheetah.domain

import java.time.LocalDateTime

/**
  * 持有一只股票详情
  *
  */
case class Position(name: String,                       //证券名称
                    code: String,                       //证券代码
                    initTime: LocalDateTime,            //建仓时间
                    transactTime: LocalDateTime,        //最后交易时间
                    totalAmount: Int,                   //总仓位, 但不包括挂单冻结仓位
                    closeableAmount: Int,               //可卖数量
                    todayAmount: Double,                //今买数量
                    sellAmount: Double,                 //今卖数量
                    avgCost: Double,                    //成本价
                    close: Double,                      //当前价
                    floatPnl: Double,                   //浮动盈亏
                    pnlRatio: Double,                   //盈亏比例
                    latestValue: Double,                //最新市值
                    side: Direction                     //多/空
                   ) {

  /**
    * 减少持仓量
    *
    * @param pos
    * @return
    */
  def -(pos: Position): Position = {
    Position(this.name,
      this.code,
      this.initTime,
      pos.initTime,
      this.totalAmount - pos.totalAmount,
      this.closeableAmount - pos.totalAmount,
      this.todayAmount,
      this.sellAmount + pos.totalAmount,
      (this.totalAmount * this.avgCost - pos.totalAmount * pos.totalAmount)
        / (this.totalAmount - pos.totalAmount),
      pos.close,
      (this.totalAmount + pos.totalAmount) * (this.avgCost - pos.close),
      (this.totalAmount + pos.totalAmount) * (this.avgCost - pos.close) / (this.totalAmount * this.avgCost - pos.totalAmount * pos.totalAmount),
      this.totalAmount * this.avgCost - pos.totalAmount * pos.totalAmount,
      this.side
    )
  }

  /**
    * 增加持仓量
    *
    * @param pos
    * @return
    */
  def +(pos: Position): Position = {
    Position(this.name,
      this.code,
      this.initTime,
      pos.initTime,
      this.totalAmount + pos.totalAmount,
      this.closeableAmount,
      this.todayAmount + pos.totalAmount,
      this.sellAmount,
      (this.totalAmount * this.avgCost + pos.totalAmount * pos.totalAmount)
        / (this.totalAmount + pos.totalAmount),
      pos.close,
      (this.totalAmount + pos.totalAmount) * (this.avgCost - pos.close),
      (this.totalAmount + pos.totalAmount) * (this.avgCost - pos.close) / (this.totalAmount * this.avgCost - pos.totalAmount * pos.totalAmount),
      this.totalAmount * this.avgCost + pos.totalAmount * pos.totalAmount,
      this.side
    )
  }
}

object Position {
  def mkFrom(order: Order): Position = {
    Position("", order.code, order.date, order.date, order.amount, 0, order.amount, 0, order.price, order.price, 0, 0, order.volume, order.direction)
  }
}