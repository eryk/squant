package com.squant.cheetah.engine

import com.squant.cheetah.domain._

//策略的上下文环境
class Context(n: String = "squant", c: Clock, cash: Int = 100000) {

  val name: String = n

  val clock: Clock = c

  val startCash = cash

  var benchmark: String = "000001"
  //设置滑点
  var slippage: Slippage = PriceRelatedSlippage()
  //扣费类型，默认为股票
  var costType: CostType = STOCK
  //设置佣金/印花税
  var cost: OrderCost = OrderCost(costType = costType)

}
