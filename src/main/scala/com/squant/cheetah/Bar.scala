package com.squant.cheetah

import java.time.LocalDateTime
import com.squant.cheetah.domain._

case class Bar(date: LocalDateTime, open: Float, close: Float, high: Float, low: Float, volume: Float, code: String)

object Bar extends App{
  parseCSVToStocks("000692",MIN_15)
}