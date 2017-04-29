package com.squant.cheetah.examples

import com.squant.cheetah.Feeds
import com.squant.cheetah.broker.Broker
import com.squant.cheetah.domain.{DAY, LONG, LimitOrderStyle, Order, SHORT}
import com.squant.cheetah.engine.Context
import com.squant.cheetah.strategy.Strategy

class DoubleMovingAverage(context: Context)(implicit broker: Broker) extends Strategy(context, broker) {

  val symbols = Feeds.symbols().take(5)

  override def init() = {
    logger.info("symbols:" + symbols.size)
    symbols.foreach(symbol => logger.info(s"$symbol"))
  }

  override def handle() = {
    symbols.foreach { symbol =>
      val closeData = feeds.getHistoryData(symbol.code, 30, DAY)

      if (closeData.nonEmpty) {
        val ma5: Double = closeData.takeRight(5).map(_.close).sum / 5

        val ma10: Double = closeData.takeRight(10).map(_.close).sum / 10

        val amount: Int = (context.portfolio.availableCash / closeData.last.close).toInt
        if (ma5 > ma10) {
          order(Order(symbol.code, amount, LimitOrderStyle(closeData.last.close), LONG ,context.currentDate))
        } else if (ma5 < ma10 && context.portfolio.positions.contains(symbol.code)) {
          orderTargetAmount(symbol.code, 0, LimitOrderStyle(closeData.last.close))
        }
      }
    }


  }
}
