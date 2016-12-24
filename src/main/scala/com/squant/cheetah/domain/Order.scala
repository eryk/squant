package com.squant.cheetah.domain

class Order(code: String, amount: Int, style: OrderStyle){
  var state:OrderState = CREATE
}
