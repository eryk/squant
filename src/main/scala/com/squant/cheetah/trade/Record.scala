package com.squant.cheetah.trade

import java.time.LocalDateTime

/**
  * 记录股票交易状态，持仓情况，以及风险分析指标
  */
case class Record(positions:Map[String,Position],
                  todayBuy:Double, //当日买入额
                  todaySell:Double, //当日卖出额
                  todayEarn:Double, //当日盈利，不包括亏损数额
                  todayLoss:Double, //当日亏损
                  ts:LocalDateTime
                 )
