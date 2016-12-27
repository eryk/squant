package com.squant.cheetah.trade

import java.time.LocalDateTime

import com.squant.cheetah.domain.{CREATE_FAIL, FAILED, Order, OrderState, OrderStyle, SUCCESS, UNKNOW}

import scala.collection._

//账户当前的资金，标的信息，即所有标的操作仓位的信息汇总
class Portfolio(startingCash: Double) {

  private val porfolioMetric: PortfolioMetric = new PortfolioMetric(startingCash)

  private val records = mutable.Map[LocalDateTime, Record]() //记录各个时间点账户状态

  var ts: LocalDateTime = null //最后更新record的时间点

  //key是股票代码code
  var positions: mutable.Map[String, Position] = mutable.Map[String, Position]() //记录账户当前持仓情况

  def longOrder(order: Order): OrderState = {
    porfolioMetric.availableCash > order.volume match {
      case true => {
        positions.put(order.code, Position.mk(order))
        porfolioMetric.availableCash = porfolioMetric.availableCash - order.volume
      }
      case false => CREATE_FAIL
      //TODO update porfolio
    }
    UNKNOW
  }

  def shortOrder(order: Order): OrderState = {
    positions.contains(order.code) match {
      case true => {
        val position = positions.get(order.code).get
        if (position.totalAmount < order.amount) {
          return FAILED
        } else if (position.totalAmount == order.amount) {
          positions - order.code
          //TODO update porfolio
        } else {
          Position.sub(positions.get(order.code).get, positions.get(order.code).get)
          //TODO update porfolio
        }
        SUCCESS
      }
      case false => FAILED
    }
  }

  def shortAllOrder(order: Order): OrderState = {
    positions.get(order.code) match {
      case Some(position) => {
        porfolioMetric.availableCash += position.totalAmount * position.avgCost
        SUCCESS
      }
      case _ => FAILED
    }
  }
}
