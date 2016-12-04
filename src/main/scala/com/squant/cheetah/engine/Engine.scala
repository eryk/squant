package com.squant.cheetah.engine

import java.time.LocalDateTime

import com.squant.cheetah.domain.{BarType, DAY}

trait Engine {
  def getPrice(code: String, count: Int, frequency: BarType = DAY)

  def getFundamentals(code: String, startDate: LocalDateTime)
}