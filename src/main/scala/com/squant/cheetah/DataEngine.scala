package com.squant.cheetah

import java.time.LocalDateTime
import java.util.Date

import com.squant.cheetah.datasource.{DailyKTypeDataSource, StockCategoryDataSource, TickDataSource}
import com.squant.cheetah.domain._
import com.squant.cheetah.engine.Context
import com.squant.cheetah.utils.Constants._
import com.squant.cheetah.utils._

import scala.io.Source

class DataEngine(context: Context) {

  //获取历史数据
  def getHistoryData(code: String, //股票代码
                     count: Int, //数量, 返回的结果集的行数
                     frequency: BarType = DAY, //单位时间长度
                     index: Boolean = false): List[Bar] = {
    val now: LocalDateTime = context.clock.now()
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
    DataEngine.ktype(code, frequency, start, now, index)
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

  def realtime2(code: String, date: LocalDateTime = LocalDateTime.now()): RealTime2 = {
    val url = "http://nuff.eastmoney.com/EM_Finance2015TradeInterface/JS.ashx?id=%s&_=%s"

    def getPath(symbol: String): String = {
      val date: Date = new Date
      return url.format(Symbol.getSymbol(symbol, url), date.getTime)
    }

    val source = Source.fromURL(getPath(code), "utf8").mkString
    val array = source.substring(source.indexOf("Value") + 8, source.lastIndexOf("]")).replaceAll("\"", "").split(",")
    RealTime.arrayToRealTime2(array)
  }

  def tick(code: String, date: LocalDateTime): Seq[Tick] = {
    TickDataSource.fromCSV(code, date)
  }

  //包含当天数据，内部做数据的聚合
  def ktype(code: String, kType: BarType, start: LocalDateTime = LocalDateTime.now().plusYears(-1), stop: LocalDateTime = LocalDateTime.now(), index: Boolean = false): List[Bar] = {
    val bars = DailyKTypeDataSource.fromCSV(code, kType, index)
    bars.filter(bar => bar.date.isAfter(start) && bar.date.isBefore(stop)).toList
  }

  //地区、概念、行业
  def category(): Map[String, Category] = {
    StockCategoryDataSource.readCategory(config.getString(CONFIG_PATH_DB_BASE) + config.getString(CONFIG_PATH_CATEGORY))
  }

  def symbols(): Seq[Symbol] = Symbol.csvToSymbols(config.getString(CONFIG_PATH_DB_BASE) + config.getString(CONFIG_PATH_STOCKS))

  def getFundamentals(code: String) = ???

  //获取指数成份股
  def getIndexStocks() = ???

  def getSymbolInfo(code: String): Symbol = symbols().filter(_.code == code)(0)

}