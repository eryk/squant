package com.squant.cheetah

import java.util.concurrent.{Executors, TimeUnit}

import akka.actor.{ActorSystem, Props}
import com.squant.cheetah.engine.{TRADE, TradingSystem}
import com.squant.cheetah.strategy.{CLOCK_EVENT, DoubleMovingAverage, INIT_EVENT, Strategy}
import com.squant.cheetah.utils.Constants._
import com.squant.cheetah.utils._
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext

object SQuantMain extends App with LazyLogging {

  val start = System.currentTimeMillis()
  val actorSystem = ActorSystem("cheetah")
  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
  val contexts = loadContext(config.getString(CONFIG_PATH_STRATEGY))

  println("main cost:" + (System.currentTimeMillis() - start))

  val context = contexts.get("default").get
  val actor = actorSystem.actorOf(Props(new DoubleMovingAverage(context).init()))

  println("main cost:" + (System.currentTimeMillis() - start))

  while (!context.clock.isFinished()) {
    actor ! CLOCK_EVENT
    if (context.clock.clockType() == TRADE) {
      TimeUnit.MINUTES.sleep(context.clock.interval())
    }
  }

  println("main cost:" + (System.currentTimeMillis() - start))

  logger.info("strategy is finished.")
  //  actorSystem.terminate
}