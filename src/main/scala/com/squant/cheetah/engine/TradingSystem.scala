package com.squant.cheetah.engine

import java.time.LocalDateTime

import akka.actor.{ActorSystem, Props}
import com.squant.cheetah.domain.BarType
import com.squant.cheetah.strategy.Strategy
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._


class TradingSystem(context: Context) extends App with LazyLogging {

  val clock = context.clock()

  val stratgies = context.strategyList()


  var actorSystem = ActorSystem(context.name)

  for (strategy <- stratgies) {
    val actor = actorSystem.actorOf(Props[Strategy])
    actorSystem.scheduler.schedule(Duration.Zero, 60 seconds, new Runnable {
      override def run(): Unit = {
        actor ! clock.now()
      }
    })
  }
}
