package com.squant.cheetah.domain

import scala.collection.immutable.TreeMap
import scala.collection.JavaConverters._

/**
  * author: eryk
  * mail: xuqi86@gmail.com
  * date: 17-3-29.
  */
case class Finance(mainreport: Map[String, Map[String, Double]],
                   debtreport: Map[String, Map[String, Double]],
                   benefitreport: Map[String, Map[String, Double]],
                   cashreport: Map[String, Map[String, Double]])

object Finance {
  def csvToMap(lines: List[String]): TreeMap[String, Map[String, Double]] = {

    def stringToDouble(value: String): Double = {
      if (value == null || value.length == 0) {
        0
      } else {
        value.toDouble
      }
    }
    val report = new java.util.TreeMap[String, Map[String, Double]]().asScala

    if (lines.length > 1) {
      val columns = lines.map(line => line.split(","))
      for (i <- 1 to columns(0).length - 1) {
        val kvMap = new scala.collection.mutable.HashMap[String, Double]()
        for (j <- 1 to columns.size - 1) {
          if (i < columns(j).length) {
            kvMap.put(columns(j)(0), stringToDouble(columns(j)(i)))
          }
        }
        report.put(columns(0)(i), kvMap.toMap)
      }
    }
    TreeMap[String, Map[String, Double]](report.toArray: _*)
  }
}