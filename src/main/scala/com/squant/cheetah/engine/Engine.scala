package com.squant.cheetah.engine

import java.time.LocalDateTime

import com.squant.cheetah.domain.{BarType, CostType, DAY, OrderCost, Slippage}

trait Engine {

  def setBenchmark(code: String)  // 设置基准

  def setOrderCost(cost: OrderCost, cType: CostType)  //设置佣金/印花税

  def setSlippage(slippage:Slippage)  //设置滑点


  def currentData(code: String, //股票代码
                  count: Int, //数量, 返回的结果集的行数
                  frequency: BarType = DAY //单位时间长度
                 )

  def getFundamentals(code: String, startDate: LocalDateTime)
}