package com.squant.cheetah

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import scala.collection.{Seq, _}

package object domain {

  val stockColumns = List("index", "date", "open", "close", "high", "low", "volume", "code")

  def parseCSVToSymbols(file: String): Seq[Symbol] = {
    def mapToSymbol(map: Map[String, String]): Symbol =
      new Symbol(
        map.get("code").get, //代码
        map.get("name").get, //名称
        map.get("industry").get, //所属行业
        map.get("area").get, //地区
        map.get("pe").get.toFloat, //市盈率
        map.get("outstanding").get.toFloat, //流通股本
        map.get("totals").get.toFloat, //总股本(万)
        map.get("totalAssets").get.toFloat, //总资产(万)
        map.get("liquidAssets").get.toFloat, //流动资产
        map.get("fixedAssets").get.toFloat, //固定资产
        map.get("reserved").get.toFloat, //公积金
        map.get("reservedPerShare").get.toFloat, //每股公积金
        map.get("esp").get.toFloat, //每股收益
        map.get("bvps").get.toFloat, //每股净资
        map.get("pb").get.toFloat, //市净率
        map.get("timeToMarket").get, //上市日期
        map.get("undp").get.toFloat, //未分利润
        map.get("perundp").get.toFloat, //每股未分配
        map.get("rev").get.toFloat, //收入同比(%)
        map.get("profit").get.toFloat, //利润同比(%)
        map.get("gpr").get.toFloat, //毛利率(%)
        map.get("npr").get.toFloat, //净利润率(%)
        map.get("holders").get.toLong //股东人数
      )

    val lines = scala.io.Source.fromFile(new File(file)).getLines().toList

    for {
      line <- lines.slice(1, lines.length + 1)
      fields = line.split(",")
      if (fields.length == 23)
      map = (lines(0).split(",") zip fields) (breakOut): Map[String, String]
    } yield mapToSymbol(map)
  }

  implicit def stringToDate(date: String): LocalDateTime = {
    if(date.length == 10){
      val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
      LocalDateTime.parse(date,formatter)
    }else{
      val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
      LocalDateTime.parse(date,formatter)
    }
  }

  def getProjectDir(): String = {
    new File("").getAbsolutePath
  }
}
