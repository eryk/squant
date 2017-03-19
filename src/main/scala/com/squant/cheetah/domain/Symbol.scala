package com.squant.cheetah.domain

import java.io.File
import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

import com.squant.cheetah.engine.Row
import com.squant.cheetah.utils._

import scala.collection.{Map, _}
import scala.io.Source

case class Symbol(code: String, //代码
                  name: String, //名称
                  industry: String, //所属行业
                  area: String, //地区
                  pe: Float, //市盈率
                  outstanding: Float, //流通股本
                  totals: Float, //总股本(万)
                  totalAssets: Float, //总资产(万)
                  liquidAssets: Float, //流动资产
                  fixedAssets: Float, //固定资产
                  reserved: Float, //公积金
                  reservedPerShare: Float, //每股公积金
                  esp: Float, //每股收益
                  bvps: Float, //每股净资
                  pb: Float, //市净率
                  timeToMarket: LocalDateTime, //上市日期
                  undp: Float, //未分利润
                  perundp: Float, // 每股未分配
                  rev: Float, //收入同比(%)
                  profit: Float, //利润同比(%)
                  gpr: Float, //毛利率(%)
                  npr: Float, //净利润率(%)
                  holders: Long //股东人数
                 ) {
  def getStatus(): Unit = {
    Source.fromURL("http://d.10jqka.com.cn/v2/fiverange/hs_002121/last.js")
  }
}

object Symbol extends App {
  def symbols = csvToSymbols("/data/stocks.csv")

  //  symbols.filter(_.pe < 10).filter(_.pe > 0).reverse.foreach(println)
  var symbolFilter = SymbolFilter(symbols) excludeNew (30) excludeST

  def getSymbol(code: String, url: String): String = {
    def netEaseSymbol(symbol: String): String = {
      if (symbol.length() != 6) return ""
      if (symbol.startsWith("6")) return "0" + symbol
      if (symbol.startsWith("0") || symbol.startsWith("3")) return "1" + symbol
      ""
    }

    def eastMoneyRealTimeSymbol(symbol: String): String = {
      if (symbol.length != 6) return ""
      if (symbol.startsWith("6")) return symbol + "1"
      if (symbol.startsWith("0") || symbol.startsWith("3")) return symbol + "2"
      ""
    }

    def sinaSymbol(symbol: String): String = {
      if (symbol.length != 6) return ""
      if (symbol.startsWith("0") || symbol.startsWith("3")) return "sz" + symbol
      if (symbol.startsWith("6")) return "sh" + symbol
      ""
    }

    url match {
      case url if url.contains("sinajs.cn") => sinaSymbol(code)
      case url if url.contains("sina.com") => sinaSymbol(code)
      case url if url.contains("163.com") => netEaseSymbol(code)
      case url if url.contains("ifeng.com") => sinaSymbol(code)
      case url if url.contains("ifeng.com") => sinaSymbol(code)
      case url if url.contains("nuff.eastmoney.com") => eastMoneyRealTimeSymbol(code)
      case url if url.contains("f10.eastmoney.com") => sinaSymbol(code)
      case _ => ""
    }
  }


  case class SymbolFilter(symbolList: Seq[Symbol]) {
    var symbols = scala.collection.mutable.Seq() ++ symbolList

    def strToDate(date: String): LocalDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"))

    def excludeNew(dayCount: Long): SymbolFilter = {
      SymbolFilter(
        for (symbol <- symbols if symbol.timeToMarket.plusDays(dayCount).isBefore(LocalDate.now())) yield symbol
      )
    }

    def excludeST: SymbolFilter = {
      SymbolFilter(
        for (symbol <- symbols if symbol.name.contains("ST")) yield symbol
      )
    }

    def get(): Seq[Symbol] = {
      symbols
    }
  }

  def symbolToRow(symbol: Symbol): Row = {
    val map = scala.collection.mutable.HashMap[String, String]()
    map.put("code", symbol.code) //代码
    map.put("name", symbol.name) //名称
    map.put("industry", symbol.industry) //所属行业
    map.put("area", symbol.area) //地区
    map.put("pe", symbol.pe.toString) //市盈率
    map.put("outstanding", symbol.outstanding.toString) //流通股本
    map.put("totals", symbol.totals.toString) //总股本(万)
    map.put("totalAssets", symbol.totalAssets.toString) //总资产(万)
    map.put("liquidAssets", symbol.liquidAssets.toString) //流动资产
    map.put("fixedAssets", symbol.fixedAssets.toString) //固定资产
    map.put("reserved", symbol.reserved.toString) //公积金
    map.put("reservedPerShare", symbol.reservedPerShare.toString) //每股公积金
    map.put("esp", symbol.esp.toString) //每股收益
    map.put("bvps", symbol.bvps.toString) //每股净资
    map.put("pb", symbol.pb.toString) //市净率
    map.put("timeToMarket", format(symbol.timeToMarket, "yyyyMMddhhmmss")) //上市日期
    map.put("undp", symbol.undp.toString) //未分利润
    map.put("perundp", symbol.perundp.toString) //每股未分配
    map.put("rev", symbol.rev.toString) //收入同比(%)
    map.put("profit", symbol.profit.toString) //利润同比(%)
    map.put("gpr", symbol.gpr.toString) //毛利率(%)
    map.put("npr", symbol.npr.toString) //净利润率(%)
    map.put("holders", symbol.holders.toString) //股东人数

    Row(symbol.code, localDateTimeToLong(LocalDateTime.now()), map.toMap)
  }

  def csvToSymbols(file: String): Seq[Symbol] = {

    val lines = scala.io.Source.fromFile(new File(file)).getLines().toList

    for {
      line <- lines.slice(1, lines.length + 1)
      fields = line.split(",")
      if (fields.length == 23)
      map = (lines(0).split(",") zip fields) (breakOut): Map[String, String]
    } yield mapToSymbol(map)
  }

}