package com.squant.cheetah.domain

case class OrderStyle()

case class MarketOrderStyle() extends OrderStyle

case class LimitOrderStyle(limitPrice: Double) extends OrderStyle
