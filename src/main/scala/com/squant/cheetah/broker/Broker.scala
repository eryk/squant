package com.squant.cheetah.broker

import com.squant.cheetah.domain._
import com.squant.cheetah.engine.Context

trait Broker{

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
