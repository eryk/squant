package com.squant.cheetah.datasource

import java.io.{File, FileWriter}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.squant.cheetah.DataEngine
import com.squant.cheetah.domain.{MIN_5, Symbol}
import com.typesafe.scalalogging.LazyLogging

import scala.io.Source

object SinaDataSource extends DataSource with LazyLogging{

  private def writeTick(code: String, date: LocalDateTime) = {
    val tickDayDataURL = "http://market.finance.sina.com.cn/downxls.php?date=%s&symbol=%s"
    val formatDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    if(!new File(s"/data/tick/$formatDate").exists()){
      new File(s"/data/tick/$formatDate").mkdirs()
    }
    val out = new FileWriter(s"/data/tick/$formatDate/$code.csv", false)
    out.write(Source.fromURL(String.format(tickDayDataURL, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), Symbol.getSymbol(code, tickDayDataURL)), "gbk").mkString.replaceAll("\t", ",")).ensuring {
      out.close()
      true
    }
  }

  override def update(): Unit = {
    val stocks = DataEngine.symbols()

    stocks.foreach(symbol => {
      writeTick(symbol.code,LocalDateTime.now())
    })
  }

  override def clear(): Unit = ???
}

object StockDataSource extends App{
//  SinaDataSource.update()
  TushareDataSource.update()
//  DataEngine.ktype("002737",MIN_5).foreach(println)
}