package com.squant.cheetah.strategy

import java.time.LocalDateTime

import com.squant.cheetah.domain._
import com.squant.cheetah.engine.{Context, DataEngine}
import com.typesafe.scalalogging.LazyLogging

abstract class Strategy(context: Context) extends LazyLogging {

  var symbols: Seq[Symbol] = context.symbols

  val broker = context.broker

  val dataEngine: DataEngine = new DataEngine(context)

  val clock = context.clock

  val g = scala.collection.mutable.Map[String,Any]()

  def init(): Unit

  def process(symbol: Symbol)

  def processes() = {
    symbols.foreach(process)
  }

  def now(): LocalDateTime = clock.now()

}
