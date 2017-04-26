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
    context.portfolio.update(Order(code, amount, style, direction, context.currentDate()))
  }

  //买卖标的, 使最终标的的数量达到指定的amount
  override def orderTargetAmount(code: String, amount: Int, style: OrderStyle): Unit = {
    val currentPosition: Position = context.portfolio.positions.get(code) match {
      case Some(position) => {
        if (math.abs(position.closeableAmount - amount) < 100) {
          position
        } else {
          if (position.closeableAmount > amount) {
            logger.info(s"order:SHORT\t${code}\t${amount}\t${style}")
            val order = Order(code, position.closeableAmount - amount, style, SHORT, context.currentDate())
            context.portfolio.update(order)
            position - Position.mkFrom(order)
          } else if (position.closeableAmount < amount) {
            logger.info(s"order:LONG\t${code}\t${amount}\t${style}")
            val order = Order(code, amount - position.closeableAmount, style, LONG, context.currentDate())
            context.portfolio.update(order)
            position + Position.mkFrom(order)
          } else {
            position
          }
        }
      }
      case None => Position.mkFrom(Order(code, amount, style, LONG, context.currentDate()))
    }
    context.portfolio.positions.put(code, currentPosition)
  }
}
