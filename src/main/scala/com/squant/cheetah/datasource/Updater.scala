package com.squant.cheetah.datasource

import java.time.LocalDateTime

import com.typesafe.scalalogging.StrictLogging
import cronish._
import cronish.dsl._

object Updater extends App with StrictLogging {

  val start = LocalDateTime.now.plusDays(-1)
  val stop = LocalDateTime.now

  logger.info("开始下载股票基本面数据")
  StockBasicsSource.init()
  logger.info("股票基本面数据下载完成")
  logger.info("开始下载股票分类数据,下载持续时间较长，请耐心等待")
  StockCategoryDataSource.init()
  logger.info("股票基本面数据下载完成")
  logger.info("开始下载股票日线数据,下载持续时间较长，请耐心等待")
  DailyKTypeDataSource.init()
  logger.info("股票日线数据下载完成")
  MinuteKTypeDataSource.init()
  logger.info("开始下载股票当日逐笔数据,下载持续时间较长，请耐心等待")
  TickDataSource.init()
  logger.info("股票当日逐笔数据下载完成")
}
