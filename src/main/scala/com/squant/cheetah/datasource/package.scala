package com.squant.cheetah

import java.time.format.DateTimeFormatter

import com.squant.cheetah.domain.{BarType, DAY, MIN_15, MIN_30, MIN_5, MIN_60, MONTH, WEEK}

package object datasource {
  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  implicit def stringToBarType(kType:String):BarType = {
    kType match{
      case kType if kType == "5" => MIN_5
      case kType if kType == "15" => MIN_15
      case kType if kType == "30" => MIN_30
      case kType if kType == "60" => MIN_60
      case kType if kType == "day" => DAY
      case kType if kType == "week" => WEEK
      case kType if kType == "month" => MONTH
    }
  }
}
