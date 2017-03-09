package com.squant.cheetah.datasource

import java.io.{File, FileWriter}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.squant.cheetah.DataEngine
import com.squant.cheetah.domain.Symbol
import com.squant.cheetah.utils._
import com.squant.cheetah.utils.Constants._
import com.typesafe.scalalogging.LazyLogging

import scala.io.Source

object TickDataSource extends App with DataSource with LazyLogging {

  val baseDir = config.getString(CONFIG_PATH_DB_BASE)
  val tickDir = config.getString(CONFIG_PATH_TICK)

  //初始化数据源
  override def init(): Unit = {
    //TODO 按照时间范围初始化
  }

  override def update(start: LocalDateTime, stop: LocalDateTime): Unit = {
    val stocks = DataEngine.symbols()

    stocks.foreach(symbol => {
      writeTick(symbol.code, LocalDateTime.now())
    })
  }

  override def clear(): Unit = {
    rm(s"/$baseDir/$tickDir").foreach(r => logger.info(s"delete ${r._1} ${r._2}"))
  }

  private def writeTick(code: String, date: LocalDateTime) = {
    val tickDayDataURL = "http://market.finance.sina.com.cn/downxls.php?date=%s&symbol=%s"
    val formatDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    if (!new File(s"/$baseDir/$tickDir/$formatDate").exists()) {
      new File(s"/$baseDir/$tickDir/$formatDate").mkdirs()
    }
    val out = new FileWriter(s"/$baseDir/$tickDir/$formatDate/$code.csv", false)
    out.write(Source.fromURL(String.format(tickDayDataURL, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), Symbol.getSymbol(code, tickDayDataURL)), "gbk").mkString.replaceAll("\t", ",")).ensuring {
      out.close()
      true
    }
  }

}