package com.squant.cheetah.datasource

import java.io.{File, FileNotFoundException, PrintWriter}
import java.nio.charset.MalformedInputException
import java.time.LocalDateTime

import com.squant.cheetah.domain.Category
import com.squant.cheetah.utils._
import com.squant.cheetah.utils.Constants._
import com.typesafe.scalalogging.LazyLogging
import org.jsoup.Jsoup

import scala.collection.mutable
import scala.io.Source


object THSDataSource extends App with DataSource with LazyLogging {

  //初始化数据源
  override def init(): Unit = {
    update(LocalDateTime.now, LocalDateTime.now)
  }

  def update(start: LocalDateTime, stop: LocalDateTime) = {
    //下载股票分类数据
    saveCategory(config.getString(CONFIG_PATH_DB_BASE) + config.getString(CONFIG_PATH_CATEGORY))
  }

  override def clear(): Unit = {
    rm(config.getString(CONFIG_PATH_DB_BASE) + config.getString(CONFIG_PATH_CATEGORY))
      .foreach(r => logger.info(s"delete ${r._1} ${r._2}"))
  }

  private def getSource(url: String, encode: String): Option[String] = {
    for (i <- 1 to 3)
      try {
        return Some(Source.fromURL(url, encode).mkString)
      } catch {
        case ex: FileNotFoundException => if (i == 3) logger.error("page not found:" + url)
        case ex: Exception => if (i == 3) logger.error("fail to get source:" + url, ex.getMessage)
      }
    None
  }

  def hy(): Map[String, Category] = {
    val map = mutable.HashMap[String, Category]()
    (1 to 5).foreach { pageNum =>
      val url = s"http://data.10jqka.com.cn/funds/hyzjl/field/tradezdf/order/desc/page/$pageNum/ajax/1/"
      urlToCategoryMap(map, url, "hy")
    }
    map.toMap[String, Category]
  }

  def gn(): Map[String, Category] = {
    val map = mutable.HashMap[String, Category]()
    (1 to 10).foreach { pageNum =>
      val url = s"http://data.10jqka.com.cn/funds/gnzjl/board/1/field/tradezdf/order/desc/page/$pageNum/ajax/1/"
      urlToCategoryMap(map, url, "gn")
    }
    map.toMap[String, Category]
  }

  def urlToCategoryMap(map: mutable.HashMap[String, Category], url: String, categoryType: String): Unit = {
    getSource(url, "gbk") match {
      case source: Some[String] => {
        val elements = Jsoup.parse(source.get).select("table tbody").get(0).select("tr").iterator()
        while (elements.hasNext) {
          val tds = elements.next().select("td")
          val url = tds.select("a").get(0).attr("href")
          val gn = parseToCategory(
            tds.get(1).text(),
            tds.select("a").get(0).attr("href"), categoryType)
          map.put(gn.name, gn)
        }
      }
      case None => logger.error("unable to get gn list page source from:" + url)
    }
  }

  private def getHYCode(source: String): String = {
    Jsoup.parse(source).select("div[class='board-hq'] span").get(0).text().replaceAll("[（）]", "")
  }

  private def getGNCode(source: String): String = {
    Jsoup.parse(source).select("div[class='board-hq'] span").get(0).text()
  }

  private def fetchStockFromCode(code: String): List[String] = {
    //http://m.10jqka.com.cn/hq/industry.html#code=885738
    getSource(s"http://d.10jqka.com.cn/v2/blockrank/$code/199112/d300.js", "gbk") match {
      case source: Some[String] => {
        val set = mutable.Set[String]()
        val regex = """5\":\"([0-9]{6})""".r
        regex.findAllMatchIn(source.get).foreach {
          machi => set.add(machi.group(1))
        }
        set.toList
      }
      case None => List[String]()
    }
  }

  def parseToCategory(name: String, url: String, categoryType: String): Category = {
    val pinyin = categoryType match {
      case "gn" => url.replace("http://q.10jqka.com.cn/stock/gn/", "").replace("/", "")
      case "hy" => url.replace("http://q.10jqka.com.cn/stock/thshy/", "").replace("/", "")
    }
    getSource(url, "gbk") match {
      case source: Some[String] => {
        val code = if (categoryType == "gn") getGNCode(source.get) else getHYCode(source.get)
        if (code == "") {
          logger.warn("can't get code from gn detail page:" + url)
          Category(name, "", categoryType, pinyin, url, List[String]())
        } else {
          Category(name, code, categoryType, pinyin, url, fetchStockFromCode(code))
        }
      }
      case None => Category(name, "", categoryType, pinyin, url, List[String]())
    }
  }

  def saveCategory(file: String): Unit = {
    val writer = new PrintWriter(new File(file))
    val map: Map[String, Category] = hy() ++ gn()

    map.foreach(item => {
      val stocks = item._2.stocks.foldLeft("") { (a, b) =>
        a + "_" + b
      }
      writer.write(s"${item._1},${item._2.code},${item._2.categoryType},${item._2.pinyin},${item._2.url},$stocks\n")
    })
    writer.close()
  }

  def readCategory(file: String): Map[String, Category] = {
    val lines = Source.fromFile(file).getLines()
    val map = mutable.Map[String, Category]()
    for (line <- lines) {
      val fields = line.split(",", 6)
      map.put(fields(0), Category(fields(0), fields(1), fields(2), fields(3), fields(4), fields(5).split("_").filter(_ != "").toList))
    }
    map.toMap
  }
}

