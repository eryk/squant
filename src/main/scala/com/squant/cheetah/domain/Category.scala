package com.squant.cheetah.domain

import java.time.LocalDateTime

import com.squant.cheetah.engine.Row
import com.squant.cheetah.utils._

case class Category(name: String, code: String, categoryType: String, pinyin: String, url: String, stocks: List[String])

object Category {
  def categoryToRow(category: Category): Row = {
    val map = scala.collection.mutable.HashMap[String, String]()
    map.put("name", category.name)
    map.put("code", category.code)
    map.put("categoryType", category.categoryType)
    map.put("pinyin", category.pinyin)
    map.put("url", category.url)

    val stocks = category.stocks.foldLeft("") { (a, b) =>
      a + "_" + b
    }

    map.put("stocks", stocks)

    Row(category.name, localDateTimeToLong(LocalDateTime.now()), map.toMap)
  }

  def rowToCategory(row:Row):Category = ???
}