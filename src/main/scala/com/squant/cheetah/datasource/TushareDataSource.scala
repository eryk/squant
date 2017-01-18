package com.squant.cheetah.datasource

import java.time.LocalDateTime

import com.squant.cheetah.domain._
import com.typesafe.scalalogging.LazyLogging

import scala.sys.process._

object TushareDataSource extends App with DataSource with LazyLogging {

  //初始化数据源
  override def init(): Unit = {
    //TODO 按照时间范围初始化
  }

  def update(start: LocalDateTime, stop: LocalDateTime) = {
    //下载股票列表
    "python3.5 " + getProjectDir() + "/script/Download.py stocks" !;
    //下载各级别k线数据
    "python3.5 " + getProjectDir() + "/script/Download.py ktype" !;
    //下载指标数据
    "python3.5 " + getProjectDir() + "/script/Download.py index" !;
  }

  override def clear(): Unit = {
    import com.squant.cheetah.utils._
    import com.squant.cheetah.utils.Constants._

    val base = config.getString(CONFIG_PATH_DB_BASE)
    val stocks = config.getString(CONFIG_PATH_STOCKS)

    val files = Seq(stocks,"/5","/15","/30","/60","/day","/week","/month","/index")

    files.foreach(file => rm(base + file).foreach(r => logger.info(s"delete ${r._1} ${r._2}")))
  }

}