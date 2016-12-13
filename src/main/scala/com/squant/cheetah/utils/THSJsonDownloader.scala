package com.squant.cheetah.utils

import org.jsoup.Jsoup

import scala.io.Source

object THSJsonDownloader extends App {
  val doc = Jsoup.parse(Source.fromURL("http://data.10jqka.com.cn/funds/gnzjl/board/20/field/tradezdf/order/desc/page/1/ajax/1/", "gbk").mkString)
  println(doc.select("table tbody").get(0))
}
