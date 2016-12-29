package com.squant.cheetah.engine

import akka.actor.{ActorSystem, Props}
import com.squant.cheetah.strategy.{CLOCK_EVENT, Strategy}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import com.squant.cheetah.utils._

class TradingSystem(actorSystem: ActorSystem, strategies: Seq[Strategy]) extends LazyLogging {

  def run() = {
    for (strategy <- strategies) {
      strategy.init()

      val actor = actorSystem.actorOf(Props(strategy))

      while (true) {

      }
    }
  }

}
