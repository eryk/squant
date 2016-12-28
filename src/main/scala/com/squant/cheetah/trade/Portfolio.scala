package com.squant.cheetah.trade

import java.time.LocalDateTime

import com.squant.cheetah.domain.{CREATE_FAIL, FAILED, Order, OrderState, OrderStyle, SUCCESS, UNKNOW}

import scala.collection._

//账户当前的资金，标的信息，即所有标的操作仓位的信息汇总
class Portfolio(sCash: Double) {

  var startingCash:Double = sCash //起始资金
  var endingCash: Double = sCash //期末资金
  var inoutCash: Double = sCash //累计出入金，比如初始资金 1000, 后来转移出去 100, 则这个值是 1000 - 100
  var availableCash: Double = sCash //可用资金, 可用来购买证券的资金
  var transferableCash: Double = sCash //可取资金, 即可以提现的资金, 不包括今日卖出证券所得资金
  var lockedCache: Double = 0 //挂单锁住资金

  var pnl: Double = 0 //交易盈亏
  var pnlRate: Double = 0 //收益率=交易盈亏/起始资产
  var annualReturn: Double = 0 //年化收益率= 交易盈亏 / (平均持股天数/交易总天数)
  var avgPositionDays: Int = 0 //平均每只股票持仓天数
  var totalPositionDays: Int = 0 //持仓总天数
  var maxEarnPerOp: Double = 0 //最大单笔盈利
  var maxLossPerOp: Double = 0 //最大单笔亏损
  var meanEarnPerOp: Double = 0 //平均每笔盈利
  var continuousEarnOp: Int = 0 //连续盈利次数
  var continuousLossOp: Int = 0 //连续亏损次数
  var benchmarkBenfit: Double = 0 //基准收益额，同期股价涨跌额,单位：元
  var benchmarkBenfitPercent: Double = 0 //基准收益百分比
  var max: Double = 0 //最大资产
  var min: Double = 0 //最小资产
  var drawdown: Double = 0 //最大回撤=最大资产-最小资产
  var totalOperate: Int = 0 //总交易次数
  var earnOperate: Int = 0 //总盈利次数
  var lossOperate: Int = 0 //总亏损交易次数
  var accuracy: Double = 0 //操作正确率=总盈利次数/总交易次数
  var sharpe: Double = 0 //夏普率
  var sortino: Double = 0 //所提诺比率

  private val records = mutable.Map[LocalDateTime, Record]() //记录各个时间点账户状态

  var ts: LocalDateTime = null //最后更新record的时间点

  //key是股票代码code
  var positions: mutable.Map[String, Position] = mutable.Map[String, Position]() //记录账户当前持仓情况

  def longOrder(order: Order): OrderState = {
    availableCash > order.volume match {
      case true => {
        positions.put(order.code, Position.mk(order))
        availableCash = availableCash - order.volume
      }
      case false => CREATE_FAIL
      //TODO update porfolio
    }
    UNKNOW
  }

  def shortOrder(order: Order): OrderState = {
    positions.contains(order.code) match {
      case true => {
        val position = positions.get(order.code).get
        if (position.totalAmount < order.amount) {
          return FAILED
        } else if (position.totalAmount == order.amount) {
          positions - order.code
          //TODO update porfolio
        } else {
          Position.sub(positions.get(order.code).get, positions.get(order.code).get)
          //TODO update porfolio
        }
        SUCCESS
      }
      case false => FAILED
    }
  }

  def shortAllOrder(order: Order): OrderState = {
    positions.get(order.code) match {
      case Some(position) => {
        availableCash += position.totalAmount * position.avgCost
        SUCCESS
      }
      case _ => FAILED
    }
  }
}
