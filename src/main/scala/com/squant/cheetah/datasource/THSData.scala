package com.squant.cheetah.datasource

import java.io.{File, FileNotFoundException, PrintWriter}
import java.nio.charset.{Charset, MalformedInputException}

import com.typesafe.scalalogging.LazyLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

import scala.collection.mutable
import scala.io.Source

case class GN(name: String, code: String, pinyin: String, url: String, stocks: List[String])

object THSData extends App with LazyLogging {

  private def getSource(url: String, encode: String): Option[String] = {
    for (i <- 1 to 3)
      try {
        return Some(Source.fromURL(url, encode).mkString)
      } catch {
        case ex: MalformedInputException => if (i == 3) logger.error("fail to get source:" + url)
        case ex: FileNotFoundException => if (i == 3) logger.error("page not found:" + url)
        case ex: Exception => if (i == 3) ex.printStackTrace()
      }
    Option.empty[String]
  }

  def gn(): Map[String, GN] = {
    val map = mutable.HashMap[String, GN]()
    (1 to 10).foreach { pageNum =>
      val url = s"http://data.10jqka.com.cn/funds/gnzjl/board/1/field/tradezdf/order/desc/page/$pageNum/ajax/1/"
      getSource(url, "gbk") match {
        case source: Some[String] => {
          val elements = Jsoup.parse(source.get).select("table tbody").get(0).select("tr").iterator()
          while (elements.hasNext) {
            val tds = elements.next().select("td")
            val url = tds.select("a").get(0).attr("href")
            val gn = parseToGN(
              tds.get(1).text(),
              tds.select("a").get(0).attr("href"))
            map.put(gn.name, gn)
          }
        }
        case None => logger.error("unable to get gn list page source from:" + url)
      }
    }
    map.toMap[String, GN]
  }

  private def getGNCode(source: String): String = {
    Jsoup.parse(source).select("div[class='stock-name fl'] span").text()
  }

  private def fetchFromCode(code: String): List[String] = {
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

  def parseToGN(name: String, url: String): GN = {
    val pinyin = url.replace("http://q.10jqka.com.cn/stock/gn/", "").replace("/", "")
    getSource(url, "gbk") match {
      case source: Some[String] => {
        val code = getGNCode(source.get)
        if (code == "") {
          logger.warn("can't get code from gn detail page:" + url)
          GN(name, "", pinyin, url, List[String]())
        } else {
          GN(name, code, pinyin, url, fetchFromCode(code))
        }
      }
      case None => GN(name, "", pinyin, url, List[String]())
    }
  }

  def saveGN(file: String): Unit = {
    val writer = new PrintWriter(new File(file))
    val map: Map[String, GN] = gn()

    map.foreach(item => {
      val stocks = item._2.stocks.foldLeft("") { (a, b) =>
        a + "_" + b
      }
      writer.write(s"${item._1},${item._2.code},${item._2.pinyin},${item._2.url},$stocks\n")
    })
    writer.close()
  }

  def readGN(file: String): Map[String, GN] = {
    val lines = Source.fromFile(file).getLines()
    val map = mutable.Map[String, GN]()
    for (line <- lines) {
      val fields = line.split(",",5)
      map.put(fields(0), GN(fields(0), fields(1), fields(2), fields(3), fields(4).split("_").filter(_ != "").toList))
    }
    map.toMap
  }

  saveGN("/data/gn.csv")
//  val map = readGN("/data/gn.csv")
//  map.foreach{
//    item => println(item._2)
//  }
}

