package com.squant.cheetah.engine

import java.time.LocalDateTime

import com.squant.cheetah.domain.{BarType, CostType, DAY, OrderCost, OrderDirection, OrderStyle, PriceRelatedSlippage, Slippage}

trait Engine{

  var clock: Clock

  def setClock(c: Clock) = {
    this.clock = c
  }

  def now(): LocalDateTime = clock.now()

  /** ******************/
  /** ** 交易费用设置 ****/
  /** ******************/

  def setBenchmark(code: String) // 设置基准

  def setOrderCost(cost: OrderCost, cType: CostType) //设置佣金/印花税

  def setSlippage(slippage: Slippage = PriceRelatedSlippage()) //设置滑点

  /** ******************/
  /** ****交易接口 *******/
  /** ******************/
  // 下单
  def order(code: String, amount: Int, style: OrderStyle, side: OrderDirection)

  // 撤单
  def cancelOrder(id: String) = ???

  // 获取未完成订单
  def getOpenOrder() = ???

  // 获取订单信息
  def getOrders() = ???

  // 获取成交订单
  def getTrades() = ???

  /** ******************/
  /** *****数据接口 ******/
  /** ******************/

  //获取历史数据
  def getStockData(code: String, //股票代码
                   count: Int, //数量, 返回的结果集的行数
                   frequency: BarType = DAY //单位时间长度
                  )

  //获取基金净值/期货结算价等
  def getExtras()

  //查询财务数据
  def getFundamentals(code: String, startDate: LocalDateTime)
}