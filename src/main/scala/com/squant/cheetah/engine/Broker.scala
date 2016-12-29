package com.squant.cheetah.engine

import com.squant.cheetah.domain._
import com.squant.cheetah.trade.Portfolio

class Broker(portfolio: Portfolio) {

  // 下单
  def order(order: Order): Unit = {

    //TODO 先创建order，由broker操作
    order.direction match {
      case LONG => portfolio.longOrder(order)
      case SHORT => portfolio.shortAllOrder(order)
      case _ => new UnknownError()
    }
  }

  // 撤单
  def cancelOrder(id: String) = ???

  // 获取未完成订单
  def getOpenOrder() = ???

  // 获取订单信息
  def getOrders() = ???

  // 获取成交订单
  def getTrades() = ???
}
