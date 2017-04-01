package com.squant.cheetah

import com.squant.cheetah.datasource.{DailyKTypeDataSource, FinanceDataSource, MinuteKTypeDataSource, MoneyFlowDataSource, StockBasicsSource, StockCategoryDataSource, TaskConfig, TickDataSource}
import com.squant.cheetah.utils._

object Main extends App {

  sealed trait Command {
    def run()
  }

  case object DummyCommand extends Command {
    override def run(): Unit = ???
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
        case _ => throw new NoSuchMethodError()
      }
    }
  }

  var config = TaskConfig(clear = true, toCSV = true, toDB = false)
  var source = Source(isInit = false)

  val sep = "\n\t\t\t\t\t\t\t"

  val parser = new scopt.OptionParser[Command]("squant") {
    head("squant", "1.x")
    help("help").abbr("h").text("display this help and exit")

    cmd("source").action((_, c) => Source()).
      text("init or download data source").
      children(
        opt[String]("type").abbr("t").text(
          s"set datasource to be downloaded,${sep}support: " +
            s"${sep}\tall: all kind of datasource," +
            s"${sep}\tbasic: stock list with basic info," +
            s"${sep}\tcategory: stock category data," +
            s"${sep}\tdaily: stock day bar," +
            s"${sep}\tfinance: Financial Statements," +
            s"${sep}\tminute: stock minute bar,include 5/15/30/60 minutes," +
            s"${sep}\tmoneyflow: stock money flow data," +
            s"${sep}\ttick: stock tick data")
          .minOccurs(1).maxOccurs(1)
          .action({
            case (s: String, c: Source) => c.copy(sourceType = s)
            case _ => throw new IllegalStateException
          }),
        opt[Boolean]("init").text("if true,init data from source, default:false").action({
          case (value: Boolean, c: Source) => {
            c.copy(isInit = value)
          }
          case _ => throw new IllegalStateException
        }),
        opt[String]("start").text("format:yyyyMMdd, set download start time").action({
          case (value: String, c: Source) => {
            config = config.copy(start = stringToLocalDateTime(value, "yyyyMMdd"))
            c.copy(config = config)
          }
          case _ => throw new IllegalStateException
        }),
        opt[String]("stop").text("format:yyyyMMdd, set download stop time").action({
          case (value: String, c: Source) => {
            config = config.copy(stop = stringToLocalDateTime(value, "yyyyMMdd"))
            c.copy(config = config)
          }
          case _ => throw new IllegalStateException
        }),
        opt[Boolean]("clear").text("if true,clean data dir before download, default:true").action({
          case (value: Boolean, c: Source) => {
            config = config.copy(clear = value)
            c.copy(config = config)
          }
        }),
        opt[Boolean]("toDB").hidden().text("if true,save downloaded data to hbase, default:false").action({
          case (value: Boolean, c: Source) => {
            config = config.copy(toDB = value)
            c.copy(config = config)
          }
        })
      )
    cmd("test")
    cmd("run")
  }

  parser.parse(args, DummyCommand) match {
    case Some(DummyCommand) | None => {
      parser.showUsage()
      System.exit(-1)
    }
    case Some(c) => c.run()
  }
}
