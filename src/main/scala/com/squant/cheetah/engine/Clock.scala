package com.squant.cheetah.engine

import java.time.LocalDateTime

sealed trait ClockType {
  def now(): LocalDateTime

  def isFinished(): Boolean
}

case class BACKTEST(start: LocalDateTime, stop: LocalDateTime, interval: Int) extends ClockType {
  var currentTime = start

  assert(start.isBefore(stop))

  override def now(): LocalDateTime = {
    currentTime = currentTime.plusMinutes(interval)
    currentTime
  }

  override def isFinished(): Boolean = currentTime.isBefore(stop)
}

case class TRADE(interval: Int) extends ClockType {

  override def now(): LocalDateTime = LocalDateTime.now()

  override def isFinished(): Boolean = false;
}

class Clock(clockType: ClockType) {

  def now(): LocalDateTime = clockType.now()

  def getRange(count: Int): (LocalDateTime, LocalDateTime) = {
    val date = clockType.now
    (date.plusMinutes(-count), date)
  }

  def isFinished(): Boolean = clockType.isFinished()
}
