package com.squant.cheetah.datasource

import com.squant.cheetah.domain._
import com.typesafe.scalalogging.LazyLogging

import scala.sys.process._

object TushareDataSource extends App with DataSource with LazyLogging {

  def update() = {
    //下载股票列表
    "python3.5 " + getProjectDir() + "/script/Download.py stocks" !;
    //下载各级别k线数据
    "python3.5 " + getProjectDir() + "/script/Download.py ktype" !;
  }

  override def clear(): Unit = ???

  update()
}