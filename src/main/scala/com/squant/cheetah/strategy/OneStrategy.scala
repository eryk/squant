package com.squant.cheetah.strategy

import com.squant.cheetah.domain.Symbol
import com.squant.cheetah.engine.Context

class OneStrategy(c:Context) extends Strategy(c) {

  override def process(symbol: Symbol) = {
    println(symbol)
  }

}
