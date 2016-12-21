package com.squant.cheetah.strategy

import com.squant.cheetah.engine.Engine
import com.typesafe.scalalogging.LazyLogging

trait Strategy extends Engine with LazyLogging {

  val symbols: Seq[Symbol]

  def initialize: Unit

  def process(symbol: Symbol)

  def processes() = {
    symbols.foreach(process)
  }
}
