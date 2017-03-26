package com.squant.cheetah.datasource

import java.time.LocalDateTime

import com.typesafe.scalalogging.StrictLogging
import com.squant.cheetah.utils._
import it.sauronsoftware.cron4j.{Scheduler, Task, TaskExecutionContext}

import scala.collection.JavaConverters._


case class TaskConfig(name: String, cron: String, clear: Boolean, toCSV: Boolean, toDB: Boolean)

object Updater extends App with StrictLogging {

  class UpdateTask(taskConfig: TaskConfig, dataSource: DataSource) extends Task {
    override def execute(context: TaskExecutionContext): Unit = {
      logger.info(s"start to download ${taskConfig.name} data")
      if (taskConfig.clear) dataSource.clear
      dataSource.update(start = LocalDateTime.now().plusDays(-5))
      logger.info(s"Download completed")
    }

  }

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

  try {
    Thread.sleep(1000L * 60L * 10L)
  } catch {
    case e:InterruptedException => e.printStackTrace()
  }
  scheduler.stop()
}
