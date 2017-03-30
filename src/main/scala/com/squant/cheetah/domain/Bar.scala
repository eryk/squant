package com.squant.cheetah.domain

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.squant.cheetah.engine.Row
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
               low: Float, open: Float, lastClose: Float, p_change: Float, a_change: Float, volume: Float,
               amount: Float, turnover: Float, mktcap: Float, nmc: Float)

object Bar {

  import com.squant.cheetah.utils._

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


  def barToRow(bar: Bar): Row = {
    val map = scala.collection.mutable.HashMap[String, String]()
    map.put("barType", bar.barType.toString)
    map.put("code", bar.code.toString)
    map.put("name", bar.name.toString)
    map.put("close", bar.close.toString)
    map.put("high", bar.high.toString)
    map.put("low", bar.low.toString)
    map.put("open", bar.open.toString)
    map.put("lastClose", bar.lastClose.toString)
    map.put("p_change", bar.p_change.toString)
    map.put("a_change", bar.a_change.toString)
    map.put("volume", bar.volume.toString)
    map.put("amount", bar.amount.toString)
    map.put("turnover", bar.turnover.toString)
    map.put("mktcap", bar.mktcap.toString)
    map.put("nmc", bar.nmc.toString)

    Row(bar.code + "_" + format(bar.date, "yyyyMMddHHmmss"), localDateTimeToLong(bar.date), map.toMap)
  }

  def minuteBarToRow(bar: Bar): Row = {
    val map = scala.collection.mutable.HashMap[String, String]()
    map.put("barType", bar.barType.toString)
    map.put("close", bar.close.toString)
    map.put("high", bar.high.toString)
    map.put("low", bar.low.toString)
    map.put("open", bar.open.toString)
    map.put("volume", bar.volume.toString)

    Row(bar.code + format(bar.date, "yyyyMMddHHmmss"), localDateTimeToLong(bar.date), map.toMap)
  }

  def minuteRowToBar(row: Row): Bar = {
    val map = row.record
    val code = row.index.substring(0, 6)

    new Bar(
      stringToBarType(map.get("barType").get),
      longToLocalDateTime(row.timestamp),
      code,
      "",
      map.get("close").get.toFloat,
      map.get("high").get.toFloat,
      map.get("low").get.toFloat,
      map.get("open").get.toFloat,
      0,
      0,
      0,
      map.get("volume").get.toFloat,
      0,
      0,
      0,
      0
    )
  }

  def stringToBarType(ktype: String) = ktype match {
    case barType if (barType == "SEC_1") => SEC_1
    case barType if (barType == "MIN_1") => MIN_1
    case barType if (barType == "MIN_5") => MIN_5
    case barType if (barType == "MIN_15") => MIN_15
    case barType if (barType == "MIN_30") => MIN_30
    case barType if (barType == "MIN_60") => MIN_60
    case barType if (barType == "DAY") => DAY
    case barType if (barType == "WEEK") => WEEK
    case barType if (barType == "MONTH") => MONTH
  }

  def rowToBar(row: Row): Bar = {
    val map = row.record

    new Bar(
      stringToBarType(map.get("barType").get),
      longToLocalDateTime(row.timestamp),
      map.get("code").get,
      map.get("name").get,
      map.get("close").get.toFloat,
      map.get("high").get.toFloat,
      map.get("low").get.toFloat,
      map.get("open").get.toFloat,
      map.get("lastClose").get.toFloat,
      map.get("p_change").get.toFloat,
      map.get("a_change").get.toFloat,
      map.get("volume").get.toFloat,
      map.get("amount").get.toFloat,
      map.get("turnover").get.toFloat,
      map.get("mktcap").get.toFloat,
      map.get("nmc").get.toFloat
    )
  }

  def minuteCSVToBar(ktype: BarType, line: String): Option[Bar] = {
    val fields = line.split(",")
    if (fields.length == 7) {
      Some(Bar(
        ktype,
        stringToDate(fields(0)),
        fields(1),
        "",
        fields(5).toFloat, //close
        fields(3).toFloat, //high
        fields(4).toFloat, //low
        fields(2).toFloat, //open
        0.0f,
        0.0f,
        0.0f,
        fields(6).toFloat,
        0.0f,
        0.0f,
        0.0f,
        0.0f
      ))
    } else {
      None
    }
  }
}