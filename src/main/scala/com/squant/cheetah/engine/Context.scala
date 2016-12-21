package com.squant.cheetah.engine

import com.squant.cheetah.trade.Portfolio

import com.squant.cheetah.domain._

//策略的上下文环境
class Context() {

  val name: String = "cheetah"

  def clock: Clock = ???

  def portfolio: Portfolio = ???

  def broker: Broker = ???

  def symbols: Seq[Symbol] = ???
}

object Context {
  def buildFrom(file: String): Context = ???
}
