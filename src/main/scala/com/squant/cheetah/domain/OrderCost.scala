package com.squant.cheetah.domain

case class OrderCost(openTax: Double = 0, //买入时印花税 (只股票类标的收取，基金与期货不收)
                     closeTax: Double = 0.001, //卖出时印花税 (只股票类标的收取，基金与期货不收)
                     openCommission: Double = 0.00025, //买入时佣金
                     closeCommission: Double = 0.00025, //卖出时佣金
                     closeTodayCommission: Double = 0, //平今仓佣金
                     minCommission: Double = 5, //最低佣金，不包含印花税
                     costType: CostType = STOCK //默认的扣费类型为股票
                    ) {
  def cost(order: Order): Double = costType match {
    case STOCK if order.direction == LONG => {
      val cost = order.amount * order.price * (openTax + openCommission)
      if (cost < minCommission) minCommission else cost
    }
    case STOCK if order.direction == SHORT => {
      order.amount * order.price * (closeTax + closeCommission)
    }
    case _ => order.price
  }
}

sealed class CostType

//股票
case object STOCK extends CostType

//基金
case object FUND extends CostType

//金融期货
case object INDEX_FUTURES extends CostType
