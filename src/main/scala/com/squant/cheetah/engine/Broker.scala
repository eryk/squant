package com.squant.cheetah.engine

import com.squant.cheetah.domain.{OrderDirection, OrderStyle}
import com.squant.cheetah.trade.Portfolio

class Broker(portfolio: Portfolio) {

  // 下单
  def order(code: String, amount: Int, style: OrderStyle, side: OrderDirection) = ???

  // 撤单
  def cancelOrder(id: String) = ???

  // 获取未完成订单
  def getOpenOrder() = ???

  // 获取订单信息
  def getOrders() = ???

  // 获取成交订单
  def getTrades() = ???
}
