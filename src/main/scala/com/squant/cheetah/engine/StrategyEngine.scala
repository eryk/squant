package com.squant.cheetah.engine

import java.time.LocalDateTime

import com.squant.cheetah.domain.{CostType, OrderCost, PriceRelatedSlippage, STOCK, Slippage}

trait StrategyEngine{

  protected var clock: Clock
  protected var benchmark: String
  protected var cost: OrderCost
  protected var costType: CostType
  protected var slippage: Slippage

  def init(): Unit ={
    setBenchmark()
    setOrderCost()
    setSlippage()
  }

  def setClock(c: Clock) = {
    this.clock = c
  }

  def now(): LocalDateTime = clock.now()

  // 设置基准
  def setBenchmark(code: String = "000001") = {
    benchmark = code
  }

  //设置佣金/印花税
  def setOrderCost(cost: OrderCost = OrderCost(), cType: CostType = STOCK) = {
    this.cost = cost
    this.costType = cType
  }

  //设置滑点
  def setSlippage(slippage: Slippage = PriceRelatedSlippage()) = {
    this.slippage = slippage
  }

}