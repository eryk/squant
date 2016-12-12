package com.squant.cheetah.datasource

import com.squant.cheetah.domain._

import scala.sys.process._

class DataBase {

}

object DataBase extends App{
  def symbols:Seq[Symbol] = parseCSVToSymbols("/data/stocks.csv")

  def init() = {
    "python3.5 " + getProjectDir() + "/script/Download.py stocks" !;
    "python3.5 " + getProjectDir() + "/script/Download.py ktype" !;
  }

  init
}
