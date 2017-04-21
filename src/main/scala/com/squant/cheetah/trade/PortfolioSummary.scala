package com.squant.cheetah.trade

import java.time.LocalDate

/**
  * 每日投资组合盈亏情况记录
  * author: eryk
  * mail: xuqi86@gmail.com
  * date: 17-4-7.
  */
case class PortfolioSummary(
                            LocalDate:LocalDate,//日期
                            pnl:Double,         //策略收益
                            benchMarkPnl:Double,//基准收益
                            todayEarn:Double,   //当日盈利
                            todayLoss:Double,   //当日亏损
                            todayBuy:Double,    //当日开仓
                            todaySell:Double    //当日平仓
                          )
