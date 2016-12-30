package com.squant.cheetah.trade

import org.apache.commons.math3.stat.descriptive.SummaryStatistics

class SortinoRatio {
  /**
    * 所提诺比率 = (平均收益 - 国债利率)/损失波动率
    * Computes the Sortino ratio for a list of returns.
    *
    * @param returns    The returns
    * @param rf         The risk free average return
    * @param multiplier Mainly used to compute the Sortino ratio
    *                   for the opposite returns, i.e. short strategy.
    * @return The Sortino ratio. Double.MAX_VALUE is returned if there
    *         are no negative returns after applying the multiplier (i.e.
    *         the divisor is 0).
    */
  def value(returns: List[Double], rf: Double, multiplier: Double): Double = {
    val fullStats = new SummaryStatistics
    val downStats = new SummaryStatistics
    for (i <- returns) {
      val dd: Double = (i - rf) * multiplier
      fullStats.addValue(dd)
      if (dd < rf) downStats.addValue(dd)
      else downStats.addValue(0)
    }

    if (downStats.getN == 0) return Double.MaxValue
    return fullStats.getMean / downStats.getStandardDeviation
  }

  def value(returns: List[Double]): Double = {
    return value(returns, 0, 1)
  }

  def value(returns: List[Double], rf: Double): Double = {
    return value(returns, rf, 1)
  }

  def reverseValue(returns: List[Double]): Double = {
    return value(returns, 0, -1)
  }

  def reverseValue(returns: List[Double], rf: Double): Double = {
    return value(returns, rf, -1)
  }
}
