package com.squant.cheetah.engine

import akka.actor.ActorSystem
import com.squant.cheetah.strategy.Strategy
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import com.squant.cheetah.utils._

class TradingSystem(actorSystem: ActorSystem, strategies: Seq[Strategy]) extends LazyLogging {

  def run() = {
    for (strategy <- strategies) {
      strategy.init()
      actorSystem.scheduler.schedule(Duration.Zero, 1 microseconds, new Runnable {
        override def run(): Unit = {
          if(isTradingTime(strategy.clock.now())){
            strategy.processes()
          }
          strategy.clock.update()
        }
      })
    }
  }

}
