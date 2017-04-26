package com.squant.cheetah.utils

import java.text.SimpleDateFormat
import java.util.Date

import com.squant.cheetah.domain.Position
import com.squant.cheetah.engine.Context
import com.squant.cheetah.event.{OrderRecord, Portfolio}
import info.folone.scala.poi.{Cell, NumericCell, Row, Sheet, StringCell, Workbook}
import com.squant.cheetah.utils.Constants._
import org.apache.poi.hssf.usermodel.{HSSFSheet, HSSFWorkbook}

import scala.collection.mutable

/**
  * author: eryk
  * mail: xuqi86@gmail.com
  * date: 17-4-4.
  */
object ExcelUtils extends App {

  def importPortfolio(path: String): Portfolio = ???

  def export(context: Context, path: String) = {
    createDir(s"${config.getString(CONFIG_PATH_DB_BASE)}/backtest/")

    val p = context.portfolio

    val pos: List[Set[StringCell]] = p.positions.map(item => {
      val position = item._2
      Set(StringCell(0, position.name),
        StringCell(1, position.code),
        StringCell(2, format(position.initTime, "yyyyMMdd HH:mm:ss")),
        StringCell(3, position.totalAmount.toString),
        StringCell(4, position.closeableAmount.toString),
        StringCell(5, position.todayAmount.toString),
        StringCell(6, position.sellAmount.toString),
        StringCell(7, position.avgCost.toString),
        StringCell(8, position.close.toString),
        StringCell(9, position.floatPnl.toString),
        StringCell(10, position.pnlRatio.toString),
        StringCell(11, position.latestValue.toString)
      )
    }).toList

    val postionRows: mutable.Buffer[Row] = scala.collection.mutable.Buffer()
    postionRows.append(Row(0) {
      Set(StringCell(0, "证券名称"),
        StringCell(1, "证券代码"),
        StringCell(2, "更新时间戳"),
        StringCell(3, "证券数量"),
        StringCell(4, "可卖数量"),
        StringCell(5, "今买数量"),
        StringCell(6, "今卖数量"),
        StringCell(7, "成本价"),
        StringCell(8, "当前价"),
        StringCell(9, "浮动盈亏"),
        StringCell(10, "盈亏比例"),
        StringCell(11, "最新市值")
      )
    })
    for (i <- 0 to pos.size - 1) {
      postionRows.append(Row(i + 1) {
        pos(i).toSet[Cell]
      })
    }

    val records = p.records.map { record: OrderRecord => {
      Set(StringCell(0, record.code),
        StringCell(1, record.direction.toString),
        StringCell(2, record.amount.toString),
        StringCell(3, record.price.toString),
        StringCell(4, record.volume.toString),
        StringCell(5, record.cost.toString),
        StringCell(6, format(record.ts, "yyyyMMdd HH:mm:ss"))
      )
    }
    }

    val recordRows: mutable.Buffer[Row] = scala.collection.mutable.Buffer()
    recordRows.append(Row(0) {
      Set(StringCell(0, "证券名称"),
        StringCell(1, "买卖方向"),
        StringCell(2, "证券数量"),
        StringCell(3, "价格"),
        StringCell(4, "当日买入卖出额"),
        StringCell(5, "交易费用"),
        StringCell(6, "更新时间戳")
      )
    })
    for (i <- 0 to records.size - 1) {
      recordRows.append(Row(i + 1) {
        records(i).toSet[Cell]
      })
    }

    Workbook {
      Set(Sheet("summary") {
        Set(Row(0) {
          Set(StringCell(0, "起始资金"), NumericCell(1, context.startCash))
        },
          Row(1) {
            Set(StringCell(0, "期末资产"), NumericCell(1, context.startCash))
          },
          Row(2) {
            Set(StringCell(0, "当前可用资金"), NumericCell(1, p.availableCash))
          }
        )
      },
        Sheet("position") {
          postionRows.toSet
        },
        Sheet("record") {
          recordRows.toSet
        }
      )
    }.safeToFile(path).fold(ex ⇒ throw ex, identity).unsafePerformIO
  }

  val contexts = loadContext(config.getString(CONFIG_PATH_STRATEGY))
  val context = contexts("default")

  export(context, "/home/eryk/test.xls")
}
