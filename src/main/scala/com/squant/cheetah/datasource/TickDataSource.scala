package com.squant.cheetah.datasource

import java.io.{File, FileWriter}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.squant.cheetah.DataEngine
import com.squant.cheetah.domain.{Symbol, Tick, TickType}
import com.squant.cheetah.engine.DataBase
import com.squant.cheetah.utils._
import com.squant.cheetah.utils.Constants._
import com.typesafe.scalalogging.LazyLogging

import scala.io.Source

/**
  * 由于tick数据量太大，在初始化数据时并不会更新全部tick数据，在请求csv数据不存在是会更新tick数据
  */
object TickDataSource extends DataSource with LazyLogging {

  private val baseDir = config.getString(CONFIG_PATH_DB_BASE)
  private val tickDir = config.getString(CONFIG_PATH_TICK)

  //初始化数据源
  override def init(taskConfig: TaskConfig =
                    TaskConfig("TickDataSource",
                      "", true, true, true, LocalDateTime.now, LocalDateTime.now)): Unit = {
    clear()
    update(taskConfig)
  }

  override def update(taskConfig: TaskConfig): Unit = {
    logger.info(s"Start to download stock tick data, ${format(taskConfig.stop, "yyyyMMdd")}")
    val stocks = DataEngine.symbols()
    stocks.par.foreach(symbol => {
      if (taskConfig.clear) clear()
      if (taskConfig.toCSV) toCSV(symbol.code, taskConfig.stop)
      if (taskConfig.toDB) toDB(symbol.code, taskConfig.stop)
    })
    logger.info(s"Download completed")
  }

  override def clear(): Unit = {
    rm(s"$baseDir/$tickDir/${LocalDateTime.now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))}")
      .foreach(r => logger.info(s"delete ${r._1} ${r._2}"))
  }

  def fromCSV(code: String, date: LocalDateTime) = {
    val file = new File(s"$baseDir/$tickDir/${date.format(DateTimeFormatter.ofPattern("yyyyMMdd"))}/$code.csv")

    if (!file.exists()) {
      toCSV(code, date)
    }

    val lines = Source.fromFile(file).getLines().drop(1)
    lines.map {
      line =>
        val fields = line.split(",", 6)
        Tick(LocalDateTime.parse(date.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + " " + fields(0), DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")), fields(1).toFloat, fields(3).toInt, fields(4).toDouble, TickType.from(fields(5)))
    }.toSeq.reverse
  }

  def toCSV(code: String, date: LocalDateTime) = {
    val tickDayDataURL = "http://market.finance.sina.com.cn/downxls.php?date=%s&symbol=%s"
    val formatDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    if (!new File(s"$baseDir/$tickDir/$formatDate").exists()) {
      new File(s"$baseDir/$tickDir/$formatDate").mkdirs()
    }
    val out = new FileWriter(s"$baseDir/$tickDir/$formatDate/$code.csv", false)
    out.write(Source.fromURL(String.format(tickDayDataURL, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), Symbol.getSymbol(code, tickDayDataURL)), "gbk").mkString.replaceAll("\t", ",")).ensuring {
      out.close()
      true
    }
  }

  def getTableName(code: String): String = {
    s"stock_tick_$code"
  }

  def toDB(code: String, date: LocalDateTime): Unit = {
    DataBase.getEngine.toDB(getTableName(code), fromCSV(code, date).toList.map(Tick.tickToRow(code, _)))
  }

  def fromDB(code: String, s: LocalDateTime, e: LocalDateTime): List[Tick] = {
    val rows = DataBase.getEngine.fromDB(getTableName(code), start = s, stop = e)
    rows.map(Tick.rowToTick)
  }

}