package com.squant.cheetah.strategy

import java.time.LocalDateTime

import com.squant.cheetah.domain._
import com.squant.cheetah.engine.{Context, DataEngine}
import com.squant.cheetah.trade.Portfolio
import com.squant.cheetah.utils._
import com.typesafe.scalalogging.LazyLogging

abstract class Strategy(context: Context) extends LazyLogging {

  var symbols: Seq[Symbol] = DataEngine.symbols()

  val portfolio: Portfolio = new Portfolio(context)

  val dataEngine: DataEngine = new DataEngine(context)

  val g = scala.collection.mutable.Map[String, Any]()

  def init()

  def process(symbol: Symbol)

  def processes() = {
    if (isTradingTime(context.clock.now())) {
      symbols.foreach(process)
    }
    context.clock.update()
  }

  def now(): LocalDateTime = context.clock.now()

  def find(code: String): Option[Symbol] = symbols.find(_.code == code)
}
