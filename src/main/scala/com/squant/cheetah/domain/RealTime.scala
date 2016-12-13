package com.squant.cheetah.domain

case class RealTime(
                     name: String,
                     open: Float,
                     lastClose: Float,
                     close: Float,
                     high: Float,
                     low: Float,
                     buy1: Float,
                     sell1: Float,
                     chengjiao: Long,
                     jine: Long
                   )