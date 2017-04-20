package com.squant.cheetah.examples

import com.squant.cheetah.Feeds
import com.squant.cheetah.domain.{DAY, FAILED, LONG, LimitOrderStyle, Order, SHORT, SUCCESS, Symbol}
import com.squant.cheetah.engine.Context
import com.squant.cheetah.strategy.Strategy

class DoubleMovingAverage(context: Context) extends Strategy(context) {

  val symbols = Feeds.symbols().take(5)

  override def init() = {
    logger.info("symbols:" + symbols.size)
    symbols.foreach(symbol => logger.info(s"$symbol"))
  }

  override def handle() = {
    symbols.foreach { symbol =>
      val closeData = feeds.getHistoryData(symbol.code, 30, DAY)

      if (closeData.size != 0) {
        val ma5: Float = closeData.takeRight(5).map(_.close).sum / 5

        val ma10: Float = closeData.takeRight(10).map(_.close).sum / 10

        if (ma5 > ma10) {
          portfolio.buyAll(symbol.code, LimitOrderStyle(closeData.last.close))
        } else if (ma5 < ma10 && portfolio.positions.contains(symbol.code)) {
          portfolio.sellAll(symbol.code, LimitOrderStyle(closeData.last.close))
        }
      }
    }


  }
}
