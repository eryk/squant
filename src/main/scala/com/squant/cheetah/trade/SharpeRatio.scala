package com.squant.cheetah.trade

import org.apache.commons.math3.stat.descriptive.SummaryStatistics

object SharpeRatio {
  /**
    * 夏普比率 = 实际回报率 / 回报率的标准差
    * Computes the Sharpe ratio for a list of returns.
    *
    * @param returns The returns
    * @param rf      The risk free average return
    * @return The Sharpe ratio
    */
  def value(returns: List[Double], rf: Double): Double = {
    val ss = new SummaryStatistics()

    returns.foreach(cur => ss.addValue(cur - rf))
    return ss.getMean / ss.getStandardDeviation
  }

  def value(returns: List[Double]):Double = {
    return value(returns, 0);
  }
}
