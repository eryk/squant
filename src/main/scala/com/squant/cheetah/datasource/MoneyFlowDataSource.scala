package com.squant.cheetah.datasource

import java.io.{File, FileWriter}
import java.time.LocalDateTime
import java.util.Random

import com.google.gson.Gson
import com.squant.cheetah.domain.MoneyFlow
import com.squant.cheetah.engine.{DataBase, Row}
import com.squant.cheetah.utils.Constants._
import com.squant.cheetah.utils._
import com.typesafe.scalalogging.LazyLogging

import scala.io.Source
import scala.collection.JavaConverters._

object MoneyFlowDataSource extends DataSource with LazyLogging {

  private val baseDir = config.getString(CONFIG_PATH_DB_BASE)
  private val moneyflowDir = config.getString(CONFIG_PATH_MONEYFLOW)

  //http://data.eastmoney.com/bkzj/hy.html
  private val moneyFlowIndustryHisURL: String = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?cmd=C._BKHY&type=ct&st=(BalFlowMain)&&token=894050c76af8597a853f5b408b759f5d&sty=DCFFITABK&rt=%s"
  private val moneyFlowIndustry5DayHisURL: String = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?cmd=C._BKHY&type=ct&st=(BalFlowMainNet5)&token=894050c76af8597a853f5b408b759f5d&sty=DCFFITABK5&rt=%s"
  private val moneyFlowIndustry10DayHisURL: String = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?cmd=C._BKHY&type=ct&st=(BalFlowMainNet10)&token=894050c76af8597a853f5b408b759f5d&sty=DCFFITABK10&rt=%s"
  //http://data.eastmoney.com/bkzj/gn.html
  private val moneyFlowConceptHisURL: String = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?cmd=C._BKGN&type=ct&st=(BalFlowMain)&sr=-1&p=1&ps=500&token=894050c76af8597a853f5b408b759f5d&sty=DCFFITABK&rt=%s"
  private val moneyFlowConcept5DayHisURL: String = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?cmd=C._BKGN&type=ct&st=(BalFlowMainNet5)&sr=-1&p=1&ps=500&token=894050c76af8597a853f5b408b759f5d&sty=DCFFITABK5&rt=%s"
  private val moneyFlowConcept10DayHisURL: String = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?cmd=C._BKGN&type=ct&st=(BalFlowMainNet10)&sr=-1&p=1&ps=500&token=894050c76af8597a853f5b408b759f5d&sty=DCFFITABK10&rt=%s"
  //http://data.eastmoney.com/bkzj/dy.html
  private val moneyFlowRegionHisURL: String = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?cmd=C._BKDY&type=ct&st=(BalFlowMain)&sr=-1&p=1&ps=50&token=894050c76af8597a853f5b408b759f5d&sty=DCFFITABK&rt=%s"
  private val moneyFlowRegion5DayHisURL: String = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?cmd=C._BKDY&type=ct&st=(BalFlowMainNet5)&sr=-1&p=1&ps=50&&token=894050c76af8597a853f5b408b759f5d&sty=DCFFITABK5&rt=%s"
  private val moneyFlowRegion10DayHisURL: String = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?cmd=C._BKDY&type=ct&st=(BalFlowMainNet10)&sr=-1&p=1&ps=50&&token=894050c76af8597a853f5b408b759f5d&sty=DCFFITABK10&rt=%s"

  private val today = List(moneyFlowIndustryHisURL, moneyFlowConceptHisURL, moneyFlowRegionHisURL)
  private val fiveDay = List(moneyFlowIndustry5DayHisURL, moneyFlowConcept5DayHisURL, moneyFlowRegion5DayHisURL)
  private val tenDay = List(moneyFlowIndustry10DayHisURL, moneyFlowConcept10DayHisURL, moneyFlowRegion10DayHisURL)

  private val INDUSTRY_MONEYFLOW_COLUMNS = List(
    "num", //0     序号
    "block_code", //1     板块代码
    "name", //2     板块名称
    "change", //3     涨跌幅
    "主力净流入-净额", //4     主力净流入-净额(万)
    "主力净流入-净占比", //5     主力净流入-净占比
    "超大单净流入-净额", //6     超大单净流入-净额
    "超大单净流入-净占比", //7     超大单净流入-净占比
    "大单净流入-净额", //8     大单净流入-净额
    "大单净流入-净占比", //9     大单净流入-净占比
    "中单净流入-净额", //10     中单净流入-净额
    "中单净流入-净占比", //11    中单净流入-净占比
    "小单净流入-净额", //12    小单净流入-净额
    "小单净流入-净占比", //13     小单净流入-净占比
    "name", //14     股票名称
    "code" //15     股票代码
  )

  //初始化数据源
  override def init(): Unit = {
    clear()
    update()
  }

  //每个周期更新数据
  override def update(start: LocalDateTime = LocalDateTime.now(), stop: LocalDateTime = LocalDateTime.now()): Unit = {
    val types = List("today", "5_day", "10_day")

    val dir = new File(s"$baseDir/$moneyflowDir/${format(stop, "yyyyMMdd")}")
    if (!dir.exists()) {
      dir.mkdirs()
    }

    /**
      * @param `type` 0: history, 1:5day , 2:10day
      * @return
      */
    def collect(`type`: Int): List[String] = {
      var url: String = null
      val random: Random = new Random
      val i: Int = random.nextInt(99999999)
      if (`type` == 1) {
        ;
        url = fiveDay(`type`).format(i)
      }
      else if (`type` == 2) {
        url = tenDay(`type`).format(i)
      }
      else {
        url = today(`type`).format(i)
      }
      val data: String = Source.fromURL(url, "utf8").mkString
      val s: String = data.substring(1, data.length - 1)
      val gson: Gson = new Gson
      val list: java.util.List[String] = gson.fromJson(s, classOf[java.util.List[String]])
      return list.asScala.toList
    }

    for (path <- types) {
      toCSV("Industry_" + path, stop, collect(0))
      toCSV("Concept_" + path, stop, collect(1))
      toCSV("Region_" + path, stop, collect(2))
    }
  }

  def toCSV(path: String, dateTime: LocalDateTime, data: List[String]) = {
    val file = new File(s"$baseDir/$moneyflowDir/${format(dateTime, "yyyyMMdd")}/$path.csv")
    val writer = new FileWriter(file, false)

    writer.write(INDUSTRY_MONEYFLOW_COLUMNS.foldLeft[String]("")((x: String, y: String) => x + "," + y).drop(1) + "\n")

    for (line <- data) {
      writer.write(new String(line.getBytes("utf-8")) + "\n");
    }
    writer.close()
  }

  def fromCSV(path: String, dateTime: LocalDateTime = LocalDateTime.now()): List[MoneyFlow] = {
    val data = Source.fromFile(new File(s"$baseDir/$moneyflowDir/${format(dateTime, "yyyyMMdd")}/$path.csv")).getLines()
    val list = data.drop(1).map(item => MoneyFlow.csvToMoneyFlow(item) match {
      case Some(flow) => flow
      case None => throw new UnknownError("fail to parse moneyflow data")
    }
    )
    list.toList
  }

  def toDB(tableName: String, engine: DataBase, data: List[MoneyFlow]): Unit = {
    val rows: List[Row] = data.map(MoneyFlow.moneyflowToRow)
    engine.toDB(tableName, rows)
  }

  def fromDB(tableName: String, engine: DataBase, start: LocalDateTime,
             stop: LocalDateTime): List[MoneyFlow] = {
    val rowList = engine.fromDB(tableName, start, stop)
    rowList.map(MoneyFlow.rowToMoneyFlow)
  }

  //清空数据源
  override def clear(): Unit = {
    rm(s"/$baseDir/$moneyflowDir").foreach(r => logger.info(s"delete ${r._1} ${r._2}"))
  }
}
