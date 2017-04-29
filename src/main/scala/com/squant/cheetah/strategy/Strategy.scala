package com.squant.cheetah.strategy

import com.squant.cheetah.Feeds
import com.squant.cheetah.broker.Broker
import com.squant.cheetah.domain.{Direction, Order, OrderStyle}
import com.squant.cheetah.engine.Context
import com.typesafe.scalalogging.LazyLogging

abstract class Strategy(context: Context, broker: Broker) extends LazyLogging with Broker {
  val feeds: Feeds = new Feeds(context)

  def init()

  def handle()

  def getContext = context

  override def order(order: Order): Unit = {
    broker.order(order)
  }

  override def order(code: String, amount: Int, style: OrderStyle, direction: Direction): Unit = {
    broker.order(code, amount, style, direction)
  }

  override def orderTargetAmount(code: String, amount: Int, style: OrderStyle): Unit = {
    broker.orderTargetAmount(code, amount, style)
  }
}
