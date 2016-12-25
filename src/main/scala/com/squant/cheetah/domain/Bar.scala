package com.squant.cheetah.domain

import java.io.File
import java.time.LocalDateTime

import scala.collection._


sealed trait BarType

case object SEC_1 extends BarType

case object MIN_1 extends BarType

case object MIN_5 extends BarType

case object MIN_15 extends BarType

case object MIN_30 extends BarType

case object MIN_60 extends BarType

case object DAY extends BarType

case object WEEK extends BarType

case object MONTH extends BarType

case class Bar(barType: BarType, date: LocalDateTime, open: Float, close: Float, high: Float, low: Float, volume: Float, code: String)

object Bar extends App {

  def typeToPath(barType: BarType): String = {
    val ktype = Map("MIN_5" -> "5", "MIN_15" -> "15", "MIN_30" -> "30", "MIN_60" -> "60", "DAY" -> "day", "WEEK" -> "week", "MONTH" -> "month")
    ktype.get(barType.toString).get
  }

  /**
    *
    * @param code
    * @param ktype
    * @param index if true,return index data ,else return stock data
    * @return
    */
  def parseCSVToBars(code: String, ktype: BarType, index:Boolean = false): Seq[Bar] = {

    def mapToStock(map: Map[String, String]): Bar = new Bar(
      ktype,
      map.get("date").get,
      map.get("open").get.toFloat,
      map.get("close").get.toFloat,
      map.get("high").get.toFloat,
      map.get("low").get.toFloat,
      map.get("volume").get.toFloat,
      map.get("code").get
    )

    var file:String =  ""
    if(index){
      file = s"/data/index/$code.csv"
    }else{
      file = s"/data/${typeToPath(ktype)}/$code.csv"
    }
    val lines = scala.io.Source.fromFile(new File(file)).getLines().toList
    for {
      line <- lines
      fields = line.split(",")
      if (fields.length == 8)
      map = (stockColumns zip fields) (breakOut): Map[String, String]
    } yield mapToStock(map)
  }
  
  val stocks = parseCSVToBars("002173", MIN_30)
  stocks.foreach(println)
}