package com.squant.cheetah.strategy

import akka.actor.Actor
import com.squant.cheetah.engine.Engine
import com.typesafe.scalalogging.LazyLogging

trait Strategy extends Actor with Engine with LazyLogging {

  val symbols: Seq[Symbol]

  def initialize: Unit

  def process(code: String)

  override def receive = {
    case code: String => process(code)
  }
}
