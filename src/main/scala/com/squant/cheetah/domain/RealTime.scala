package com.squant.cheetah.domain

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalTime}

object RealTime {
  def arrayToRealTime(array: Array[String]): RealTime = {
    assert(array.length == 33)
    RealTime(
      array(0),
      array(1).toFloat,
      array(2).toFloat,
      array(3).toFloat,
      array(4).toFloat,
      array(5).toFloat,
      array(6).toFloat,
      array(7).toFloat,
      array(8).toLong / 100,
      array(9).toFloat,
      array(10).toLong,
      array(11).toFloat,
      array(12).toLong,
      array(13).toFloat,
      array(14).toLong,
      array(15).toFloat,
      array(16).toLong,
      array(17).toFloat,
      array(18).toLong,
      array(19).toFloat,
      array(20).toLong,
      array(21).toFloat,
      array(22).toLong,
      array(23).toFloat,
      array(24).toLong,
      array(25).toFloat,
      array(26).toLong,
      array(27).toFloat,
      array(28).toLong,
      array(29).toFloat,
      LocalDate.parse(array(30), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
      LocalTime.parse(array(31), DateTimeFormatter.ofPattern("HH:mm:ss")),
      array(32)
    )
  }
}

case class RealTime(
                     name: String, //股票名字
                     open: Float, //今日开盘价
                     lastClose: Float, //昨日收盘价
                     close: Float, //当前价格
                     high: Float, //今日最高价
                     low: Float, //今日最低价
                     buy: Float, //竞买价
                     sell: Float, //竞卖价
                     volume: Long, //成交量   单位：手
                     amount: Float, //成交额：元
                     buy1Volume: Long, //买一 申请
                     buy1: Float, //买一竞买价
                     buy2Volume: Long, //买二 申请
                     buy2: Float, //买二竞买价
                     buy3Volume: Long, //买三 申请
                     buy3: Float, //买三竞买价
                     buy4Volume: Long, //买四 申请
                     buy4: Float, //买四竞买价
                     buy5Volume: Long, //买五 申请
                     buy5: Float, //买五竞买价
                     sell1Volume: Long, //卖一 申请
                     sell1: Float, //卖一竞卖价
                     sell2Volume: Long, //卖二 申请
                     sell2: Float, //卖二竞卖价
                     sell3Volume: Long, //卖三 申请
                     sell3: Float, //卖三竞卖价
                     sell4Volume: Long, //卖四 申请
                     sell4: Float, //卖四竞卖价
                     sell5Volume: Long, //卖五 申请
                     sell5: Float, //卖五竞卖价
                     date: LocalDate, //日期
                     time: LocalTime, //时间
                     unknow: String
                   )