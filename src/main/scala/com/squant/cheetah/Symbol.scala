package com.squant.cheetah

import scala.io.Source

case class Symbol(code: String, //代码
                  name: String, //名称
                  industry: String, //所属行业
                  area: String, //地区
                  pe: Float, //市盈率
                  outstanding: Float, //流通股本
                  totals: Float, //总股本(万)
                  totalAssets: Float, //总资产(万)
                  liquidAssets: Float, //流动资产
                  fixedAssets: Float, //固定资产
                  reserved: Float, //公积金
                  reservedPerShare: Float, //每股公积金
                  esp: Float, //每股收益
                  bvps: Float, //每股净资
                  pb: Float, //市净率
                  timeToMarket: String, //上市日期
                  undp: Float, //未分利润
                  perundp: Float, // 每股未分配
                  rev: Float, //收入同比(%)
                  profit: Float, //利润同比(%)
                  gpr: Float, //毛利率(%)
                  npr: Float, //净利润率(%)
                  holders: Long //股东人数
                 ){
  def getStatus(): Unit ={
    Source.fromURL("http://d.10jqka.com.cn/v2/fiverange/hs_002121/last.js")
  }
}

object Symbol extends App {
  val symbols = parseCSVToSymbols("/data/stocks.csv")
  symbols.filter(_.pe < 10).filter(_.pe > 0).reverse.foreach(println)

}