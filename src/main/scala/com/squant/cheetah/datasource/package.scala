package com.squant.cheetah

import java.time.format.DateTimeFormatter

import com.squant.cheetah.domain.{BarType, DAY, MIN_15, MIN_30, MIN_5, MIN_60, MONTH, WEEK}

import scala.io.Source

package object datasource {

  implicit def stringToBarType(kType: String): BarType = {
    kType match {
      case kType if kType == "5" => MIN_5
      case kType if kType == "15" => MIN_15
      case kType if kType == "30" => MIN_30
      case kType if kType == "60" => MIN_60
      case kType if kType == "day" => DAY
      case kType if kType == "week" => WEEK
      case kType if kType == "month" => MONTH
    }
  }

  def retry[T](n: Int)(fn: => T): T = {
    try {
      fn
    } catch {
      case e =>
        if (n > 1)
          retry(n - 1)(fn)
        else
          throw e
    }
  }

  def downloadWithRetry(url: String, encode: String, retry: Int = 3): String = {
    try {
      Source.fromURL(url, encode).mkString
    } catch {
      case ex: Exception => {
        if (retry > 1)
          downloadWithRetry(url, encode, retry - 1)
        else
          ""
      }
    }
  }
}
