package com.squant.cheetah.engine

import java.time.LocalDateTime

import com.squant.cheetah.domain._
import com.squant.cheetah.event.Portfolio

//策略的上下文环境
class Context(n: String = "squant", c: Clock, cash: Int = 100000) {
  val name: String = n

  val clock: Clock = c

  val startCash = cash

  var benchmark: String = "000001"
  //设置滑点
  var slippage: Slippage = PriceRelatedSlippage()
  //设置佣金/印花税
  var cost: OrderCost = OrderCost(costType = STOCK)

  val portfolio: Portfolio = new Portfolio(this)

  /**
    * 当前单位时间的开始时间
    * 按天回测时, hour = 9, minute = 30, second = microsecond = 0,
    * 按分钟回测时, second = microsecond = 0
    */
  def currentDate: LocalDateTime = {
    //TODO
    clock.now()
  }

  def previousDate: LocalDateTime = {
    clock.now().plusDays(-1)
  }


  override def toString = s"Context(\nname:$name,\n clock:$clock,\n startCash:$startCash,\n benchmark:$benchmark,\n" +
    s" slippage:$slippage,\n cost:$cost,\n portfolio:$portfolio,\n date:$currentDate)"
}
