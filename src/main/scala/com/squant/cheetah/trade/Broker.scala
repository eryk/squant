package com.squant.cheetah.trade

import com.squant.cheetah.domain._

class Broker(portfolio: Portfolio) {

  // 下单
  def order(order: Order): Unit = ???

  // 撤单
  def cancelOrder(id: String) = ???

  // 获取未完成订单
  def getOpenOrder() = ???

  // 获取订单信息
  def getOrders() = ???

  // 获取成交订单
  def getTrades() = ???
}
