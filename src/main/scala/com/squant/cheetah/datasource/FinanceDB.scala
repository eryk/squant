package com.squant.cheetah.datasource

import java.time.LocalDateTime

import com.squant.cheetah.domain._
import com.squant.cheetah.utils._

//https://www.joinquant.com/data
//https://www.joinquant.com/data/dict/fundamentals
trait FinanceDB {

  def realtime(code: String): RealTime

  def tick(code: String, date: LocalDateTime): List[Tick]

  //包含当天数据，内部做数据的聚合
  def ktype(code: String, kType: BarType, start: LocalDateTime, stop: LocalDateTime)

  //地区、概念、行业
  def stockCategory(): Map[String, Map[String, Seq[String]]]

  def stocks(): Seq[Symbol] = parseCSVToSymbols(config.getString("squant.db.path") + "/stocks.csv")

  def getFundamentals(code: String)

  def updateAll(start: LocalDateTime, stop: LocalDateTime): Boolean

  def update(code: String, start: LocalDateTime, stop: LocalDateTime)
}
