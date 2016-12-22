package com.squant.cheetah.engine

import java.time.LocalDateTime

sealed trait ClockType {
  def now(): LocalDateTime

  def isFinished(): Boolean

  def interval():Int
}

case class BACKTEST(start: LocalDateTime, stop: LocalDateTime, i: Int) extends ClockType {
  var currentTime = start

  assert(start.isBefore(stop))

  override def now(): LocalDateTime = {
    currentTime = currentTime.plusMinutes(interval)
    currentTime
  }

  def interval() = i

  override def isFinished(): Boolean = currentTime.isBefore(stop)
}

case class TRADE(i: Int) extends ClockType {

  override def now(): LocalDateTime = LocalDateTime.now()

  def interval() = i

  override def isFinished(): Boolean = false;
}

class Clock(clockType: ClockType) {

  def now(): LocalDateTime = clockType.now()

  def getRange(count: Int): (LocalDateTime, LocalDateTime) = {
    val date = clockType.now
    (date.plusMinutes(-count), date)
  }

  def interval() = clockType.interval()

  def isFinished(): Boolean = clockType.isFinished()
}

object Clock {
  /**
    *
    * @param interval 单位:分钟
    * @param start
    * @param stop
    * @return
    */
  def mk(interval: Int = 1, start: Option[LocalDateTime] = None, stop: Option[LocalDateTime] = Option[LocalDateTime](LocalDateTime.now())): Clock = {
    start match {
      case None => new Clock(TRADE(interval))
      case Some(t) => new Clock(BACKTEST(start.get, stop.get, interval))
    }
  }
}