package com.squant.cheetah.event

import java.time.LocalDateTime

import com.squant.cheetah.domain.Order

/**
  * Created by eryk on 17-4-23.
  */
class Event()

case class TimeEvent(localDateTime: LocalDateTime) extends Event

case class OrderEvent(order: Order) extends Event

case class FillEvent(order: Order, cost: Double) extends Event