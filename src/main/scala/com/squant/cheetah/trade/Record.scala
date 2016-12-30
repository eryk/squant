package com.squant.cheetah.trade

import java.time.LocalDateTime

import com.squant.cheetah.domain.Order

/**
  * 记录股票交易状态，持仓情况，以及风险分析指标
  */
case class Record(code: String,
                  amount: Int,
                  price: Double, //价格
                  volume: Double, //当日买入卖出额
                  cost: Double, //交易费用
                  ts: LocalDateTime
                 ) {
  override def toString: String = {
    f"$ts $code $amount,$price%2.2f,$volume%2.2f,$cost%2.2f"
  }
}
