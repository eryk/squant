package com.squant.cheetah.strategy

import com.squant.cheetah.domain._
import com.squant.cheetah.engine.{Context, DataEngine, StrategyEngine}
import com.typesafe.scalalogging.LazyLogging

abstract class Strategy(context: Context) extends StrategyEngine with LazyLogging {

  val symbols: Seq[Symbol] = context.symbols

  val broker = context.broker

  val dataEngine:DataEngine = new DataEngine(context)

  def initialize = {
    super.init()
  }

  def process(symbol: Symbol)

  def processes() = {
    symbols.foreach(process)
  }
}
