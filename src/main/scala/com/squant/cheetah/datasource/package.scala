package com.squant.cheetah

import java.time.{LocalDate, LocalDateTime, LocalTime}
import java.time.format.DateTimeFormatter

import com.squant.cheetah.domain.{BarType, DAY, MIN_15, MIN_30, MIN_5, MIN_60, MONTH, TickType, WEEK}
import io.circe.{Decoder, Encoder, Json}
import io.circe.java8.time._


package object datasource {
  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  implicit final val localTimeDecoder: Decoder[LocalTime] =
    decodeLocalTime(DateTimeFormatter.ofPattern("HH:mm:ss"))

  implicit final val localTimeEncoder: Encoder[LocalTime] =
    encodeLocalTime(DateTimeFormatter.ofPattern("HH:mm:ss"))

  implicit final val localDateDecoder: Decoder[LocalDate] =
    decodeLocalDate(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

  implicit final val localDateEncoder: Encoder[LocalDate] =
    encodeLocalDate(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

  implicit final val localDateTimeDecoder: Decoder[LocalDateTime] =
    decodeLocalDateTime(formatter)

  implicit final val localDateTimeEncoder: Encoder[LocalDateTime] =
    encodeLocalDateTime(formatter)

  implicit final val encodeURL: Encoder[TickType] = Encoder.instance { v =>
    Json.fromString(v.toString)
  }

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
