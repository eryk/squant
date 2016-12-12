package com.squant.cheetah.datasource

import java.io.{FileWriter, InputStreamReader, PrintWriter}
import java.time.{LocalDateTime, LocalTime}
import java.time.format.DateTimeFormatter
import java.util.Date

import com.squant.cheetah.domain._

import scala.sys.process._
import scala.io.Source

object DataBase extends App {
  def symbols: Seq[Symbol] = parseCSVToSymbols("/data/stocks.csv")

  def init() = {
    "python3.5 " + getProjectDir() + "/script/Download.py stocks" !;
    "python3.5 " + getProjectDir() + "/script/Download.py ktype" !;
  }

  //http://vip.stock.finance.sina.com.cn/quotes_service/view/vMS_tradehistory.php?symbol=sh600199&date=2015-11-16
  private val tickDayDataURL = "http://market.finance.sina.com.cn/downxls.php?date=%s&symbol=%s"

  def tick(code: String, date: LocalDateTime): List[Tick] = {
    val lines = Source.fromURL(String.format(tickDayDataURL, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), Symbol.getSymbol(code, tickDayDataURL)), "gbk").getLines().drop(1)
    lines.map {
      line =>
        val fields = line.split("\t", 6)
        Tick(LocalTime.parse(fields(0), DateTimeFormatter.ofPattern("HH:mm:ss")), fields(1).toFloat, fields(3).toInt, fields(4).toDouble, TickType.from(fields(5)))
    }.toList.reverse
  }

  def writeTick(code: String, date: LocalDateTime) = {

    val formatDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    val out = new FileWriter(s"/data/tick/$code-$formatDate.csv",false)

    out.write(Source.fromURL(String.format(tickDayDataURL, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), Symbol.getSymbol(code, tickDayDataURL)), "gbk").mkString.replaceAll("\t", ",")).ensuring {
      out.close()
      true
    }
  }
  writeTick("600199", LocalDateTime.now().plusDays(-1))
}
