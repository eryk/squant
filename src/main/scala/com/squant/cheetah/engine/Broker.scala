package com.squant.cheetah.engine

import com.squant.cheetah.domain.{OrderDirection, OrderStyle}
import com.squant.cheetah.trade.{Portfolio, Position}
import com.squant.cheetah.domain._

class Broker(clock: Clock, portfolio: Portfolio) {

  // 下单
  def order(code: String, amount: Int, style: OrderStyle, side: OrderDirection): Unit = {

    //TODO 先创建order，由broker操作
    val orderState = side match {
      case LONG => portfolio.longOrder(code,amount,style,clock.now)
      case SHORT => portfolio.shortOrder(code,amount,style,clock.now)
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
