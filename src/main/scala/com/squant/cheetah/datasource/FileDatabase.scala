package com.squant.cheetah.datasource

import java.io.FileWriter
import java.time.{LocalDateTime, LocalTime}
import java.time.format.DateTimeFormatter

import com.squant.cheetah.domain.{Tick, TickType, _}

import scala.io.Source

import scala.sys.process._

class FileDatabase extends FinanceDB {

  override def init() = {
    "python3.5 " + getProjectDir() + "/script/Download.py stocks" !;
    "python3.5 " + getProjectDir() + "/script/Download.py ktype" !;
    THSData.saveGN("/data/gn.csv")
  }

  private def writeTick(code: String, date: LocalDateTime) = {
    val tickDayDataURL = "http://market.finance.sina.com.cn/downxls.php?date=%s&symbol=%s"
    val formatDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    val out = new FileWriter(s"/data/tick/$code-$formatDate.csv", false)

    out.write(Source.fromURL(String.format(tickDayDataURL, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), Symbol.getSymbol(code, tickDayDataURL)), "gbk").mkString.replaceAll("\t", ",")).ensuring {
      out.close()
      true
    }
  }

  //包含当天数据，内部做数据的聚合
  override def ktype(code: String, kType: BarType, start: LocalDateTime, stop: LocalDateTime): Unit = ???

  //地区、概念、行业
  override def category(): Map[String, Map[String, Seq[String]]] = ???

  override def getFundamentals(code: String): Unit = ???

  override def updateAll(start: LocalDateTime, stop: LocalDateTime): Boolean = ???

  override def update(code: String, start: LocalDateTime, stop: LocalDateTime): Unit = ???
}

object FileDatabase extends App {
  val db = new FileDatabase
  val realTime = db.realtime("600133")
  println(realTime)
}