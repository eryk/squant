package com.squant.cheetah.strategy

import com.squant.cheetah.domain.{LONG, LimitOrderStyle, Order, SHORT, Symbol}
import com.squant.cheetah.engine.Context

class TwoAvg(context: Context) extends Strategy(context) {

  override def init() = {
    context.symbols = symbols.filter()
  }

  override def process(symbol: Symbol) = {
    val bars = dataEngine.getHistoryData(symbol.code, 20)
    if(bars.length < 10){
//      println("no data:" + symbol.code)
    }else{
      if (bars(0).close > bars(1).close) {
        broker.order(Order(symbol.code, 100, LimitOrderStyle(bars(1).close), LONG, clock.now()))
      }
      else {
        broker.order(Order(symbol.code, 100, LimitOrderStyle(bars(1).close), SHORT, clock.now()))
      }
      println(clock.now() + "\t" + context.portfolio.positions)
    }
  }

}
