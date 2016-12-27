package com.squant.cheetah.strategy

import com.squant.cheetah.domain.{LONG, LimitOrderStyle, Order, SHORT, Symbol}
import com.squant.cheetah.engine.Context

class OneStrategy(c: Context) extends Strategy(c) {

  override def process(symbol: Symbol) = {
    println(symbol.code)
    val bars = dataEngine.getHistoryData(symbol.code, 20)
    if (bars(0).close > bars(1).close) {
      broker.order(Order(symbol.code, 100, LimitOrderStyle(bars(1).close), LONG, clock.now()))
    }
    else {
      broker.order(Order(symbol.code, 100, LimitOrderStyle(bars(1).close), SHORT, clock.now()))
    }
    println(clock.now() + "\t" + c.portfolio)
  }
}
