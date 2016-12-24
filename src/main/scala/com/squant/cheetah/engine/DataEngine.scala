package com.squant.cheetah.engine

import java.io.File
import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, LocalTime}

import com.squant.cheetah.datasource.THSDataSource
import com.squant.cheetah.domain._
import com.squant.cheetah.utils.Constants._
import com.squant.cheetah.utils._

import scala.io.Source

class DataEngine(context: Context) {

  private val clock = context.clock

  //获取历史数据
  def getStockData(code: String, //股票代码
                   count: Int, //数量, 返回的结果集的行数
                   frequency: BarType = DAY //单位时间长度
                  ): List[Bar] = {
    val now: LocalDateTime = clock.now()
    val start = frequency match {
      case SEC_1 => now.plusSeconds(-count)
      case MIN_1 => now.plusMinutes(-count)
      case MIN_5 => now.plusMinutes(-count * 5)
      case MIN_15 => now.plusMinutes(-count * 15)
      case MIN_30 => now.plusMinutes(-count * 30)
      case MIN_60 => now.plusMinutes(-count * 60)
      case DAY => now.plusDays(-count)
      case WEEK => now.plusWeeks(-count)
      case MONTH => now.plusMonths(-count)
    }
    DataEngine.ktype(code, frequency, start, now)
  }

  //获取基金净值/期货结算价等
  def getExtras() = ???

  //查询财务数据
  def getFundamentals(code: String, startDate: LocalDateTime) = ???
}

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
    val bars = Bar.parseCSVToBars(code, kType)
    bars.takeWhile(bar => bar.date.isAfter(start) && bar.date.isBefore(stop)).toList
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