package com.squant.cheetah

import java.time.LocalDateTime
import com.squant._

case class Stock(ktype: String, date: LocalDateTime, open: Float, close: Float, high: Float, low: Float, volume: Float, code: String)

object Stock extends App {
  val stocks = parseCSVToStocks("002173", "30")
  stocks.foreach(println)
}