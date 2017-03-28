package com.squant.cheetah.datasource

import java.time.LocalDateTime

import com.typesafe.scalalogging.StrictLogging
import com.squant.cheetah.utils._
import it.sauronsoftware.cron4j.{Scheduler, Task, TaskExecutionContext}

import scala.collection.JavaConverters._


case class TaskConfig(name: String,
                      cron: String,
                      clear: Boolean = false,
                      toCSV: Boolean = false,
                      toDB: Boolean = false,
                      start: LocalDateTime,
                      stop: LocalDateTime
                     )

object Updater extends App with StrictLogging {

  class UpdateTask(taskConfig: TaskConfig, dataSource: DataSource) extends Task {
    override def execute(context: TaskExecutionContext): Unit = {
      dataSource.update(taskConfig)
    }
  }

  val sourceTypes = config.getStringList(Constants.CONFIG_SCHEDULE_TASKS).asScala.toList

  def loadTaskConfig(): Map[String, TaskConfig] = {

    def getLocalDateTime(key: String, default: LocalDateTime): LocalDateTime = {
      if (config.hasPath(key)) {
        stringToLocalDateTime(config.getString(key), "yyyyMMdd")
      } else {
        default
      }
    }

    def getBoolean(key: String, default: Boolean): Boolean = {
      if (config.hasPath(key))
        config.getBoolean(key)
      else
        default
    }

    sourceTypes.map(
      (sourceType: String) => (sourceType, TaskConfig(
        config.getString(s"squant.schedule.$sourceType.name"),
        config.getString(s"squant.schedule.$sourceType.cron"),
        clear = getBoolean(s"squant.schedule.$sourceType.clear", false),
        toCSV = getBoolean(s"squant.schedule.$sourceType.toCSV", true),
        toDB = getBoolean(s"squant.schedule.$sourceType.toDB", true),
        start = getLocalDateTime(s"squant.schedule.$sourceType.start", YESTERDAY),
        stop = getLocalDateTime(s"squant.schedule.$sourceType.stop", TODAY)
      ))
    ).toMap
  }

  val tasks = loadTaskConfig()
  val scheduler = new Scheduler()

  for ((name, taskConfig) <- tasks) {
    name match {
      case "daily" => scheduler.schedule(taskConfig.cron, new UpdateTask(taskConfig, DailyKTypeDataSource))
      case "basics" => scheduler.schedule(taskConfig.cron, new UpdateTask(taskConfig, StockBasicsSource))
      case "minute" => scheduler.schedule(taskConfig.cron, new UpdateTask(taskConfig, MinuteKTypeDataSource))
      case "tick" => scheduler.schedule(taskConfig.cron, new UpdateTask(taskConfig, TickDataSource))
      case "moneyflow" => scheduler.schedule(taskConfig.cron, new UpdateTask(taskConfig, MoneyFlowDataSource))
      case "category" => scheduler.schedule(taskConfig.cron, new UpdateTask(taskConfig, StockCategoryDataSource))
    }
  }

  scheduler.start()
  Thread.sleep(1000L * 3600L * 24L * 6L)
  scheduler.stop()
}
