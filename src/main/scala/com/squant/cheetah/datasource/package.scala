package com.squant.cheetah

import java.time.{LocalDate, LocalDateTime, LocalTime}
import java.time.format.DateTimeFormatter

import io.circe.{Decoder, Encoder}
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
}
