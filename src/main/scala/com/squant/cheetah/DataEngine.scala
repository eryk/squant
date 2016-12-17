package com.squant.cheetah

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, LocalTime}

import com.squant.cheetah.Constants._
import com.squant.cheetah.datasource.{GN, THSDataSource}
import com.squant.cheetah.domain._
import com.squant.cheetah.utils._

import scala.io.Source

//https://www.joinquant.com/data
//https://www.joinquant.com/data/dict/fundamentals
object DataEngine {

  def realtime(code: String): Option[RealTime] = {
    val url = "http://hq.sinajs.cn/list="
    val source = Source.fromURL(url + Symbol.getSymbol(code, url), "GBK").mkString
    val array = source.substring(source.indexOf("\"") + 1, source.lastIndexOf("\"")).split(",")
    Some(RealTime.arrayToRealTime(array))
  }

  def tick(code: String, date: LocalDateTime): List[Tick] = {
    val tickDayDataURL = "http://market.finance.sina.com.cn/downxls.php?date=%s&symbol=%s"
    val lines = Source.fromURL(String.format(tickDayDataURL, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), Symbol.getSymbol(code, tickDayDataURL)), "gbk").getLines().drop(1)
    lines.map {
      line =>
        val fields = line.split("\t", 6)
        Tick(LocalTime.parse(fields(0), DateTimeFormatter.ofPattern("HH:mm:ss")), fields(1).toFloat, fields(3).toInt, fields(4).toDouble, TickType.from(fields(5)))
    }.toList.reverse
  }

  //包含当天数据，内部做数据的聚合
  def ktype(code: String, kType: BarType, start: LocalDateTime = LocalDateTime.now().plusYears(-1), stop: LocalDateTime = LocalDateTime.now()): List[Bar] = {
    Bar.parseCSVToBars(code, kType).takeWhile(bar => bar.date.isAfter(start) && bar.date.isBefore(stop)).toList
  }

  //地区、概念、行业
  def category(): Map[String, GN] = {
    THSDataSource.readGN(config.getString(CONFIG_PATH_DB_BASE) + config.getString(CONFIG_PATH_GN))
  }

  def symbols(): Seq[Symbol] = parseCSVToSymbols(config.getString(CONFIG_PATH_DB_BASE) + config.getString(CONFIG_PATH_STOCKS))

  def getFundamentals(code: String) = ???

}