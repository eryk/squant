package com.squant.cheetah.strategy

import java.time.LocalDateTime

import akka.actor.Actor
import com.squant.cheetah.domain.BarType
import com.squant.cheetah.engine.{Context, Engine}
import com.typesafe.scalalogging.LazyLogging

trait Strategy extends Actor with Engine with LazyLogging {

  val symbols: Seq[Symbol]

  def initialize: Unit

  override def currentData(code: String, count: Int, frequency: BarType): Unit = ???

  override def getFundamentals(code: String, startDate: LocalDateTime): Unit = ???


  def process(code: String)

  override def receive = {
    case code: String => process(code)
  }
}
