package com.squant.cheetah

import com.squant.cheetah.datasource.{DailyKTypeDataSource, FinanceDataSource, MinuteKTypeDataSource, MoneyFlowDataSource, StockBasicsSource, StockCategoryDataSource, TaskConfig, TickDataSource}
import com.squant.cheetah.utils._

object Main extends App {

  trait Command {
    def run()
  }

  case class Source(sourceType: String = "", config: TaskConfig = TaskConfig(), isInit: Boolean = false) extends Command {
    override def run(): Unit = {
      sourceType match {
        case "basic" => if (isInit) StockBasicsSource.init() else StockBasicsSource.update(config)
        case "daily" => if (isInit) DailyKTypeDataSource.init() else DailyKTypeDataSource.update(config)
        case "finance" => if (isInit) FinanceDataSource.init() else FinanceDataSource.update(config)
        case "minute" => if (isInit) MinuteKTypeDataSource.init() else MinuteKTypeDataSource.update(config)
        case "moneyflow" => if (isInit) MoneyFlowDataSource.init() else MoneyFlowDataSource.update(config)
        case "category" => if (isInit) StockCategoryDataSource.init() else StockCategoryDataSource.update(config)
        case "tick" => if (isInit) TickDataSource.init() else TickDataSource.update(config)
        case "all" => if (isInit) {
          StockBasicsSource.init()
          DailyKTypeDataSource.init()
          FinanceDataSource.init()
          MinuteKTypeDataSource.init()
          MoneyFlowDataSource.init()
          StockCategoryDataSource.init()
          TickDataSource.init()
        } else {
          StockBasicsSource.update(config)
          DailyKTypeDataSource.update(config)
          FinanceDataSource.update(config)
          MinuteKTypeDataSource.update(config)
          MoneyFlowDataSource.update(config)
          StockCategoryDataSource.update(config)
          TickDataSource.update(config)
        }
//        case _ => throw new NoSuchMethodError()
      }
    }
  }

  var config = TaskConfig(toCSV = true)
  var source = Source()

  val parser = new scopt.OptionParser[Unit]("squant") {
    head("squant", "1.x")

    cmd("source").foreach(_ => Source()).
      text("init or download data source").
      children(
        opt[String]("type").abbr("t").text("set datasource to be downloaded")
            .minOccurs(1).maxOccurs(1)
          .foreach((sType: String) => {
            source = source.copy(sourceType = sType)
          }),
        opt[Boolean]("init").text("init data from source").foreach((value: Boolean) => {
          source = source.copy(isInit = value)
        }),
        opt[String]("start").text("set download start time").foreach((value) =>
          config = config.copy(start = stringToLocalDateTime(value, "yyyyMMdd"))),
        opt[String]("stop").text("set download stop time").foreach((value) =>
          config = config.copy(stop = stringToLocalDateTime(value, "yyyyMMdd"))),
        opt[Boolean]("clear").text("if true,clean data dir before download").foreach((value: Boolean) => {
          config = config.copy(clear = value)
        }),
        opt[Boolean]("toDB").text("if true,save downloaded data to hbase").foreach((value: Boolean) => {
          config = config.copy(toDB = value)
        })
      )
  }

  if (parser.parse(args)) {
    source = source.copy(config = config)
    source.run()
  }
  else {
//    parser.showUsage()
  }
}
