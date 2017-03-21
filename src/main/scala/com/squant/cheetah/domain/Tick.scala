package com.squant.cheetah.domain

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, LocalTime}

import com.squant.cheetah.engine.Row
import com.squant.cheetah.utils._

object TickType {
  def from(name: String): TickType = name match {
    case "买盘" => BUY
    case "卖盘" => SELL
    case "中性盘" => MID
  }

}

sealed class TickType() {

  override def toString: String = {
    if (this == BUY) return "买盘"
    else if (this == SELL) return "卖盘"
    else return "中性盘"
  }
}

//买盘
case object BUY extends TickType

//卖盘
case object MID extends TickType

//中性盘
case object SELL extends TickType

case class Tick(
                 date: LocalDateTime, //时间
                 price: Double, //成交价格  单位：元
                 volume: Int, //成交量   单位：手
                 amount: Double, //成交额：元
                 tickType: TickType
               ) {
  override def toString: String = "Tick{" + "时间='" + date.format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")) + '\'' + ", 价格=" + price + ", 成交量=" + volume + ", 成交额=" + amount + ", 类型=" + `tickType` + '}'
}

object Tick {

  def tickToRow(code: String, tick: Tick): Row = {
    val map = scala.collection.mutable.HashMap[String, String]()
    map.put("price", tick.price.toString)
    map.put("volume", tick.volume.toString)
    map.put("amount", tick.amount.toString)
    map.put("tickType", tick.tickType.toString)
    Row(code + "_" + format(tick.date, "yyyyMMddHHmmss"), localDateTimeToLong(tick.date), map.toMap)
  }

  def rowToTick(row: Row): Tick = {
    val map = row.record

    Tick(
      longToLocalDateTime(row.timestamp),
      map.get("price").get.toDouble,
      map.get("volume").get.toInt,
      map.get("volume").get.toDouble,
      TickType.from(map.get("tickType").get)
    )
  }
}