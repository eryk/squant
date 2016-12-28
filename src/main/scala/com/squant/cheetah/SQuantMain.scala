package com.squant.cheetah

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import com.squant.cheetah.engine.TradingSystem
import com.squant.cheetah.strategy.{TwoAvg, Strategy}
import com.squant.cheetah.utils.Constants._
import com.squant.cheetah.utils._

import scala.concurrent.ExecutionContext

object SQuantMain extends App {

  val actorSystem = ActorSystem("squant")
  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
  val contexts = loadContext(config.getString(CONFIG_PATH_STRATEGY))

  val strategies = Seq[Strategy](new TwoAvg(contexts.get("one").get))

  val tradingSystem = new TradingSystem(actorSystem,strategies);
  tradingSystem.run()

}