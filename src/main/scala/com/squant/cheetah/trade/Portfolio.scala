package com.squant.cheetah.trade

import java.time.LocalDateTime

import scala.collection._

//账户当前的资金，标的信息，即所有标的操作仓位的信息汇总
class Portfolio(startingCash: Double) {

  val porfolioMetric: PortfolioMetric = new PortfolioMetric
  porfolioMetric.startingCash = startingCash

  val records = mutable.Map[LocalDateTime, Record]() //记录各个时间点账户状态

  var ts: LocalDateTime = null //最后更新record的时间点

  var positions: Map[String, Position] = mutable.Map[String, Position]() //记录账户当前持仓情况

}
