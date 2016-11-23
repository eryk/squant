package com.squant.cheetah

import java.time.LocalDateTime
import com.squant._

case class Stock(date: LocalDateTime, open: Float, close: Float, high: Float, low: Float, volume: Float, code: String)

object Stock extends App{
  parseCSVToStocks("000692","15")
}