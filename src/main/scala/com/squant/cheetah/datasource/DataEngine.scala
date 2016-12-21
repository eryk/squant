package com.squant.cheetah.datasource

import java.io.File
import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, LocalTime}

import com.squant.cheetah.utils.Constants._
import com.squant.cheetah.domain._
import com.squant.cheetah.utils._

import scala.io.Source

object DataEngine {

  def realtime(code: String, date: LocalDateTime = LocalDateTime.now()): RealTime = {
    val url = "http://hq.sinajs.cn/list="
    val source = Source.fromURL(url + Symbol.getSymbol(code, url), "GBK").mkString
    val array = source.substring(source.indexOf("\"") + 1, source.lastIndexOf("\"")).split(",")
    RealTime.arrayToRealTime(array)
  }

  def tick(code: String, date: LocalDateTime): Seq[Tick] = {
    val lines = Source.fromFile(new File(s"/data/tick/${date.format(DateTimeFormatter.ofPattern("yyyyMMdd"))}/$code.csv")).getLines().drop(1)
    lines.map {
      line =>
        val fields = line.split(",", 6)
        Tick(LocalTime.parse(fields(0), DateTimeFormatter.ofPattern("HH:mm:ss")), fields(1).toFloat, fields(3).toInt, fields(4).toDouble, TickType.from(fields(5)))
    }.toSeq.reverse
  }

  //包含当天数据，内部做数据的聚合
  def ktype(code: String, kType: BarType, start: LocalDateTime = LocalDateTime.now().plusYears(-1), stop: LocalDateTime = LocalDateTime.now()): List[Bar] = {
    Bar.parseCSVToBars(code, kType).takeWhile(bar => bar.date.isAfter(start) && bar.date.isBefore(stop)).toList
  }

  //地区、概念、行业
  def category(): Map[String, Category] = {
    THSDataSource.readCategory(config.getString(CONFIG_PATH_DB_BASE) + config.getString(CONFIG_PATH_CATEGORY))
  }

  def symbols(): Seq[Symbol] = parseCSVToSymbols(config.getString(CONFIG_PATH_DB_BASE) + config.getString(CONFIG_PATH_STOCKS))

  def getFundamentals(code: String) = ???

  //获取指数成份股
  def getIndexStocks() = ???

  def getSymbolInfo(code: String): Symbol = symbols().filter(_.code == code)(0)
}