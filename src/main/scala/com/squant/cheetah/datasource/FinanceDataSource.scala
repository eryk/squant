package com.squant.cheetah.datasource

import java.io._
import java.net.{HttpURLConnection, URL}

import com.github.tototoshi.csv.CSVReader
import com.squant.cheetah.utils.Constants._
import com.squant.cheetah.utils._
import com.typesafe.scalalogging.LazyLogging
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.util.WorkbookUtil

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
  override def init(config: TaskConfig): Unit = {
    clear()
    update(config)
  }

  //每个周期更新数据
  override def update(config: TaskConfig): Unit = {

  }

  def toCSV(code: String) = {

    def readInputStream(inputStream: InputStream): Array[Byte] = {
      var buffer = new Array[Byte](1024)
      var len = 0
      val bos = new ByteArrayOutputStream()
      len = inputStream.read(buffer)
      while (len != -1) {
        bos.write(buffer, 0, len)
        len = inputStream.read(buffer)
      }
      bos.close()
      bos.toByteArray()
    }

    for ((name, url) <- reports) {
      createDir(s"/$baseDir/$subDir/$code/")

      val path = new URL(url.format(code))
      val conn = path.openConnection()
      conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)")
      val inputStream = conn.getInputStream()
      val wb = new HSSFWorkbook(inputStream)
      val sheet = wb.getSheetAt(0)
      println(sheet.rowIterator().next().cellIterator().next().getStringCellValue)

      val writer = new FileWriter(new File(s"/$baseDir/$subDir/$code/$name"), false)
      //读取行和列
      val iter = sheet.rowIterator()
      while (iter.hasNext) {
        val sourceRow = iter.next()
        var content = ""
        val cellIter = sourceRow.cellIterator()
        while (cellIter.hasNext) {
          val sourceCell = cellIter.next
          println(sourceCell)
          content += sourceCell + ","
        }
        content += "\r\n"; //换行
        writer.write(content); //写入文件
      }
      if (writer != null) {
        //关闭文件流
        writer.close()
      }
      System.out.println("----------  export  xls 2 csv/txt  文件已转换   -----------");

    }
  }

  //      val data = Source.fromURL(url.format(code),"utf8").mkString
  //      val writer = new FileWriter(new File(s"/$baseDir/$subDir/$code/$name"), false)
  //      writer.write(data)
  //      writer.close()

  //清空数据源
  override def clear(): Unit = {
    rm(s"/$baseDir/$subDir").foreach(r => logger.info(s"delete ${r._1} ${r._2}"))
  }
}
