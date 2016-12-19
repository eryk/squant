package com.squant.cheetah.trade

import java.time.LocalDateTime

class Portfolio {
  val DEFAULT_NAME: String = "JQuant"
  private var name: String = null
  private var status: Record = null
  //需要在初始化时赋值
  //  private val riskAnalysis: RiskAnalysis = new RiskAnalysis
  //记录各个时间点账户状态
  private val records = Map[LocalDateTime, Record]()
  //最后更新record的时间点
  private var ts: LocalDateTime = null
  //记录账户当前持仓情况
  private var positions: Map[String, Position] = Map[String, Position]()

}
