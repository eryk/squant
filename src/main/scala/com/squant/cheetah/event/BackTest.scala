package com.squant.cheetah.event

import java.time.LocalDateTime

import akka.actor.{Actor, ActorLogging, Props}
import com.squant.cheetah.event.BackTest.Finished
import com.squant.cheetah.strategy.Strategy
import com.squant.cheetah.utils.Constants._
import com.squant.cheetah.utils._
import com.typesafe.scalalogging.StrictLogging

sealed trait Interval

case class SECOND(unit: Int) extends Interval

case class DAY(unit: Int) extends Interval

case class MINUTE(unit: Int) extends Interval

object BackTest {

  def props(strategy: Strategy): Props =
    Props(new BackTest(strategy))

  case object Finished

}

class BackTest(strategy: Strategy) extends Actor with StrictLogging {

  override def receive: Receive = {
    case TimeEvent(date) => {
      if (isTradingTime(date)) {
        strategy.handle()
      }
      if (!strategy.getContext.clock.isFinished()) {
        strategy.getContext.clock.update()
        self ! TimeEvent(strategy.getContext.currentDate())
      } else {
        self ! Finished
      }
    }
    case Finished => {

      def savePath = s"${config.getString(CONFIG_PATH_DB_BASE)}" +
        s"/backtest/${strategy.getContext.name}-${format(LocalDateTime.now(), "yyyyMMdd_HHmmss")}.xls"

      //      context stop self
      ExcelUtils.export(strategy.getContext, savePath)
      context.system.terminate
    }
    case _ => println("unknow event type")
  }
}
