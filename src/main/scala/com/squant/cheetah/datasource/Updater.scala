package com.squant.cheetah.datasource

import java.time.LocalDateTime

import com.typesafe.scalalogging.StrictLogging
import cronish._
import cronish.dsl._

object Updater extends App with StrictLogging{

  val start = LocalDateTime.now.plusDays(-1)
  val stop = LocalDateTime.now

  StockBasicsSource.update()

  StockCategoryDataSource.init()

  DailyKTypeDataSource.init()

  MinuteKTypeDataSource.init()

}
