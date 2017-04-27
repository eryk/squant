package com.squant.cheetah.broker

import com.squant.cheetah.domain.{Direction, LONG, Order, OrderStyle, Position, SHORT}
import com.squant.cheetah.engine.Context
import com.typesafe.scalalogging.LazyLogging

/**
  * Created by eryk on 17-4-25.
  */
class BackTestBroker(context: Context) extends Broker with LazyLogging {
  override def order(order: Order): Unit = {
    context.portfolio.update(order)
  }

  //按股数下单
  override def order(code: String, amount: Int, style: OrderStyle, direction: Direction): Unit = {
    context.portfolio.update(Order(code, amount, style, direction, context.currentDate))
  }

  //买卖标的, 使最终标的的数量达到指定的amount
  override def orderTargetAmount(code: String, amount: Int, style: OrderStyle): Unit = {
    val order: Option[Order] = context.portfolio.positions.get(code) match {
      case Some(position) => {
        val currentAmount = computeAmount(math.abs(position.totalAmount - amount))
        if (currentAmount >= 100) {
          val direction: Direction = if (position.totalAmount > amount) SHORT else LONG
          Some(Order(code, currentAmount, style, direction, context.currentDate))
        } else {
          None
        }
      }
      case None => {
        val currentAmount = computeAmount(amount)
        if (amount > 0) Some(Order(code, currentAmount, style, LONG, context.currentDate))
        else None
      }
    }
    if (order.isDefined) {
      context.portfolio.update(order.get)
      logger.info(s"${order.get.date}\torder:${order.get.direction}\t$code\t${order.get.amount}\t$style")
    }

  }
}
