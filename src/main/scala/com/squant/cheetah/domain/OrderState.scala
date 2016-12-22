package com.squant.cheetah.domain

sealed class OrderState()

case object CREATE extends OrderState //订单创建

case object CREATE_FAIL extends OrderState //下单失败，可能是资金不足、暂时停牌等原因

case object HOLD extends OrderState //订单提交

case object SUCCESS extends OrderState  //成交

case object PART_SUCCESS extends OrderState //部分成交

case object FAILED extends OrderState //交易失败

case object CANCELED extends OrderState //订单被取消
