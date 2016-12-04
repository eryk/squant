package com.squant.cheetah.engine
import java.time.LocalDateTime

import com.squant.cheetah.domain.BarType

class RealTimeStockEngine extends Engine{
  override def getPrice(code: String, count: Int, frequency: BarType): Unit = ???

  override def getFundamentals(code: String, startDate: LocalDateTime): Unit = ???
}
