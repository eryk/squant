package com.squant.cheetah

import java.io.File
import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, LocalTime}

import com.squant.cheetah.Constants._
import com.squant.cheetah.datasource.THSDataSource
import com.squant.cheetah.domain._
import com.squant.cheetah.utils._

import scala.io.Source

//https://www.joinquant.com/data
//https://www.joinquant.com/data/dict/fundamentals
object DataEngine extends App{

  def realtime(code: String): Option[RealTime] = {
    val url = "http://hq.sinajs.cn/list="
    val source = Source.fromURL(url + Symbol.getSymbol(code, url), "GBK").mkString
    val array = source.substring(source.indexOf("\"") + 1, source.lastIndexOf("\"")).split(",")
    Some(RealTime.arrayToRealTime(array))
  }

  def tick(code: String, date: LocalDateTime): List[Tick] = {
    val lines = Source.fromFile(new File(s"/data/tick/${date.format(DateTimeFormatter.ofPattern("yyyyMMdd"))}/$code.csv")).getLines().drop(1)
    lines.map {
      line =>
        val fields = line.split(",", 6)
        Tick(LocalTime.parse(fields(0), DateTimeFormatter.ofPattern("HH:mm:ss")), fields(1).toFloat, fields(3).toInt, fields(4).toDouble, TickType.from(fields(5)))
    }.toList.reverse
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

  tick("002816",LocalDateTime.now().plusDays(-2)).foreach(println)
}