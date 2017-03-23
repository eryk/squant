package com.squant.cheetah.datasource

import java.time.LocalDateTime

import com.typesafe.scalalogging.StrictLogging
import com.squant.cheetah.utils._
import cronish._
import cronish.dsl._
import scala.collection.JavaConverters._


case class TaskConfig(name: String, cron: String, clear: Boolean, toCSV: Boolean, toDB: Boolean)

object Updater extends App with StrictLogging {
  val sourceTypes = config.getStringList(Constants.CONFIG_SCHEDULE_TASKS).asScala.toList

  def loadTaskConfig(): Map[String, TaskConfig] = {
    sourceTypes.map(
      (sourceType: String) => (sourceType, TaskConfig(
        config.getString(s"squant.schedule.$sourceType.name"),
        config.getString(s"squant.schedule.$sourceType.cron"),
        config.getBoolean(s"squant.schedule.$sourceType.clear"),
        config.getBoolean(s"squant.schedule.$sourceType.toCSV"),
        config.getBoolean(s"squant.schedule.$sourceType.toDB"))
        )
    ).toMap
  }

  def runTask(taskConfig: TaskConfig, dataSource: DataSource) = {
    logger.info(s"start to download ${taskConfig.name} data")
    task {
      dataSource.update(start = LocalDateTime.now().plusDays(-5))
    } executes taskConfig.cron.cron
    logger.info(s"Download completed")
  }

  val tasks = loadTaskConfig()

  for ((name, taskConfig) <- tasks) {
    name match {
      case "daily" => runTask(taskConfig, DailyKTypeDataSource)
      case "basics" => runTask(taskConfig, StockBasicsSource)
      case "minute" => runTask(taskConfig, MinuteKTypeDataSource)
      case "tick" => runTask(taskConfig, TickDataSource)
      case "moneyflow" => runTask(taskConfig, MoneyFlowDataSource)
      case "category" => runTask(taskConfig, StockCategoryDataSource)
    }
  }
}
