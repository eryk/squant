package com.squant.cheetah.utils

import java.util

import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

import scala.collection.mutable
import scala.io.Source

case class GN(name: String, code: String, pinyin: String, url: String, stocks: List[String])

object THSJsonDownloader extends App {

  def getSource(url: String, encode: String): Option[String] = {
    var retry = 0;
    while (retry < 3) {
      try {
        return Some(Source.fromURL(url, encode).mkString)
      } catch {
        case ex: Exception => ex.printStackTrace()
      } finally {
        retry += 1
      }
    }
    Option.empty[String]
  }

  def gn(): Option[Map[String, GN]] = {
    def getGNCode(source: String): String = {
      Jsoup.parse(source).select("div[class='stock-name fl'] span").text()
    }

    def fetchFromCode(code: String): List[String] = {
      val url = s"http://d.10jqka.com.cn/v2/blockrank/$code/199112/d300.js"
      val source = Source.fromURL(url, "gbk").mkString

      val set = mutable.Set[String]()
      val regex = """5\":\"([0-9]{6})""".r
      regex.findAllMatchIn(source).foreach {
        machi => set.add(machi.group(1))
      }
      set.toList
    }

    def parseToGN(element: Element): GN = {
      val tds = element.select("td")
      val name = tds.get(1).text()
      val url = tds.select("a").get(0).attr("href")
      val source = Source.fromURL(url, "gbk").mkString
      val code = getGNCode(source)
      val pinyin = url.replace("http://q.10jqka.com.cn/stock/gn/", "").replace("/", "")
      GN(name, code, pinyin, url, fetchFromCode(code))
    }

    val map = mutable.HashMap[String, GN]()
    (1 to 1).foreach { pageNum =>
      val url = s"http://data.10jqka.com.cn/funds/gnzjl/board/1/field/tradezdf/order/desc/page/$pageNum/ajax/1/"
      getSource(url, "gbk").get match {
        case source => {
          val elements = Jsoup.parse(source).select("table tbody").get(0).select("tr").iterator()
          while (elements.hasNext) {
            val gn = parseToGN(elements.next())
            map.put(gn.name, gn)
          }
        }
        case _ => println("unable to get source from:" + url)
      }
    }
    Some(map.toMap[String, GN])
  }

  def getStock(code: String, pinyin: String, count: Int): Option[List[String]] = {
    val regex = """stockcode\":\"([0-9]{6})""".r
    val set = mutable.Set[String]()

    count match {
      case count if (count > 1) => {
        (1 to count).foreach { pageNum =>
          val url = s"http://q.10jqka.com.cn/interface/stock/detail/zdf/desc/$pageNum/$count/$pinyin"
          val iter = regex.findAllMatchIn(Source.fromURL(url).mkString)
          iter.foreach(machi => {
            set.add(machi.group(1))
          })
        }
      }

      case count => {
        val url = s"http://q.10jqka.com.cn/stock/gn/$pinyin"
        var source: String = ""

        try {
          source = Source.fromURL(url, "gbk").mkString
        } catch {
          case ex: MalformedURIException => println("can't get source with url:" + url)
        }
        val doc = Jsoup.parse(source)
        val elements: util.Iterator[Element] = doc.select("table[class=m_table] tbody").get(0).select("tr").iterator()
        while (elements.hasNext) {
          val tds = elements.next().select("td")
          val code = tds.get(1).text()
          set.add(code)
        }
      }
    }
    Some(set.toList)
  }


  val map = gn().get
  println(map.size)
  map.foreach {
    item => println(item._2)
  }
}

