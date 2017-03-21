package com.squant.cheetah.datasource

import java.io.{File, FileWriter}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.squant.cheetah.DataEngine
import com.squant.cheetah.domain.{Symbol, Tick, TickType}
import com.squant.cheetah.utils._
import com.squant.cheetah.utils.Constants._
import com.typesafe.scalalogging.LazyLogging

import scala.io.Source

object TickDataSource extends DataSource with LazyLogging {

  private val baseDir = config.getString(CONFIG_PATH_DB_BASE)
  private val tickDir = config.getString(CONFIG_PATH_TICK)

  //初始化数据源
  override def init(): Unit = {
    clear()
    update(start = LocalDateTime.now(), stop = LocalDateTime.now)
  }

  override def update(start: LocalDateTime, stop: LocalDateTime): Unit = {
    val stocks = DataEngine.symbols()

    stocks.foreach(symbol => {
      toCSV(symbol.code, LocalDateTime.now())
    })
  }

  override def clear(): Unit = {
    rm(s"/$baseDir/$tickDir").foreach(r => logger.info(s"delete ${r._1} ${r._2}"))
  }

  def fromCSV(code: String, date: LocalDateTime) = {
    val lines = Source.fromFile(new File(s"/data/tick/${date.format(DateTimeFormatter.ofPattern("yyyyMMdd"))}/$code.csv")).getLines().drop(1)
    lines.map {
      line =>
        val fields = line.split(",", 6)
        Tick(LocalDateTime.parse(date.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + " " + fields(0), DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")), fields(1).toFloat, fields(3).toInt, fields(4).toDouble, TickType.from(fields(5)))
    }.toSeq.reverse
  }

  def toCSV(code: String, date: LocalDateTime) = {
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