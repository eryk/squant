package com.squant.cheetah.datasource

import java.io.{File, FileWriter}
import java.nio.file.Files
import java.time.LocalDateTime

import com.squant.cheetah.domain._
import com.squant.cheetah.engine.DataBase
import com.squant.cheetah.utils.Constants._
import com.squant.cheetah.utils._
import com.typesafe.scalalogging.LazyLogging

import scala.io.Source

object StockBasicsSource extends DataSource with LazyLogging {
  private val url = "http://218.244.146.57/static/all.csv"

  private val path = config.getString(CONFIG_PATH_DB_BASE)
  private val name = config.getString(CONFIG_PATH_STOCKS)
  private val tableName = "stock_tables"

  //初始化数据源
  override def init(): Unit = {
    clear()
    update()
  }

  //每个周期更新数据
  override def update(start: LocalDateTime = LocalDateTime.now(),
                      stop: LocalDateTime = LocalDateTime.now()): Unit = {
    logger.info(s"Start to download stock basics data, ${format(stop,"yyyyMMdd")}")
    toCSV(stop)
    toDB(tableName)
    logger.info(s"Download completed")
  }

  def toCSV(date: LocalDateTime): Unit = {
    val content = Source.fromURL(url, "gbk").mkString
    val sourceFile = new File(s"$path/$name")
    val writer = new FileWriter(sourceFile, false)
    writer.write(content)
    writer.close()

    if (!sourceFile.exists()) {
      logger.error("fail to download stock basics data.")
      return
    }

    val dest = new File(s"$path/$name-${format(date, "yyyyMMdd")}")
    if (dest.exists()) {
      dest.delete()
    }
    Files.copy(sourceFile.toPath, dest.toPath)
  }

  def toDB(tableName: String): Unit = {
    val symbols = Symbol.csvToSymbols(s"$path/$name").map(symbol => Symbol.symbolToRow(symbol)).toList
    DataBase.getEngine.toDB(tableName, symbols)
  }

  def fromDB(tableName: String = tableName): Seq[Symbol] = {
    val rows = DataBase.getEngine.fromDB(tableName, start = LocalDateTime.now().plusDays(-2), stop = LocalDateTime.now())
    rows.map(Symbol.rowToSymbol)
  }

  //清空数据源
  override def clear(): Unit = {
    rm(s"$path/$name")
    DataBase.getEngine.deleteTable(tableName)
  }
}
