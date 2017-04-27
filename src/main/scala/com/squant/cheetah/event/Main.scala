package com.squant.cheetah.event

import akka.actor.ActorSystem
import com.squant.cheetah.broker.{BackTestBroker, Broker}
import com.squant.cheetah.examples.DoubleMovingAverage
import com.squant.cheetah.utils.Constants._
import com.squant.cheetah.utils._
import com.typesafe.scalalogging.StrictLogging

object Main extends App with StrictLogging {
  val system = ActorSystem("squant-backtest")

  val contexts = loadContext(config.getString(CONFIG_PATH_STRATEGY))
  val context = contexts("default")
  logger.info(s"${context}")

  implicit val broker: Broker = new BackTestBroker(context)

  val backtest = system.actorOf(BackTest.props(new DoubleMovingAverage(context)))
  backtest ! TimeEvent(context.currentDate)
}
