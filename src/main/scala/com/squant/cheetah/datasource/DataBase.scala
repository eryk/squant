package com.squant.cheetah.datasource

import com.squant.cheetah.domain._

import scala.sys.process._

class DataBase {
  def init() = {
    "python3.5 " + getProjectDir() + "/script/Download.py" !
  }
}

object DataBase{
  def symbols:Seq[Symbol] = parseCSVToSymbols("/data/stocks.csv")
}
