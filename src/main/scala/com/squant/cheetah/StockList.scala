package com.squant.cheetah

import java.io.File

import scala.collection.breakOut

import com.squant.cheetah._

object StockList extends App {

  val lines = scala.io.Source.fromFile(new File("/home/eryk/IdeaProjects/squant/data/stocks.csv")).getLines().toList

  val symbolList = for {
    line <- lines.slice(1,lines.length + 1)
    fields = line.split(",")
    if (fields.length == 23)
    map = (lines(0).split(",") zip fields) (breakOut): Map[String, String]
  } yield mapToSymbol(map)

  symbolList.foreach(println)

  println(symbolList.size)
}
