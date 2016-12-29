package com.squant.cheetah.domain

import java.time.LocalDateTime

case class Order(code: String, amount: Int, style: OrderStyle,direction: OrderDirection, date: LocalDateTime) {
  var state: OrderState = OPEN
  var price = style.price
  var volume = amount * style.price
}
