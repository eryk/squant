package com.squant.cheetah

import java.io.{File, FileInputStream}
import java.time.{LocalDateTime, LocalTime}
import java.time.format.DateTimeFormatter
import java.util.{Calendar, Date}

import com.squant.cheetah.engine.{Clock, Context}
import org.yaml.snakeyaml.Yaml

import scala.collection.JavaConverters._
import scala.collection.mutable

package object utils {

  import com.typesafe.config._

  lazy val config = ConfigFactory.load()

  def yaml(path: String): java.util.Map[String, Any] = {
    val input = new FileInputStream(new File(path))
    val yaml = new Yaml()
    yaml.load(input).asInstanceOf[java.util.Map[String, Any]]
  }

  def loadContext(file: String): Map[String, Context] = {
    import com.squant.cheetah.domain._

    val strategyMap = yaml(file).get("contexts").asInstanceOf[java.util.List[java.util.Map[String, String]]].asScala
    val contexts = mutable.Map[String, Context]()
    strategyMap.foreach(map => {
      val value = map.asScala
      val interval: String = value.get("interval").getOrElse("0")
      val start:LocalDateTime = stringToDate(value.get("start").getOrElse("19901219"))
      val stop:LocalDateTime = stringToDate(value.get("stop").getOrElse("20860621"))
      contexts.put(value.get("name").get, new Context(Clock.mk(interval.toInt,Some(start),Some(stop))))
    }
    )
    contexts.toMap
  }

  /**
    * 计算两个日期之间相差的天数
    *
    * @param smdate 较小的时间
    * @param bdate  较大的时间
    * @return 相差天数
    */
  def daysBetween(smdate: Date, bdate: Date): Int = {
    val cal: Calendar = Calendar.getInstance
    cal.setTime(smdate)
    val time1: Long = cal.getTimeInMillis
    cal.setTime(bdate)
    val time2: Long = cal.getTimeInMillis
    val between_days: Long = (time2 - time1) / (1000 * 3600 * 24)
    String.valueOf(between_days).toInt
  }

  /**
    * 获取最新的交易日期
    *
    * @param date   date
    * @param format format
    * @return string
    */
  def getRecentWorkingDay(date: LocalDateTime, format: String): String = {
    var tmpDate = date;
    while (date.getDayOfWeek.getValue >= 6) tmpDate = date.plusDays(-1)
    tmpDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
  }

  /**
    * 判断当前时间是否是交易时间段
    * 周一到周五
    * 上午：09:30-11:30
    * 下午：13:00-14:45
    *
    * @return if now is trading time,return true, else return false
    */
  def isTradingTime(dateTime: LocalDateTime = LocalDateTime.now()): Boolean = {
    if (dateTime.getDayOfWeek.getValue >= 1 && dateTime.getDayOfWeek.getValue <= 5) {
      val now: LocalTime = dateTime.toLocalTime
      if ((now.isAfter(LocalTime.of(9, 30, 0, 0)) && now.isBefore(LocalTime.of(11, 30, 0, 0)))
        || (now.isAfter(LocalTime.of(13, 0, 0, 0))) && now.isBefore(LocalTime.of(15, 0, 0, 0))) return true
    }
    false
  }

  /**
    * 检查给定时间是否在时间范围内
    *
    * @param date
    * @param start
    * @param stop
    * @return
    */
  def isInRange(date: LocalDateTime, start: LocalDateTime, stop: LocalDateTime): Boolean = {
    date.isAfter(start) && date.isBefore(stop)
  }
}