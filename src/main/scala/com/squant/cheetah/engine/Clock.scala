package com.squant.cheetah.engine

import java.time.LocalDateTime

sealed trait ClockType {
  def now(): LocalDateTime

  def update()

  def isFinished(): Boolean

  def interval(): Int
}

case class BACKTEST(start: LocalDateTime, stop: LocalDateTime, unit: Int) extends ClockType {
  var currentTime = start

  assert(start.isBefore(stop))

  override def update() = {
    currentTime = currentTime.plusMinutes(unit)
  }

  override def now(): LocalDateTime = {
    currentTime
  }

  def interval() = unit

  override def isFinished(): Boolean = currentTime.isAfter(stop)
}

case class TRADE(i: Int) extends ClockType {

  override def now(): LocalDateTime = LocalDateTime.now()

  def interval() = i

  override def isFinished(): Boolean = false

  override def update(): Unit = {}
}

class Clock(cType: ClockType) {

  def now(): LocalDateTime = cType.now()

  def getRange(count: Int): (LocalDateTime, LocalDateTime) = {
    val date = cType.now
    (date.plusMinutes(-count), date)
  }

  def clockType(): ClockType = cType

  def interval() = cType.interval()

  def update() = {
    cType.update()
  }

  def isFinished(): Boolean = cType.isFinished()
}

object Clock extends App {
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