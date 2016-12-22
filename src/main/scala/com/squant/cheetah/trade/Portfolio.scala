package com.squant.cheetah.trade

import java.time.LocalDateTime

import com.squant.cheetah.domain.OrderStyle

import scala.collection._

//账户当前的资金，标的信息，即所有标的操作仓位的信息汇总
class Portfolio(startingCash: Double) {

  private val porfolioMetric: PortfolioMetric = new PortfolioMetric(startingCash)

  private val records = mutable.Map[LocalDateTime, Record]() //记录各个时间点账户状态

  var ts: LocalDateTime = null //最后更新record的时间点

  //key是股票代码code
  var positions: Map[String, Position] = mutable.Map[String, Position]() //记录账户当前持仓情况

  def longOrder(code: String, amount: Int, style: OrderStyle): Boolean = {
    if (porfolioMetric.availableCash > amount * style.price) {

    }
    true
  }

  def shortOrder(code: String, amount: Int, style: OrderStyle): Boolean = {
    if(positions.contains(code))
    true
  }
}
