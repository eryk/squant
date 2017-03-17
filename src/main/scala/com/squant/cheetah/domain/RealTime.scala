package com.squant.cheetah.domain

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, LocalTime}

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

  def arrayToRealTime2(array: Array[String]): RealTime2 = {
    assert(array.length == 53)
    RealTime2(
      array(0), //0     市场类型,沪市:1,深市:2
      array(1), //1     证券代码
      array(2), //2     证券名称
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
      array(13).toLong,
      array(14).toLong,
      array(15).toLong,
      array(16).toLong,
      array(17).toLong,
      array(18).toLong,
      array(19).toLong,
      array(20).toLong,
      array(21).toLong,
      array(22).toLong,
      array(23).toFloat,
      array(24).toFloat,
      array(25).toFloat,
      array(26).toFloat,
      array(27).toFloat,
      array(28).toFloat,
      array(29).toFloat,
      array(30).toFloat,
      array(31).toLong,
      array(32).toFloat,
      array(33),
      array(34).toFloat,
      array(35).replaceAll("万","").toFloat,
      array(36).toFloat,
      array(37).toFloat,
      array(38).toFloat,
      array(39).toLong,
      array(40).toLong,
      array(41).toFloat,
      array(42).toFloat,
      array(43).toFloat,
      array(44),
      array(45).toFloat,
      array(46).toFloat,
      array(47),
      array(48),
      LocalDateTime.parse(array(49), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
      array(50),
      array(51),
      array(52)
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

case class RealTime2(
                      marketType: String, //0     市场类型,沪市:1,深市:2
                      code: String, //1     证券代码
                      name: String, //2     证券名称
                      buy1: Float, //3     买一
                      buy2: Float, //4     买二
                      buy3: Float, //5     买三
                      buy4: Float, //6     买四
                      buy5: Float, //7     买五
                      sell1: Float, //8     卖一
                      sell2: Float, //9     卖二
                      sell3: Float, //10    卖三
                      sell4: Float, //11    卖四
                      sell5: Float, //12    卖五
                      buy1Volume: Long, //13    买一手数
                      buy2Volume: Long, //14    买二手数
                      buy3Volume: Long, //15    买三手数
                      buy4Volume: Long, //16    买四手数
                      buy5Volume: Long, //17    买五手数
                      sell1Volume: Long, //18    卖一手数
                      sell2Volume: Long, //19    卖二手数
                      sell3Volume: Long, //20    卖三手数
                      sell4Volume: Long, //21    卖四手数
                      sell5Volume: Long, //22    卖五手数
                      limitUp: Float, //23    涨停价
                      limitDown: Float, //24    跌停价
                      close: Float, //25    最新价,收盘价
                      avgCost: Float, //26    均价
                      changeAmount: Float, //27    涨跌额
                      open: Float, //28    开盘价
                      change: Float, //29    涨跌幅
                      high: Float, //30    最高价
                      volume: Long, //31    成交量,单位：手
                      low: Float, //32    最低价
                      unknow1: String, //33    未知
                      lastClose: Float, //34    昨日收盘价
                      amount: Float, //35    成交额,单位:万
                      quantityRelative: Float, //36    量比
                      turnoverRate: Float, //37    换手率
                      PE: Float, //38    市盈率
                      outerDisc: Long, //39    外盘,主动买,单位:手
                      innerDisc: Long, //40    内盘,主动卖,单位:手
                      committeeThan: Float, //41    委比,百分比
                      committeeSent: Float, //42    委差
                      PB: Float, //43    市净率
                      unknow2: String, //44    未知
                      marketValue: Float, //45    流通市值,单位:亿
                      totalValue: Float, //46    总市值,单位:亿
                      unknow3: String, //47    未知
                      unknow4: String, //48    未知
                      date: LocalDateTime, //49    时间
                      unknow5: String, //50    未知
                      unknow6: String, //51    未知
                      unknow7: String //52    未知
                    )