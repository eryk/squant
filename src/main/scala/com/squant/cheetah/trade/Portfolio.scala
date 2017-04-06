package com.squant.cheetah.trade

import java.time.LocalDateTime

import com.squant.cheetah.domain.{FAILED, LONG, Order, OrderState, OrderStyle, SHORT, SUCCESS}
import com.squant.cheetah.engine.Context
import com.typesafe.scalalogging.LazyLogging

import scala.collection._

//账户当前的资金，标的信息，即所有标的操作仓位的信息汇总
class Portfolio(context: Context) extends LazyLogging {

  //起始资金
  val startingCash: Double = context.startCash
  //期末资产
  var endingCash: Double = startingCash
  //可用资金, 可用来购买证券的资金
  var availableCash: Double = startingCash

  val records = mutable.Buffer[Record]() //记录各个时间点账户状态

  var ts: LocalDateTime = null //最后更新record的时间点

  //key是股票代码code
  var positions: mutable.Map[String, Position] = mutable.LinkedHashMap[String, Position]()

  //记录账户当前持仓情况
  val metric = new Metric

  def update(order: Order): Unit = {
    ts = context.clock.now()
    order.direction match {
      case LONG => {
        //是否已经持有此股票
        if (positions.get(order.code).isEmpty) {
          positions.put(order.code, Position.mk(order))
        } else {
          val position = positions.get(order.code).get
          positions.put(order.code, position.add(Position.mk(order)))
        }
        //扣除交易金额
        availableCash -= order.volume

      }
      case SHORT => {
        var position = positions.get(order.code).get

        position = position.sub(Position.mk(order))
        if (position.totalAmount == 0) {
          positions.remove(order.code)
        } else {
          positions.put(order.code, position)
        }
        availableCash += order.volume

        metric.max = Math.max(metric.max, endingCash)
        metric.min = Math.min(metric.min, endingCash)
      }
      case _ => new UnknownError("unkown order direction")
    }
    metric.totalOperate += 1

    //计算税费
    availableCash -= context.cost.cost(order)

    endingCash = positions.foldLeft[Double](availableCash) {
      (a, b) => a + b._2.totalAmount * b._2.avgCost
    }

    endingCash -= context.cost.cost(order)

    metric.pnl = endingCash - startingCash
    metric.pnlRate = (endingCash - startingCash) / startingCash * 100

    records.append(new Record(order.code, order.direction, order.amount, order.price, order.volume, context.cost.cost(order), ts))
  }

  def buy(code: String, amount: Int, orderStyle: OrderStyle): OrderState = {
    val order = context.slippage.compute(Order(code, amount, orderStyle, LONG, context.clock.now()))
    update(order)
    logger.debug(s"${context.clock.now()} buy ${code} $amount at price ${order.price}")
    SUCCESS
  }

  def buyAll(code: String, orderStyle: OrderStyle): OrderState = {

    val amount = (availableCash / context.slippage.compute(Order(code, 0, orderStyle, LONG, context.clock.now())).price / 100).toInt * 100
    if (amount == 0) {
      return FAILED
    }
    val order = context.slippage.compute(Order(code, amount, orderStyle, LONG, context.clock.now()))
    update(order)
    logger.debug(s"${context.clock.now()} buy ${code} $amount at price ${order.price}")
    SUCCESS
  }

  def sell(code: String, amount: Int, orderStyle: OrderStyle): OrderState = {
    val order = context.slippage.compute(Order(code, amount, orderStyle, SHORT, context.clock.now()))
    update(order)
    logger.debug(s"${context.clock.now()} sell ${code} $amount at price ${order.price}")
    SUCCESS
  }

  def sellAll(code: String, orderStyle: OrderStyle): OrderState = {
    positions.get(code) match {
      case Some(position) => {
        val order = context.slippage.compute(Order(code, position.totalAmount, orderStyle, SHORT, context.clock.now()))
        update(order) //最后更新metric，metric里会记录position
        logger.debug(s"${context.clock.now()} sell ${code} ${order.amount} at price ${order.price}")
        SUCCESS
      }
      case None => FAILED
    }
  }

  override def toString: String = {
    val buffer = mutable.StringBuilder.newBuilder
    for (item <- records) {
      buffer.append(s"\t\t${item}\n")
    }

    val posStr = mutable.StringBuilder.newBuilder
    positions.foreach { item =>
      posStr.append(s"\t\t${item._1} ${item._2}\n")
    }

    s"账户详情信息:\n" +
      s"\t起始资金：$startingCash\n" +
      f"\t期末资金：$endingCash%2.2f\n" +
      f"\t可用资金：$availableCash%2.2f\n" +
      f"\t交易盈亏：${metric.pnl}%2.2f(${metric.pnlRate}%2.2f" + "%)\n" +
      s"\t交易次数：${metric.totalOperate}\n" +
      f"\t最大资产：${metric.max}%2.2f\n" +
      f"\t最小资产：${metric.min}%2.2f\n" +
      f"\t持仓情况：\n${posStr.toString}" +
      s"\t交易记录：\n${buffer.toString}"
  }
}
