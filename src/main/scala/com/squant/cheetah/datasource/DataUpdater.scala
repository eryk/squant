package com.squant.cheetah.datasource

import com.typesafe.scalalogging.StrictLogging
import cronish._
import cronish.dsl._

object DataUpdater extends App with StrictLogging{

  //下载股票分类数据
  job{THSDataSource.update()} executes "Every day on the weekday at 00:05".cron
  //下载逐笔数据
  job{SinaDataSource.update()} executes "Every day on the weekday at 00:10".cron
  //下载k线数据
  job{TushareDataSource.update()} executes "Every day on the weekday at 00:40".cron
}
