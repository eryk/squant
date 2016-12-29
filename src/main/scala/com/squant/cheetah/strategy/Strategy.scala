package com.squant.cheetah.strategy

import java.time.LocalDateTime

import akka.actor.{Actor, Props}
import com.squant.cheetah.domain._
import com.squant.cheetah.engine.{DataEngine, Context => StrategyContext}
import com.squant.cheetah.utils._
import com.typesafe.scalalogging.LazyLogging

sealed class EVENT_TYPE()

case object INIT_EVENT extends EVENT_TYPE

case object CLOCK_EVENT extends EVENT_TYPE

case object DATA_EVENT extends EVENT_TYPE

abstract class Strategy(sContext: StrategyContext) extends LazyLogging with Actor {

  var symbols: Seq[Symbol] = DataEngine.symbols()

  val broker = sContext.broker

  val dataEngine: DataEngine = new DataEngine(sContext)

  val g = scala.collection.mutable.Map[String, Any]()

  def init(): Strategy

  var isInit = false

  def process(symbol: Symbol)

  private def processes() = {
    symbols.foreach(process)
  }

  override def receive: Receive = {
    case INIT_EVENT => init()
    case CLOCK_EVENT => {
      println("clockk_event")
      if (isTradingTime(sContext.clock.now())) {
        processes()
      }
      sContext.clock.update()
    }
  }

  def now(): LocalDateTime = sContext.clock.now()

  def find(code: String): Option[Symbol] = symbols.find(_.code == code)
}
