package com.squant.cheetah

import java.util.concurrent.TimeUnit

import com.squant.cheetah.engine.TRADE
import com.squant.cheetah.examples.DoubleMovingAverage
import com.squant.cheetah.utils.Constants._
import com.squant.cheetah.utils._
import com.typesafe.scalalogging.LazyLogging

object SQuantMain extends App with LazyLogging {

  val start = System.currentTimeMillis()

  val contexts = loadContext(config.getString(CONFIG_PATH_STRATEGY))
  val context = contexts.get("default").get
  val strategy = new DoubleMovingAverage(context)
  strategy.init()

  while (!context.clock.isFinished()) {
    strategy.processes()
    if (context.clock.clockType() == TRADE) {
      TimeUnit.MINUTES.sleep(context.clock.interval())
    }
  }
  logger.info("portfolio:" + strategy.portfolio.endingCash)
  logger.info(s"strategy is finished. cost=${System.currentTimeMillis() - start} ms")
}