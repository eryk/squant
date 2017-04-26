package com.squant.cheetah.event

import com.squant.cheetah.domain.{LONG, Order, Position, SHORT}
import com.squant.cheetah.engine.Context
import com.typesafe.scalalogging.LazyLogging

import scala.collection._

//账户当前的资金，标的信息，即所有标的操作仓位的信息汇总
class Portfolio(context: Context) extends LazyLogging {

  //可用资金, 可用来购买证券的资金
  var availableCash: Double = context.startCash
  //记录各个时间点账户状态
  val records = mutable.Buffer[OrderRecord]()
  //key是股票代码code
  val positions: mutable.Map[String, Position] = mutable.LinkedHashMap[String, Position]()

  def update(order: Order): Unit = {
    val tempPosition = Position.mkFrom(order)
    order.direction match {
      case LONG => {
        //是否已经持有此股票
        if (positions.get(order.code).isEmpty) {
          positions.put(order.code, tempPosition)
        } else {
          val currentPosition = positions(order.code)
          positions.put(order.code, currentPosition + tempPosition)
        }
        //扣除交易金额
        availableCash -= order.volume
      }
      case SHORT => {
        var currentPosition = positions(order.code)

        currentPosition = currentPosition - Position.mkFrom(order)
        if (currentPosition.totalAmount == 0) {
          positions.remove(order.code)
        } else {
          positions.put(order.code, currentPosition)
        }
        availableCash += order.volume
      }
      case _ => new UnknownError("unkown order direction")
    }
    //计算税费
    availableCash -= context.cost.cost(order)

    records.append(new OrderRecord(order.code, order.direction, order.amount, order.price, order.volume,
      context.cost.cost(order), context.currentDate()))
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
      f"\t可用资金：$availableCash%2.2f\n" +
      f"\t持仓情况：\n${posStr.toString}" +
      s"\t交易记录：\n${buffer.toString}"
  }
}
