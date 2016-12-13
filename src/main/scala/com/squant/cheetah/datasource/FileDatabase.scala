package com.squant.cheetah.datasource

import java.io.FileWriter
import java.time.{LocalDateTime, LocalTime}
import java.time.format.DateTimeFormatter

import com.squant.cheetah.domain.{Tick, TickType, _}

import scala.io.Source

import scala.sys.process._

class FileDatabase{
  def symbols: Seq[Symbol] = parseCSVToSymbols("/data/stocks.csv")

  def realtime(code: String): Unit = {
    val sinaCode: String = code match {
      case code if code.startsWith("6") => "sh" + code
      case code if code.startsWith("3") || code.startsWith("0") => "sz" + code
    }
    val source = Source.fromURL("http://hq.sinajs.cn/list=" + sinaCode, "GBK").mkString
    if (source.contains("\"")) {
      val array = source.substring(source.indexOf("\"") + 1, source.lastIndexOf("\"")).split(",")
    }
  }

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

  private def writeTick(code: String, date: LocalDateTime) = {

    val formatDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    val out = new FileWriter(s"/data/tick/$code-$formatDate.csv", false)

    out.write(Source.fromURL(String.format(tickDayDataURL, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), Symbol.getSymbol(code, tickDayDataURL)), "gbk").mkString.replaceAll("\t", ",")).ensuring {
      out.close()
      true
    }
  }

  writeTick("600199", LocalDateTime.now().plusDays(-1))
}
