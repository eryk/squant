package com.squant.cheetah.datasource

import java.time.LocalDateTime

import com.typesafe.scalalogging.StrictLogging
import cronish._
import cronish.dsl._

object Updater extends App with StrictLogging{

  val start = LocalDateTime.now.plusDays(-1)
  val stop = LocalDateTime.now

  //下载股票分类数据
  job{StockCategoryDataSource.update(start,stop)} executes "Every day on the weekday at 00:05".cron
  //下载逐笔数据
  job{TickDataSource.update(start,stop)} executes "Every day on the weekday at 00:10".cron
  //下载k线数据
  job{KTypeDataSource.update(start,stop)} executes "Every day on the weekday at 00:40".cron
}
