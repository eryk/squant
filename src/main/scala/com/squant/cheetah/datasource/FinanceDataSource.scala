package com.squant.cheetah.datasource

import java.io._
import java.net.URL
import java.time.LocalDateTime

import com.squant.cheetah.DataEngine
import com.squant.cheetah.domain.Finance
import com.squant.cheetah.utils.Constants._
import com.squant.cheetah.utils._
import com.typesafe.scalalogging.LazyLogging
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell

import scala.io.Source

/**
  * author: eryk
  * mail: xuqi86@gmail.com
  * date: 17-3-28.
  * 财务报表数据
  */
object FinanceDataSource extends DataSource with LazyLogging {

  private val baseDir = config.getString(CONFIG_PATH_DB_BASE)
  private val subDir = config.getString(CONFIG_PATH_FINANCE)

  //主要指标
  private val mainreportURL = "http://basic.10jqka.com.cn/%s/xls/mainreport.xls"
  //资产负债表
  private val debreportURL = "http://basic.10jqka.com.cn/%s/xls/debtreport.xls"
  //利润表
  private val benefitreportURL = "http://basic.10jqka.com.cn/%s/xls/benefitreport.xls"
  //现金流量表
  private val cashreportURL = "http://basic.10jqka.com.cn/%s/xls/cashreport.xls"

  private val reports = Map[String, String](
    ("mainreport.xls", mainreportURL),
    ("debtreport.xls", debreportURL),
    ("benefitreport.xls", benefitreportURL),
    ("cashreport.xls", cashreportURL))

  //初始化数据源
  override def init(config: TaskConfig =
                    TaskConfig("FinanceDataSource",
                      "", true, true, false,
                      LocalDateTime.of(1990, 1, 1, 0, 0), LocalDateTime.now)): Unit = {
    clear()
    update(config)
  }

  //每个周期更新数据
  override def update(config: TaskConfig): Unit = {
    logger.info(s"Start to download stock Finance data, ${format(config.stop, "yyyyMMdd")}")
    val symbols = DataEngine.symbols()
    if (config.clear) clear()
    if (config.toCSV) symbols.par.foreach(symbol => toCSV(symbol.code))
    if (config.toDB) symbols.par.foreach(symbol => toDB(symbol.code))
    logger.info(s"Download completed")
  }

  def toCSV(code: String) = {
    for ((name, url) <- reports) {
      createDir(s"/$baseDir/$subDir/$code/")
      val data = xlsToCSV(url.format(code)).fold("")((left: String, right: String) => left + "\n" + right).drop(1)

      val writer = new FileWriter(new File(s"/$baseDir/$subDir/$code/$name"), false)
      writer.write(data)
      writer.close
    }
  }

  def fromCSV(code: String): Finance = {
    val resultMap: Iterable[(String, Map[String, Map[String, Double]])] =
      for {(name, url) <- reports
           lines = Source.fromFile(new File(s"/$baseDir/$subDir/$code/$name")).getLines().toList
           result = Finance.csvToMap(lines)
      } yield (name, result)
    val maps = resultMap.toMap

    Finance(maps.getOrElse("mainreport.xls", Map[String, Map[String, Double]]()),
      maps.getOrElse("debtreport.xls", Map[String, Map[String, Double]]()),
      maps.getOrElse("benefitreport.xls", Map[String, Map[String, Double]]()),
      maps.getOrElse("cashreport.xls", Map[String, Map[String, Double]]()))
  }

  def toDB(code: String) = {}

  //清空数据源
  override def clear(): Unit = {
    rm(s"/$baseDir/$subDir").foreach(r => logger.info(s"delete ${r._1} ${r._2}"))
  }
}
