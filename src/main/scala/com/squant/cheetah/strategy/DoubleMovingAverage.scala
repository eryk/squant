package com.squant.cheetah.strategy

import com.squant.cheetah.domain.{DAY, LONG, LimitOrderStyle, Order, SHORT, Symbol}
import com.squant.cheetah.engine.Context

class DoubleMovingAverage(context: Context) extends Strategy(context) {

  override def init() = {
    symbols = Seq[Symbol](find("000001").get)
    logger.info("symbols:" + symbols.size)
  }

  override def process(symbol: Symbol) = {
    val bars = dataEngine.getHistoryData(symbol.code, 20)

    val closeData = dataEngine.getHistoryData(symbol.code, 30, DAY)

    val ma5:Float = closeData.takeRight(5).map(_.close).sum / 5

    val ma10:Float = closeData.takeRight(10).map(_.close).sum / 10

    val cash:Double = context.portfolio.availableCash

    if (ma5 > ma10) {
      broker.order(Order(symbol.code, 100, LimitOrderStyle(closeData.last.close), LONG, clock.now()))
      logger.info(s"buying ${symbol.name},price=${closeData.last.close}")
    } else if (ma5 < ma10 && context.portfolio.positions.contains(symbol.code)) {
      broker.order(Order(symbol.code, 100, LimitOrderStyle(closeData.last.close), SHORT, clock.now()))
      logger.info(s"Selling ${symbol.name},price=${closeData.last.close}")
    }
    println(clock.now)
  }

}
