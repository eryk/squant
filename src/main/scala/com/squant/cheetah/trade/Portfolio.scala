package com.squant.cheetah.trade

import java.time.LocalDateTime

import com.squant.cheetah.domain.{FAILED, LONG, Order, OrderState, OrderStyle, SHORT, SUCCESS}
import com.squant.cheetah.engine.Context

import scala.collection._

//账户当前的资金，标的信息，即所有标的操作仓位的信息汇总
class Portfolio(context: Context) {

  //起始资金
  val startingCash: Double = context.startCash
  //期末资产
  var endingCash: Double = startingCash
  //可用资金, 可用来购买证券的资金
  var availableCash: Double = startingCash

  private class Metric() {
    //交易盈亏
    var pnl: Double = 0
    //收益率=交易盈亏/起始资产
    var pnlRate: Double = 0
    //年化收益率= 交易盈亏 / (平均持股天数/交易总天数)
    var annualReturn: Double = 0
    //平均每只股票持仓天数
    var avgPositionDays: Int = 0
    //持仓总天数
    var totalPositionDays: Int = 0
    //最大单笔盈利
    var maxEarnPerOp: Double = 0
    //最大单笔亏损
    var maxLossPerOp: Double = 0
    //平均每笔盈利
    var meanEarnPerOp: Double = 0
    //连续盈利次数
    var continuousEarnOp: Int = 0
    //连续亏损次数
    var continuousLossOp: Int = 0
    //基准收益额，同期股价涨跌额,单位：元
    var benchmarkBenfit: Double = 0
    //基准收益百分比
    var benchmarkBenfitPercent: Double = 0
    //最大资产
    var max: Double = 0
    //最小资产
    var min: Double = 0
    //最大回撤=最大资产-最小资产
    var drawdown: Double = 0
    //总交易次数
    var totalOperate: Int = 0
    //总盈利次数
    var earnOperate: Int = 0
    //总亏损交易次数
    var lossOperate: Int = 0
    //操作正确率=总盈利次数/总交易次数
    var accuracy: Double = 0
    //夏普率
    var sharpe: Double = 0
    //所提诺比率
    var sortino: Double = 0
  }

  private val metric = new Metric

  private val records = mutable.Map[LocalDateTime, Record]() //记录各个时间点账户状态

  var ts: LocalDateTime = null //最后更新record的时间点

  //key是股票代码code
  var positions: mutable.Map[String, Position] = mutable.Map[String, Position]() //记录账户当前持仓情况

  def buyAll(code: String, orderStyle: OrderStyle): OrderState = {
    val amount = (availableCash / orderStyle.price() / 100).toInt * 100

    if(amount == 0){
      return FAILED
    }

    val order = Order(code, amount, orderStyle, LONG, context.clock.now())
    availableCash -= order.volume
    endingCash -= order.volume

    positions.put(code, Position.mk(order))
    SUCCESS
  }

  def sellAll(code: String, orderStyle: OrderStyle): OrderState = {
    positions.get(code) match {
      case Some(position) => {
        availableCash += position.totalAmount * orderStyle.price()
        endingCash += position.totalAmount * orderStyle.price()
        positions.remove(code)
        Order(code, position.totalAmount, orderStyle, SHORT, context.clock.now())
        SUCCESS
      }
      case None => FAILED
    }
  }
}
