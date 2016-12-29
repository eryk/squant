package com.squant.cheetah.domain

sealed trait Slippage {
  def compute(order: Order): Double
}

//固定值：
//这个价差可以是一个固定的值(比如0.02元, 交易时加减0.01元), 设定方式为：FixedSlippage(0.02)
case class FixedSlippage(count: Double = 0.02) extends Slippage {
  override def compute(order: Order): Double = {
    order.direction match {
      case LONG => order.price + count
      case SHORT => order.price - count
    }
  }
}

//百分比：
//这个价差可以是是当时价格的一个百分比(比如0.2%, 交易时加减当时价格的0.2%), 设定方式为：PriceRelatedSlippage(0.002)
case class PriceRelatedSlippage(precent: Double = 0.002) extends Slippage {
  override def compute(order: Order): Double = {
    order.direction match {
      case LONG => order.price * (1 + precent)
      case SHORT => order.price * (1 - precent)
    }
  }
}