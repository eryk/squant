package com.squant.cheetah.engine

import com.squant.cheetah.strategy.Strategy

class Context {

  val name:String = "squant"

  def clock(): Clock = ???

  def strategyList():List[Strategy] = ???
}
