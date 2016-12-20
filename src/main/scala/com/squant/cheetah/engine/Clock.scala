package com.squant.cheetah.engine

import java.time.LocalDateTime

case class ClockType()

case object BACKTEST extends ClockType

case object REALTIME extends ClockType

class Clock(clockType:ClockType,interval:Int) {
  def now():LocalDateTime = {
    clockType match{
      case BACKTEST => LocalDateTime.now()
      case REALTIME => LocalDateTime.now()
    }
  }
}
