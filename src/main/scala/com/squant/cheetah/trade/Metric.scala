package com.squant.cheetah.trade

import com.squant.cheetah.domain.{LONG, Order, SHORT}

/**
  * Created by eryk on 17-4-5.
  */
class Metric() {
  //交易盈亏
  var pnl: Double = 0
  //收益率=交易盈亏/起始资产
  var pnlRate: Double = 0
  //年化收益率= 交易盈亏 / (平均持股天数/交易总天数)
  var annualReturn: Double = 0
  //平均每只股票持仓天数
  var avgPositionDays: Int = 0
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
  var min: Double = Double.MaxValue
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
