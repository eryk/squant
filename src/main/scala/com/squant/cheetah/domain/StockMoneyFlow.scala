package com.squant.cheetah.domain

import java.time.LocalDateTime

import com.squant.cheetah.engine.Row
import com.squant.cheetah.utils._

case class StockMoneyFlow(
                           date: LocalDateTime, //0   日期
                           close: Float, //1   收盘价
                           change: Float, //2   涨跌幅
                           turnover: Float, //3   换手率
                           inflowAmount: Float, //4   资金流入（万元）
                           outflowAmount: Float, //5   资金流出（万元）
                           netInflowAmount: Float, //6   净流入（万元）
                           mainInflowAmount: Float, //7   主力流入（万元）
                           mainOutflowAmount: Float, //8   主力流出（万元）
                           mainNetInflowAmount: Float //9   主力净流入（万元）
                         )


object StockMoneyFlow {
  def csvToStockMoneyFlow(line: String): Option[StockMoneyFlow] = {
    val array = line.split(",")
    if (array.length == 10) {
      Some(StockMoneyFlow(
        stringToDate(array(0)),
        array(1).toFloat,
        array(2).replace("%", "").toFloat,
        array(3).replace("%", "").toFloat,
        array(4).toFloat,
        array(5).toFloat,
        array(6).toFloat,
        array(7).toFloat,
        array(8).toFloat,
        array(9).toFloat
      ))
    } else {
      None
    }
  }

  def stockMoneyFlowToRow(smf: StockMoneyFlow): Row = {
    val map = scala.collection.mutable.HashMap[String, String]()
    map.put("close", smf.close.toString)
    map.put("change", smf.change.toString)
    map.put("turnover", smf.turnover.toString)
    map.put("inflowAmount", smf.inflowAmount.toString)
    map.put("outflowAmount", smf.outflowAmount.toString)
    map.put("netInflowAmount", smf.netInflowAmount.toString)
    map.put("mainInflowAmount", smf.mainInflowAmount.toString)
    map.put("mainOutflowAmount", smf.mainOutflowAmount.toString)
    map.put("mainNetInflowAmount", smf.mainNetInflowAmount.toString)

    Row(format(smf.date, "yyyyMMdd"), localDateTimeToLong(smf.date), map.toMap)
  }

  def rowToStockMoneyFlow(row: Row): StockMoneyFlow = {
    val map = row.record

    StockMoneyFlow(
      longToLocalDateTime(row.timestamp),
      map.get("close").get.toFloat,
      map.get("change").get.toFloat,
      map.get("turnover").get.toFloat,
      map.get("inflowAmount").get.toFloat,
      map.get("outflowAmount").get.toFloat,
      map.get("netInflowAmount").get.toFloat,
      map.get("mainInflowAmount").get.toFloat,
      map.get("mainOutflowAmount").get.toFloat,
      map.get("mainNetInflowAmount").get.toFloat
    )
  }
}