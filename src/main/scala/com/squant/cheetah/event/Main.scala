package com.squant.cheetah.event

import akka.actor.ActorSystem
import com.squant.cheetah.event.BackTest.TimeEvent
import com.squant.cheetah.examples.DoubleMovingAverage
import com.squant.cheetah.utils.Constants._
import com.squant.cheetah.utils._

object Main extends App {
  val system = ActorSystem("squant-backtest")

  val contexts = loadContext(config.getString(CONFIG_PATH_STRATEGY))
  val context = contexts("default")

  val backtest = system.actorOf(BackTest.props(new DoubleMovingAverage(context)))
  backtest ! TimeEvent
}
