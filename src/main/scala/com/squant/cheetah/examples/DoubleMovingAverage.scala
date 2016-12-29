package com.squant.cheetah.examples

import com.squant.cheetah.domain.{DAY, FAILED, LONG, LimitOrderStyle, Order, SHORT, SUCCESS, Symbol}
import com.squant.cheetah.engine.Context
import com.squant.cheetah.strategy.Strategy

class DoubleMovingAverage(context: Context) extends Strategy(context) {

  override def init() = {
    symbols = Seq[Symbol](find("000001").get)
    logger.info("symbols:" + symbols.size)
  }

  override def process(symbol: Symbol) = {
    val closeData = dataEngine.getHistoryData(symbol.code, 30, DAY)

    val ma5:Float = closeData.takeRight(5).map(_.close).sum / 5

    val ma10:Float = closeData.takeRight(10).map(_.close).sum / 10

    if (ma5 > ma10) {
      portfolio.buyAll(symbol.code, LimitOrderStyle(closeData.last.close)) match{
        case SUCCESS => logger.info(s"buying ${symbol.name},price=${closeData.last.close}")
        case _ =>
      }
    } else if (ma5 < ma10 && portfolio.positions.contains(symbol.code)) {
      portfolio.sellAll(symbol.code, LimitOrderStyle(closeData.last.close)) match {
        case SUCCESS => logger.info(s"Selling ${symbol.name},price=${closeData.last.close}")
        case _ =>
      }
    }
  }
}
