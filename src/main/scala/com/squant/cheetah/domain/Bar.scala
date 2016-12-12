package com.squant.cheetah.domain

import java.time.LocalDateTime

import com.squant.cheetah._


sealed trait BarType

case object SEC_1 extends BarType

case object MIN_1 extends BarType

case object MIN_5 extends BarType

case object MIN_15 extends BarType

case object MIN_30 extends BarType

case object MIN_60 extends BarType

case object DAY extends BarType

case object WEEK extends BarType

case object MONTH extends BarType

case class Bar(barType: BarType, date: LocalDateTime, open: Float, close: Float, high: Float, low: Float, volume: Float, code: String)

object Bar extends App {
  val stocks = parseCSVToStocks("002173", MIN_30)
  stocks.foreach(println)
}