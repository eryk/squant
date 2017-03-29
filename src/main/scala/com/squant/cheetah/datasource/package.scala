package com.squant.cheetah

import java.net.URL
import scala.collection.JavaConverters._

import com.squant.cheetah.domain.{BarType, DAY, MIN_15, MIN_30, MIN_5, MIN_60, MONTH, WEEK}
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell

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
      case e:Exception =>
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

  def xlsToCSV(url: String): List[String] = {
    val path = new URL(url)
    val conn = path.openConnection()
    conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)")
    val inputStream = conn.getInputStream()
    val wb = new HSSFWorkbook(inputStream)
    val sheet = wb.getSheetAt(0)
    val lines = sheet.iterator().asScala.map(item => item.cellIterator().asScala.foldLeft("")((x: String, cell: Cell) => x + "," + cell.toString).drop(1))
    lines.toList
  }
}
