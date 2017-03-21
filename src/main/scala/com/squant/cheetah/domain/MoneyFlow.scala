package com.squant.cheetah.domain

import java.time.LocalDateTime

import com.squant.cheetah.engine.Row
import com.squant.cheetah.utils._

case class MoneyFlow(
                      code: String, //1     板块代码
                      name: String, //2     板块名称
                      change: Float, //3     涨跌幅
                      netInflowAmount: Float, //4     主力净流入-净额(万)
                      netInflowRatio: Float, //5     主力净流入-净占比
                      largeOrderInflowAmount: Float, //6     超大单净流入-净额
                      largeOrderInflowRatio: Float, //7     超大单净流入-净占比
                      bigOrderInflowAmount: Float, //8     大单净流入-净额
                      bigOrderInflowRatio: Float, //9     大单净流入-净占比
                      mediumOrderInflowAmount: Float, //10     中单净流入-净额
                      mediumOrderInflowRatio: Float, //11    中单净流入-净占比
                      smallOrderInflowAmount: Float, //12    小单净流入-净额
                      smallOrderInflowRatio: Float, //13     小单净流入-净占比
                      topName: String, //14     股票名称
                      topCode: String //15     股票代码
                    )

object MoneyFlow {
  def csvToMoneyFlow(line: String): Option[MoneyFlow] = {
    val array: Array[String] = line.split(",")
    if (array == 16) {
      Some(new MoneyFlow(
        array(1),
        array(2),
        array(3).toFloat,
        array(4).toFloat,
        array(5).toFloat,
        array(6).toFloat,
        array(7).toFloat,
        array(8).toFloat,
        array(9).toFloat,
        array(10).toFloat,
        array(11).toFloat,
        array(12).toFloat,
        array(13).toFloat,
        array(14),
        array(15)
      ))
    } else {
      None
    }
  }

  def moneyflowToRow(moneyFlow: MoneyFlow): Row = {
    val map = scala.collection.mutable.HashMap[String, String]()
    map.put("code", moneyFlow.code)
    map.put("name", moneyFlow.name)
    map.put("change", moneyFlow.change.toString)
    map.put("netInflowAmount", moneyFlow.netInflowAmount.toString)
    map.put("netInflowRatio", moneyFlow.netInflowRatio.toString)
    map.put("largeOrderInflowAmount", moneyFlow.largeOrderInflowAmount.toString)
    map.put("largeOrderInflowRatio", moneyFlow.largeOrderInflowRatio.toString)
    map.put("bigOrderInflowAmount", moneyFlow.bigOrderInflowAmount.toString)
    map.put("bigOrderInflowRatio", moneyFlow.bigOrderInflowRatio.toString)
    map.put("mediumOrderInflowAmount", moneyFlow.mediumOrderInflowAmount.toString)
    map.put("mediumOrderInflowRatio", moneyFlow.mediumOrderInflowRatio.toString)
    map.put("smallOrderInflowAmount", moneyFlow.smallOrderInflowAmount.toString)
    map.put("smallOrderInflowRatio", moneyFlow.smallOrderInflowRatio.toString)
    map.put("topName", moneyFlow.topName.toString)
    map.put("topCode", moneyFlow.topCode.toString)

    Row(moneyFlow.code, localDateTimeToLong(LocalDateTime.now()), map.toMap)
  }

  def rowToMoneyFlow(row: Row): MoneyFlow = ???
}