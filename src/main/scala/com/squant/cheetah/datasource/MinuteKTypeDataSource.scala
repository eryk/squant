package com.squant.cheetah.datasource

import java.io.{File, FileWriter}
import java.time.LocalDateTime

import com.google.gson.Gson
import com.squant.cheetah.DataEngine
import com.squant.cheetah.utils.Constants._
import com.squant.cheetah.utils._
import com.typesafe.scalalogging.LazyLogging

import scala.collection.JavaConverters._
import scala.io.Source

object MinuteKTypeDataSource extends DataSource with LazyLogging {

  private var baseDir = config.getString(CONFIG_PATH_DB_BASE)
  private var ktypeDir = config.getString(CONFIG_PATH_KTYPE)
  private var ktypeSubDir = Seq("5", "15", "30", "60")

  private var INDEX_SYMBOL = Map[String, String](
    ("000001", "sh000001"), ("000002", "sh000002"), ("000003", "sh000003"), ("000008", "sh000008"),
    ("000009", "sh000009"), ("000010", "sh000010"), ("000011", "sh000011"), ("000012", "sh000012"),
    ("000016", "sh000016"), ("000017", "sh000017"), ("000300", "sh000300"), ("399001", "sz399001"),
    ("399002", "sz399002"), ("399003", "sz399003"), ("399004", "sz399004"), ("399005", "sz399005"), ("399006", "sz399006"),
    ("399100", "sz399100"), ("399101", "sz399101"), ("399106", "sz399106"), ("399107", "sz399107"), ("399108", "sz399108"),
    ("399333", "sz399333"), ("399606", "sz399606")
  )

  //(sh|sz)code,frequence
  private def mURL = "http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/" +
    "CN_MarketData.getKLineData?symbol=%s&scale=%s&ma=no&datalen=1023"

  //初始化数据源
  override def init(): Unit = {
    clear()
    update(start = LocalDateTime.of(1990, 1, 1, 0, 0))
  }

  def update(start: LocalDateTime = LocalDateTime.now(), stop: LocalDateTime = LocalDateTime.now()) = {
    def url(code: String, idx: Boolean = false, kType: String): String = {
      mURL.format(code, kType)
    }

    def getCode(code: String, index: Boolean = false): String = {
      if (index)
        return INDEX_SYMBOL.get(code).getOrElse("")

      if (code.length != 6)
        return ""
      else {
        val index_code = if (List("5", "6", "9").contains(String.valueOf(code.charAt(0)))) s"sh$code" else s"sz$code"
        return index_code
      }
    }

    def jsonParser(string: String): Seq[Map[String, String]] = {
      try {
        val gson = new Gson()
        val map = gson.fromJson(string, classOf[java.util.List[java.util.Map[String, String]]])
        for {elem <- map.asScala.toList
             item = elem.asScala.toMap
        } yield item
      } catch {
        case ex: Exception => {
          logger.error("fail to parse json. %s".format(string))
          Seq[Map[String, String]]()
        }
      }
    }

    def writeData(code: String, data: Iterator[String], ktype: String, path: String): Unit = {
      val file = new File(s"$baseDir/$ktypeDir/$ktype/$path/$code.csv")

      val dir = new File(s"$baseDir/$ktypeDir/$ktype/$path")
      if (!dir.exists()) {
        dir.mkdirs()
      }
      val isNewFile = !file.exists();

      val writer = new FileWriter(file, true)

      if (isNewFile) {
        writer.write("date,code,open,high,low,close,volume" + "\n")
      }
      val iter = data.drop(1).toList.reverse
      for (line: String <- iter) {
        writer.write(new String(line.getBytes("utf-8")) + "\n")
      }
      writer.close()
    }

    //update index minute data
    for ((c, rCode) <- INDEX_SYMBOL) {
      for (k <- ktypeSubDir) {
        val content = downloadWithRetry(url(code = getCode(c, index = true), kType = k), "gb2312")
        if (content != None && content != "") {
          val data = jsonParser(content).map(item => {
            s"${item.get("day").get},$c,${item.get("open").get},${item.get("high").get},${item.get("low").get},${item.get("close").get},${item.get("volume").get}"
          })
          writeData(c, data.toList.reverse.toIterator, k, "index")
        } else {
          logger.error(s"fail to download source. code=$c")
        }
      }
    }

    val symbols = DataEngine.symbols()
    for (symbol <- symbols) {
      for (k <- ktypeSubDir) {
        val content = downloadWithRetry(url(code = getCode(symbol.code), kType = k), "gb2312")
        if (content != None && content != "") {
          val data = jsonParser(content).map(item => {
            s"${item.get("day").get},${symbol.code},${item.get("open").get},${item.get("high").get},${item.get("low").get},${item.get("close").get},${item.get("volume").get}"
          })
          writeData(symbol.code, data.toList.reverse.toIterator, k, "stock")
        } else {
          logger.error(s"fail to download source. code=${symbol.code}")
        }
      }
    }
  }

  override def clear(): Unit = {
    ktypeSubDir.foreach(file =>
      rm(s"$baseDir/$ktypeDir/$file").foreach(r => logger.info(s"delete ${r._1} ${r._2}")))
  }
}