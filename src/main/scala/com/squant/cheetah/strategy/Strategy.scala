package com.squant.cheetah.strategy

import akka.actor.Actor
import com.squant.cheetah.StrategyContext

trait Strategy extends Actor {

  val strategyContext = new StrategyContext

  val symbols = scala.collection.mutable.Set[String]()

  def initialize

  def setSymbols(symbols: Set[String]): Unit = {
    this.symbols ++ symbols
  }

  def process(code: String)

  override def receive = {
    case code: String => process(code)
  }
}
