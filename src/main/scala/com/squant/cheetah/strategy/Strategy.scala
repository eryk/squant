package com.squant.cheetah.strategy

import com.squant.cheetah.Feeds
import com.squant.cheetah.engine.Context
import com.squant.cheetah.trade.Portfolio
import com.typesafe.scalalogging.LazyLogging

abstract class Strategy(context: Context) extends LazyLogging {

  val portfolio: Portfolio = new Portfolio(context)

  val feeds: Feeds = new Feeds(context)

  val g = scala.collection.mutable.Map[String, Any]()

  def init()

  def handle()

  def getContext = context
}
