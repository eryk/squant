package com.squant.cheetah.datasource

import java.io.{File, FileWriter}
import java.time.LocalDateTime

import com.squant.cheetah.engine.DataEngine
import com.squant.cheetah.utils.Constants._
import com.squant.cheetah.utils._
import com.typesafe.scalalogging.LazyLogging

import scala.io.Source

object DailyKTypeDataSource extends App with DataSource with LazyLogging {

  val baseDir = config.getString(CONFIG_PATH_DB_BASE)
  val ktypeDir = config.getString(CONFIG_PATH_KTYPE)

  val indexURL = "http://quotes.money.163.com/service/chddata.html?code=%s&start=%s&end=%s&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;VOTURNOVER;VATURNOVER"
  val stockURL = "http://quotes.money.163.com/service/chddata.html?code=%s&start=%s&end=%s&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP"

  private val INDEX_SYMBOL = Map[String, String](
    ("000001", "0000001"), ("000002", "0000002"), ("000003", "0000003"), ("000008", "0000008"),
    ("000009", "0000009"), ("000010", "0000010"), ("000011", "0000011"), ("000012", "0000012"),
    ("000016", "0000016"), ("000017", "0000017"), ("000300", "0000300"), ("399001", "1399001"),
    ("399002", "1399002"), ("399003", "1399003"), ("399004", "1399004"), ("399005", "1399005"), ("399006", "1399006"),
    ("399100", "1399100"), ("399101", "1399101"), ("399106", "1399106"), ("399107", "1399107"), ("399108", "1399108"),
    ("399333", "1399333"), ("399606", "1399606")
  )

  //初始化数据源
  override def init(): Unit = {

  }

  //每个周期更新数据
  override def update(start: LocalDateTime = LocalDateTime.now(), stop: LocalDateTime = LocalDateTime.now()): Unit = {
    def writeData(code: String, data: Iterator[String], path: String): Unit = {
      val file = new File(s"$baseDir/$ktypeDir/day/$path/$code.csv")

      val dir = new File(s"$baseDir/$ktypeDir/day/$path")
      if (!dir.exists()) {
        dir.mkdirs()
      }

      val writer = new FileWriter(file, true)
      if (!file.exists()) {
        writer.write(new String(data.take(1).next().getBytes("utf-8")) + "\n")
      }
      val iter = data.drop(1).toList.reverse
      for (line: String <- iter) {
        writer.write(new String(line.getBytes("utf-8")) + "\n")
      }
      writer.close()
    }

    def stockCode(code: String): String = {
      if (code.length != 6)
        return ""
      else {
        val index_code = if (List("5", "6", "9").contains(String.valueOf(code.charAt(0)))) s"0$code" else s"1$code"
        return index_code
      }
    }

    for ((code, rCode) <- INDEX_SYMBOL) {
      val data = Source.fromURL(indexURL.format(rCode, format(start, "yyyyMMdd"), format(stop, "yyyyMMdd")), "gbk").getLines()
      writeData(code, data, "index")
    }

    val stocks = DataEngine.symbols()
    for (stock <- stocks) {
      val data = Source.fromURL(stockURL.format(stockCode(stock.code), format(start, "yyyyMMdd"), format(stop, "yyyyMMdd")), "gbk").getLines()
      writeData(stock.code, data, "stock")
    }
  }

  //清空数据源
  override def clear(): Unit = {
    rm(s"/$baseDir/$ktypeDir/day").foreach(r => logger.info(s"delete ${r._1} ${r._2}"))
  }
}