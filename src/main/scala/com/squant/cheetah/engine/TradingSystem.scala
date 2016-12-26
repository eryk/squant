package com.squant.cheetah.engine

import akka.actor.ActorSystem
import com.squant.cheetah.strategy.Strategy
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class TradingSystem(actorSystem: ActorSystem, strategies: Seq[Strategy]) extends LazyLogging {

  def run() = {
    for (strategy <- strategies) {
      actorSystem.scheduler.schedule(Duration.Zero, strategy.clock.interval minutes, new Runnable {
        override def run(): Unit = {
          strategy.processes()
          strategy.clock.update()
        }
      })
    }
  }

}
