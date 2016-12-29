package com.squant.cheetah.strategy

import akka.actor.Actor.Receive
import com.squant.cheetah.domain.{DAY, LONG, LimitOrderStyle, Order, SHORT, Symbol}
import com.squant.cheetah.engine.Context

class DoubleMovingAverage(sContext: Context) extends Strategy(sContext) {

  override def init() = {
    val start = System.currentTimeMillis()
    symbols = Seq[Symbol](find("000001").get)
    println("init cost:" + (System.currentTimeMillis() - start))
    logger.info("symbols:" + symbols.size)
    this
  }

  override def process(symbol: Symbol) = {
    val closeData = dataEngine.getHistoryData(symbol.code, 30, DAY)

    val ma5:Float = closeData.takeRight(5).map(_.close).sum / 5

    val ma10:Float = closeData.takeRight(10).map(_.close).sum / 10

    val cash:Double = sContext.portfolio.availableCash

    println(s"$ma5\t$ma10")

    if (ma5 > ma10) {
      broker.order(Order(symbol.code, 100, LimitOrderStyle(closeData.last.close), LONG, sContext.clock.now()))
      logger.info(s"buying ${symbol.name},price=${closeData.last.close}")
    } else if (ma5 < ma10 && sContext.portfolio.positions.contains(symbol.code)) {
      broker.order(Order(symbol.code, 100, LimitOrderStyle(closeData.last.close), SHORT, sContext.clock.now()))
      logger.info(s"Selling ${symbol.name},price=${closeData.last.close}")
    }
  }
}
