package com.squant.cheetah.datasource

import java.net.InetSocketAddress
import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

import com.squant.cheetah.domain._
import com.squant.cheetah.engine.DataEngine
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await

import com.squant.cheetah.datasource._
import io.circe.generic.auto._
import io.finch._
import io.finch.circe._

object DataService extends App {

  private val symbols: Endpoint[Seq[Symbol]] =
    get("symbols") {
      Ok(DataEngine.symbols())
    }

  private val category: Endpoint[Map[String, Category]] =
    get("category") {
      Ok(DataEngine.category())
    }

  private val realtime: Endpoint[RealTime] =
    get("realtime" :: string) { code: String =>
      Ok(DataEngine.realtime(code))
    }

  private val tick: Endpoint[Seq[Tick]] =
    get("tick" :: string :: param("date")) { (code: String, date: String) =>
      val dt = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd")).atTime(0, 0)
      Ok(DataEngine.tick(code, dt))
    }

  private val ktype: Endpoint[List[Bar]] =
    get("k" :: string :: param("ktype")) { (code: String, kType: String) =>
      Ok(DataEngine.ktype(code, stringToBarType(kType)))
    }

  private val home: Endpoint[String] =
    get("/") {
      Ok("hello world")
    }

  private val api: Service[Request, Response] = (
    home
//      :+: symbols :+:category :+: realtime :+: tick :+: ktype
    ).toServiceAs[Application.Json]

  private lazy val server = {
    val s = Http.server.serve(new InetSocketAddress(8080), api)
    s
  }

  Await.ready(server)

}
