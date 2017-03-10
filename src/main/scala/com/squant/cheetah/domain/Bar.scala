package com.squant.cheetah.domain

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.squant.cheetah.utils.Constants._
import com.squant.cheetah.utils._

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

case class Bar(barType: BarType, date: LocalDateTime, code: String, name: String, close: Float, high: Float,
               low: Float, open: Float,lastClose: Float, p_change: Float, a_change: Float, volume: Float,
               amount: Float, turnover: Float, mktcap: Float, nmc: Float)

object Bar extends App {

  val baseDir = config.getString(CONFIG_PATH_DB_BASE)
  val ktypeDir = config.getString(CONFIG_PATH_KTYPE)

  import com.squant.cheetah.utils._

  def typeToPath(barType: BarType): String = {
    val ktype = Map("MIN_5" -> "5", "MIN_15" -> "15", "MIN_30" -> "30", "MIN_60" -> "60", "DAY" -> "day", "WEEK" -> "week", "MONTH" -> "month")
    ktype.get(barType.toString).get
  }

  private def sumTick(ticks: Seq[Tick]): Bar = ???

  def toBar(ticks: Seq[Tick], barType: BarType): Seq[Bar] = barType match {

    case barType if (barType == MIN_5) => {
      ticks.groupBy {
        tick => tick.date.format(formatter) //TODO 对齐时间
      }.map(
        item => sumTick(item._2)
      ).toSeq
    }
    case barType if (barType == MIN_15) => Seq[Bar]()
    case barType if (barType == MIN_30) => Seq[Bar]()
    case barType if (barType == MIN_60) => Seq[Bar]()
    case barType if (barType == DAY) => Seq[Bar]()
  }

  /**
    *
    * @param code
    * @param ktype
    * @param index if true,return index data ,else return stock data
    * @return
    */
  def parseCSVToBars(code: String, ktype: BarType, index: Boolean = false): Seq[Bar] = {

    def mapToStock(map: Map[String, String]): Bar = new Bar(
      ktype,
      map.get("date").get,
      map.get("code").get,
      map.get("name").getOrElse(""),
      map.get("close").getOrElse("0").toFloat,
      map.get("high").getOrElse("0").toFloat,
      map.get("low").getOrElse("0").toFloat,
      map.get("open").getOrElse("0").toFloat,
      map.get("lastClose").getOrElse("0").toFloat,
      map.get("p_change").getOrElse("0").toFloat,
      map.get("a_change").getOrElse("0").toFloat,
      map.get("volume").getOrElse("0").toFloat,
      map.get("amount").getOrElse("0").toFloat,
      map.get("turnover").getOrElse("0").toFloat,
      map.get("mktcap").getOrElse("0").toFloat,
      map.get("nmc").getOrElse("0").toFloat
    )

    var file: String = ""
    if (index) {
      file = s"$baseDir$ktypeDir/${typeToPath(ktype)}/index/$code.csv"
    } else {
      file = s"$baseDir$ktypeDir/${typeToPath(ktype)}/stock/$code.csv"
    }
    val lines = scala.io.Source.fromFile(new File(file)).getLines().toList.drop(1)
    val columns = ktype match {
      case MIN_1 | MIN_5 | MIN_15 | MIN_30 | MIN_60 => minStockColumns
      case DAY | WEEK | MONTH =>
        if (index)
          dayIndexColumns
        else
          dayStockColumns
    }

    for {
      line <- lines
      fields = line.replaceAll("None", "0").split(",")
      if (fields.length == 15 || fields.length == 12 || fields.length == 7)
      map = (columns zip fields) (breakOut): Map[String, String]
    } yield mapToStock(map)
  }

  val stocks = parseCSVToBars("000001", MIN_15)
  stocks.foreach(println)
}