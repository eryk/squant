package com.squant.cheetah.broker

import com.squant.cheetah.domain.{Direction, Order, OrderStyle}

/**
  * Created by eryk on 17-4-25.
  */
trait Broker {

  def order(order: Order)

  //按股数下单
  def order(code: String, amount: Int, style: OrderStyle, direction: Direction)

  //买卖标的, 使最终标的的数量达到指定的amount
  def orderTargetAmount(code: String, amount: Int, style: OrderStyle)

  //买卖价值为value的股票
  def orderValue(code: String, value: Double, style: OrderStyle, direction: Direction) = ???

  //调整股票仓位到value价值
  def orderTargetValue(code: String, targetValue: Double, style: OrderStyle, direction: Direction) = ???

  //取消订单
  def cancelOrder(orderId: String) = ???

  //获得当天的所有未完成的订单
  def getOpenOrders: List[Order] = ???

  //  获取当天的所有订单
  def getOrders: List[Order] = ???

  // 获取成交信息
  def getTrades: List[Order] = ???

  protected def computeAmount(amount: Int): Int = {
    amount match {
      case value if value < 100 => 0
      case value => value / 100 * 100
    }
  }
}
