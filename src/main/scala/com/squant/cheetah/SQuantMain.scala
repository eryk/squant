package com.squant.cheetah

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import com.squant.cheetah.engine.{Context, TradingSystem}

import scala.concurrent.ExecutionContext

object SQuantMain extends App {
  val actorSystem = ActorSystem("squant")

  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))

  val context = new Context()

  val tradingSystem = new TradingSystem(actorSystem, context);

  tradingSystem.run()
}